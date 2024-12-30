import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { catchError, map, Observable, of } from 'rxjs';
import { environment } from '../../app/environments/environment.development';
import { Subscription, SubscriptionRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class SubscriptionsApiService {
  private readonly baseApiUrl = `${environment.apiUrl}/subscriptions`;
  private readonly httpClient = inject(HttpClient);
  private readonly toastService = inject(MatSnackBar);
  private readonly translate = inject(TranslateService);

  public addSubscription(request: SubscriptionRequest): Observable<boolean> {
    return this.httpClient.post(`${this.baseApiUrl}`, request).pipe(
      map(() => {
        this.openGoalToast(true, 'ADD', request.subscriptionTitle);
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        this.openGoalToast(false, 'ADD', request.subscriptionTitle, error.error);
        return of(false);
      })
    );
  }

  public getSubscriptionsList(): Observable<Subscription[]> {
    return this.httpClient.get<Subscription[]>(`${this.baseApiUrl}`);
  }

  public updateGoal(subscriptionId: number, request: SubscriptionRequest): Observable<boolean> {
    return this.httpClient.put(`${this.baseApiUrl}/${subscriptionId}`, request).pipe(
      map(() => {
        this.openGoalToast(true, 'UPDATED', request.subscriptionTitle);
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        this.openGoalToast(false, 'UPDATED', request.subscriptionTitle, error.error);
        return of(false);
      })
    );
  }

  public removeSubscription(subscriptionId: number, subscriptionTitle: string) {
    return this.httpClient
      .delete(`${this.baseApiUrl}/${subscriptionId}`, { responseType: 'text' })
      .pipe(
        map(() => {
          this.openGoalToast(true, 'REMOVED', subscriptionTitle);
          return true;
        }),
        catchError((error: HttpErrorResponse) => {
          this.openGoalToast(false, 'REMOVED', subscriptionTitle, error.error);
          return of(false);
        })
      );
  }

  private openGoalToast(isSuccess: boolean, action: string, name: string, error?: string): void {
    const message = `SUBSCRIPTIONS.TOAST.${isSuccess ? 'SUCCESS' : 'ERROR'}_${action}`;
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
