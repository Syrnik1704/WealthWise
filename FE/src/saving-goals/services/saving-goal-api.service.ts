import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { catchError, map, Observable, of } from 'rxjs';
import { environment } from '../../app/environments/environment.development';
import { SavingGoalRequest } from '../models';
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
        this.openGoalToast(true, 'ADD', request.targetTitle);
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        this.openGoalToast(false, 'ADD', request.targetTitle, error.error);
        return of(false);
      })
    );
  }

  public getGoalList(): Observable<SavingGoal[]> {
    return this.httpClient.get<SavingGoal[]>(`${this.baseApiUrl}`).pipe(
      map(savingGoals =>
        savingGoals
          .map(goal => ({
            ...goal,
            progress: this.calculateProgress(goal),
          }))
          .sort((a, b) => new Date(a.targetDate).getTime() - new Date(b.targetDate).getTime())
      )
    );
  }

  public updateGoal(savingGoalId: number, request: SavingGoalRequest): Observable<boolean> {
    return this.httpClient.put(`${this.baseApiUrl}/${savingGoalId}`, request).pipe(
      map(() => {
        this.openGoalToast(true, 'UPDATED', request.targetTitle);
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        this.openGoalToast(false, 'UPDATED', request.targetTitle, error.error);
        return of(false);
      })
    );
  }

  public removeGoal(savingGoalId: number, savingGoalTitle: string) {
    return this.httpClient
      .delete(`${this.baseApiUrl}/${savingGoalId}`, { responseType: 'text' })
      .pipe(
        map(() => {
          this.openGoalToast(true, 'REMOVED', savingGoalTitle);
          return true;
        }),
        catchError((error: HttpErrorResponse) => {
          this.openGoalToast(false, 'REMOVED', savingGoalTitle, error.error);
          return of(false);
        })
      );
  }

  private calculateProgress(savingGoal: SavingGoal): number {
    return Math.round((savingGoal.currentAmount / savingGoal.targetAmount) * 10000) / 100;
  }

  private openGoalToast(isSuccess: boolean, action: string, name: string, error?: string): void {
    const message = `SAVING_GOAL.TOAST.${isSuccess ? 'SUCCESS' : 'ERROR'}_${action}`;
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
