import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { environment } from '../../environments/environment.development';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AfterLoginService {

  constructor(private http: HttpClient) { }

 getData(): Observable<any[]> {

  const url = `${environment.apiUrl}/asset/getAsset`

  return this.http.get<any[]>(url);

}
}
