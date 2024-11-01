import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, Subject, first, map, switchMap, tap, throwError, concatWith } from 'rxjs';
import { IRegister } from '../../models/authentication/IRegister';
import { IToken } from '../../models/authentication/IToken';
import { environment } from '../../environments/environment.development';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { jwtDecode } from 'jwt-decode';
import { ILogin } from '../../models/authentication/ILogin';
import { IJwtToken } from '../../models/authentication/IJwtToken';


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private controllerPath = 'auth';

  token = new BehaviorSubject<{ tokenAccess: string; tokenRefresh: string } | null>(this.loadTokens());
  loggedUser = new BehaviorSubject<IJwtToken | undefined>(this.decodeToken());
  authHasChanged = new Subject();

  constructor(
    private http: HttpClient,
    private router: Router,
   
  ) { }

  private loadTokens() {
    try {
      const tokenAccess = localStorage.getItem('access_token') ?? '';
      const tokenRefresh = localStorage.getItem('refresh_token') ?? '';
      return tokenAccess && tokenRefresh ? { tokenAccess, tokenRefresh } : null;
    } catch (e) {
      console.error('LocalStorage access denied:', e);
      return null;
    }
  }

  private saveTokens(accessToken: string, refreshToken: string) {
    try {
      localStorage.setItem('access_token', accessToken);
      localStorage.setItem('refresh_token', refreshToken);
      this.token.next({ tokenAccess: accessToken, tokenRefresh: refreshToken });
    } catch (e) {
      console.error('Failed to save tokens to LocalStorage:', e);
    }
  }

  private clearTokens() {
    try {
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      this.token.next(null);
    } catch (e) {
      console.error('Failed to clear tokens from LocalStorage:', e);
    }
  }

  public decodeToken(): IJwtToken | undefined {
    const tokenAccess = this.token.value?.tokenAccess;
    return tokenAccess ? jwtDecode<IJwtToken>(tokenAccess) : undefined;
  }

  register(user: IRegister): Observable<any> {
    console.log('registering user', user);
    return this.http.post(`${environment.apiUrl}/${this.controllerPath}/register`, user)
      .pipe(
        tap(() => { this.router.navigate(['login']); }),
        
  )
  }

  public logout(): void {

    //narazie tylko na froncie bo brak endpointu na backendzie
    this.clearTokens();
    this.router.navigate(['/login']);

    // const url = `${environment.apiUrl}/${this.controllerPath}/logOut`
    // this.http.delete(url).subscribe(() => {
    //   console.log('logged out');
    //   this.token.next('');
    //   this.loggedUser.next(undefined);

    //   localStorage.removeItem('access_token');
    //   localStorage.removeItem('refresh_token');
    //   this.router.navigate(['/login']);
    // })

  }

  public login(body: ILogin): Observable<any> {
    const url = `${environment.apiUrl}/${this.controllerPath}/login`;

    return this.http.post(url, body).pipe(
      tap((res: any) => {
        if (res.tokenAccess && res.tokenRefresh) {
          this.saveTokens(res.tokenAccess, res.tokenRefresh);

          const decodedToken: IJwtToken = jwtDecode<IJwtToken>(res.tokenAccess);
          console.log("Decoded token:", decodedToken);

          this.loggedUser.next(decodedToken); // Przechowaj dane użytkownika, w tym rolę

          this.router.navigate(['USER/afterLogin']);
        } else {
          console.error("No tokens in response", res);
        }
      })
    );
  }

  getTokenData(): Observable<{ tokenAccess: string; tokenRefresh: string } | null> {
    return this.token.asObservable();
  }

  refreshJWT(): Observable<IToken> {
    const refreshToken = localStorage.getItem('refresh_token'); // Pobierz refresh token z lokalnego magazynu
    const refreshTokenUrl = `${environment.apiUrl}/${this.controllerPath}/refreshToken`;
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    const body = {
      refreshToken: refreshToken // Dodaj refresh token do ciała zapytania
    };

    return this.http.post<IToken>(refreshTokenUrl, body, { headers })
      .pipe(
        tap((response: IToken) => {
          if (response) {
            localStorage.setItem('refresh_token', response.tokenRefresh);
            localStorage.setItem('access_token', response.tokenAccess);
            this.token.next({ tokenAccess: response.tokenAccess, tokenRefresh: response.tokenRefresh });
          }
        })
      );
  }
}
