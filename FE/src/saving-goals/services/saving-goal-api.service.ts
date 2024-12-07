import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { catchError, map, Observable, of } from 'rxjs';
import { SavingGoalRequest } from '../models';
import { environment } from '../../app/environments/environment.development';
import { SavingGoal } from '../models/saving-goal.model';

@Injectable({ providedIn: 'root' })
export class SavingGoalApiService {
  private readonly baseApiUrl = `${environment.apiUrl}/savingTargets`;
  private readonly httpClient = inject(HttpClient);
  private readonly toastService = inject(MatSnackBar);
  private readonly translate = inject(TranslateService);

  public addGoal(request: SavingGoalRequest): Observable<boolean> {
    return this.httpClient.post(`${this.baseApiUrl}`, request).pipe(
      map(() => {
        this.openAddEditGoalToast(true, false, request.targetTitle);
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        this.openAddEditGoalToast(false, false, request.targetTitle, error.error);
        return of(false);
      })
    );
  }

  public getGoalList(): Observable<SavingGoal[]> {
    return this.httpClient.get<SavingGoal[]>(`${this.baseApiUrl}`);
  }

  public updateGoal(savingGoalId: number, request: SavingGoalRequest): Observable<boolean> {
    return this.httpClient.put(`${this.baseApiUrl}/${savingGoalId}`, request).pipe(
      map(() => {
        this.openAddEditGoalToast(true, false, request.targetTitle);
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        this.openAddEditGoalToast(false, false, request.targetTitle, error.error);
        return of(false);
      })
    );
  }

  private openAddEditGoalToast(
    isSuccess: boolean,
    isEdit: boolean,
    name: string,
    error?: string
  ): void {
    const message = `SAVING_GOAL.TOAST.${isSuccess ? 'SUCCESS' : 'ERROR'}_${isEdit ? 'ADD' : 'UPDATED'}`;
    let interpolateParams: Record<string, string> = { name };
    if (error) {
      interpolateParams = { ...interpolateParams, error };
    }

    this.toastService.open(this.translate.instant(message, interpolateParams), '', {
      duration: 5000,
      panelClass: [isSuccess ? 'success-toast' : 'error-toast'],
    });
  }
}
