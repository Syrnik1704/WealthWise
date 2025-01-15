import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { environment } from '../../app/environments/environment.development';

@Injectable({ providedIn: 'root' })
export class UserApiService {
  private controllerPath = 'user';

  constructor(
    private http: HttpClient,
  ) {}

  loadBalance(): Observable<number> {
    return this.http.get<number>(`${environment.apiUrl}/${this.controllerPath}/balance`);
  }
}
