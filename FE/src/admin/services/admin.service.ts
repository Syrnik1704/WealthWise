import { inject, Injectable } from '@angular/core';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { UserData } from '../models/usersData';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { UserEmails } from '../models/userEmails';
import { AddCategory } from '../models/addCategory';
import { environment } from '../../app/environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private url = `${environment.apiUrl}/admin`;
  private readonly httpClient = inject(HttpClient);
  constructor(private http: HttpClient) { }

  public getUsers(): Observable<UserData[]> {
    return this.http.get<UserData[]>(`${this.url}/getUsers`).pipe(
      map((response) => response),
      catchError((error: HttpErrorResponse) => {
        return throwError(() => error);
      })
    );
  }

  public deleteUser(email: UserEmails): Observable<void> {
    return this.http.delete<void>(`${this.url}/delete`, { body: email }).pipe(
      catchError((error) => {
        console.error('Error deleting user:', error);
        return throwError(() => new Error('Failed to delete user'));
      })
    );
  }

  public updateUser(user: UserData): Observable<void> {
    return this.http.put<void>(`${this.url}/modifyUserData`, { user }).pipe(
      catchError((error) => {
        console.error('Error updating user:', error);
        return throwError(() => new Error('Failed to update user'));
      })
    );
  }

  public blockUser(email: UserEmails): Observable<boolean> {
    return this.http.put<boolean>(`${this.url}/block`, email).pipe(
      map(() => {
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        console.log("error: " + error);
        return of(false);
      })
    );
  }

  public unblockUser(email: UserEmails): Observable<boolean> {
    return this.http.put<boolean>(`${this.url}/unblock`, email).pipe(
      map(() => {
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        console.log("error: " + error);
        return of(false);
      })
    );
  }

  public addCategory(category: AddCategory): Observable<string> {
    return this.httpClient.post(`${this.url}/addCategory`, category, { responseType: 'text' }).pipe(
      catchError((error: HttpErrorResponse) => {
        return throwError(() => error);
      })
    );
  }

  public deleteCategory(category: AddCategory): Observable<boolean> {
    return this.httpClient
      .delete(`${this.url}/deleteCategory`, { body: category, responseType: 'text' })
      .pipe(
        map(() => {
          return true;
        }),
        catchError((error: HttpErrorResponse) => {
          console.log("error: " + error);
          return of(false);
        })
      );
  }
}