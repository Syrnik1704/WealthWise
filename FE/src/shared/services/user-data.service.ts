import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserDataService {
  private readonly http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/user`;

  public getUserData(): Observable<any> {
    return this.http.get(`${this.baseUrl}/getUserData`);
  }
}
