import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment.development';
import { catchError, map, Observable, switchMap, throwError } from 'rxjs';
import { UserData, UsersData } from '../models/usersData';
import { HttpClient } from '@angular/common/http';
import { UserEmails } from '../models/userEmails';
import { AddCategory } from '../models/addCategory';


@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private url = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  public getUsers(): Observable<UsersData> {
    return this.http.get<UsersData>(`${this.url}/users`).pipe(
      map((response) => response),
      catchError((error) => {
        console.error('Error fetching users:', error);
        return throwError(() => new Error('Failed to fetch users'));
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

  public blockUser(email: UserEmails): Observable<void> {
    return this.http.put<void>(`${this.url}/block`, { email }).pipe(
      catchError((error) => {
        console.error('Error blocking user:', error);
        return throwError(() => new Error('Failed to block user'));
      })
    );
  }

  public unblockUser(email: UserEmails): Observable<void> {
    return this.http.put<void>(`${this.url}/unblock`, { email }).pipe(
      catchError((error) => {
        console.error('Error unblocking user:', error);
        return throwError(() => new Error('Failed to unblock user'));
      })
    );
  }

  public addCategory(category: AddCategory): Observable<void> {
    return this.http.post<void>(`${this.url}/addCategory`, { name: category }).pipe(
      catchError((error) => {
        console.error('Error adding category:', error);
        return throwError(() => new Error('Failed to add category'));
      })
    );
  }
}
