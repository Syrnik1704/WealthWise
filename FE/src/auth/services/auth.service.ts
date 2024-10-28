import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { LoginRequest } from '../../login';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly httpClient = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  public login(request: LoginRequest): Observable<void> {
    return this.httpClient.post<void>(this.baseUrl, request).pipe(tap(console.log));
  }
}
