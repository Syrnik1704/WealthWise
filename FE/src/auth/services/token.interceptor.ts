import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Observable, of, throwError, iif } from 'rxjs';
import { catchError, switchMap, filter, first, map, skip } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { environment } from '../../app/environments/environment.development';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  noTokenUrls = [`${environment.apiUrl}/auth/login`, `${environment.apiUrl}/auth/refreshToken`, `${environment.apiUrl}/auth/register`];

  constructor(private auth: AuthService) {}

  refreshTokenUrl = `${environment.apiUrl}/auth/refreshToken`;
  refreshTokenInProgress = false;

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (request.url === this.refreshTokenUrl) {
      // Nie przechwytuj żądania odświeżenia tokena
      return next.handle(request);
    }
    return this.sendRequest(request, next)
      .pipe(
        catchError((httpError: HttpErrorResponse) =>
          httpError?.status === 401 && !this.noTokenUrls.some(el => el === request.url)
            ? this.handleTokenRefresh(request, next)
            : throwError(() => httpError.error)
        )
      );
  }
  // intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  //   console.log('Interceptor called'); // Dodaj logowanie

  //   const token = localStorage.getItem('access_token'); // Pobierz token z lokalnego magazynu

  //   // Klonowanie żądania i dodawanie nagłówków
  //   const clonedRequest = req.clone({
  //     setHeaders: {
  //       Authorization: `Bearer ${token}`,
  //       'Content-Type': 'application/json'
  //     }
  //   });
  //   // Przekazanie zmodyfikowanego żądania dalej
  //   return next.handle(clonedRequest);
  // }

  private sendRequest(request: HttpRequest<any>, next: HttpHandler) {
    return this.waitForNewTokenIfNeeded(request)
      .pipe(
        switchMap(() => this.addTokenToRequestIfNeeded(request)),
        switchMap(requestWithToken => next.handle(requestWithToken)),
        filter(res => res instanceof HttpResponse)
      );
  }

  private waitForNewTokenIfNeeded(request: HttpRequest<any>) {
    const shouldWaitForNewToken = () => this.refreshTokenInProgress && request.url !== this.refreshTokenUrl;
    return iif(
      shouldWaitForNewToken,
      this.waitForNewToken(),
      of(null)
    );
  }

  private addTokenToRequestIfNeeded(request: HttpRequest<any>) {
    return this.noTokenUrls.some(el => el === request.url)
      ? of(request)
      : this.auth.getTokenData().pipe(
          first(),
          map(tokenData => {
            if (!tokenData?.tokenAccess) {
              return request;
            } else {
              const headers = request.headers.set('Authorization', `Bearer ${tokenData.tokenAccess}`);
              return request.clone({ headers });
            }
          })
        );
  }

  private handleTokenRefresh(request: HttpRequest<any>, next: HttpHandler) {
    return this.postTokenRefresh().pipe(
      switchMap(() => this.addTokenToRequestIfNeeded(request)),
      switchMap(requestWithToken => next.handle(requestWithToken))
    );
  }

  private postTokenRefresh() {
    this.refreshTokenInProgress = true;
    return this.auth.refreshJWT().pipe(
      first(),
      catchError(error => {
        this.refreshTokenInProgress = false;
        return throwError(() => error);
      }),
      switchMap(() => {
        this.refreshTokenInProgress = false;
        return of(null);
      })
    );
  }

  private waitForNewToken() {
    return this.auth.getTokenData().pipe(
      skip(1),
      first()
    );
  }
}