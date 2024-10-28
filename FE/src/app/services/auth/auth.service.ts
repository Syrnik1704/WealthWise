import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { IRegister } from '../../models/authentication/IRegister';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  register(user: IRegister): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/register`, user);
  }
}
