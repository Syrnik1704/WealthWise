import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngxs/store';
import { jwtDecode } from 'jwt-decode';
import { BehaviorSubject, finalize, Observable, Subject, tap } from 'rxjs';
import { AddUser, RemoveUserData, User } from '../../../shared';
import { environment } from '../../environments/environment.development';
import { IJwtToken } from '../../models/authentication/IJwtToken';
import { ILogin } from '../../models/authentication/ILogin';
import { IRegister } from '../../models/authentication/IRegister';
import { IToken } from '../../models/authentication/IToken';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private controllerPath = 'auth';

  token = new BehaviorSubject<{ tokenAccess: string; tokenRefresh: string } | null>(
    this.loadTokens()
  );
  authHasChanged = new Subject();
  private readonly store = inject(Store);

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  public initAuthState(): void {
    this.saveUser();
  }

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

  private saveUser(): void {
    if (!this.token.value) {
      return;
    }
    const decodedToken: IJwtToken = jwtDecode<IJwtToken>(this.token.value.tokenAccess);
    const user: User = {
      name: decodedToken.name,
      email: decodedToken.email,
      role: decodedToken.role,
    };
    this.store.dispatch(new AddUser(user));
  }

  register(user: IRegister): Observable<any> {
    return this.http.post(`${environment.apiUrl}/${this.controllerPath}/register`, user).pipe(
      tap(() => {
        this.router.navigate(['login']);
      })
    );
  }

  public logout(): void {
    const url = `${environment.apiUrl}/${this.controllerPath}/logout`;
    this.http
      .post(url, {})
      .pipe(
        finalize(() => {
          this.clearTokens();
          this.store.dispatch(new RemoveUserData());
          this.router.navigate(['']);
        })
      )
      .subscribe();
  }

  public login(body: ILogin): Observable<{ tokenAccess: string; tokenRefresh: string }> {
    const url = `${environment.apiUrl}/${this.controllerPath}/login`;

    return this.http.post<{ tokenAccess: string; tokenRefresh: string }>(url, body).pipe(
      tap(res => {
        if (res.tokenAccess && res.tokenRefresh) {
          this.saveTokens(res.tokenAccess, res.tokenRefresh);
          this.saveUser();
          this.router.navigate(['']);
        } else {
          console.error('No tokens in response', res);
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
      'Content-Type': 'application/json',
    });

    const body = {
      refreshToken: refreshToken, // Dodaj refresh token do ciała zapytania
    };

    return this.http.post<IToken>(refreshTokenUrl, body, { headers }).pipe(
      tap((response: IToken) => {
        if (response) {
          localStorage.setItem('refresh_token', response.tokenRefresh);
          localStorage.setItem('access_token', response.tokenAccess);
          this.token.next({
            tokenAccess: response.tokenAccess,
            tokenRefresh: response.tokenRefresh,
          });
        }
      })
    );
  }
}
