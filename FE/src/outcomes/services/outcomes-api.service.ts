import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { catchError, map, Observable, of } from 'rxjs';
import { environment } from '../../app/environments/environment.development';

@Injectable({ providedIn: 'root' })
export class OutcomeApiService {
  private readonly baseApiUrl = `${environment.apiUrl}/expenses`;
  private readonly httpClient = inject(HttpClient);
  private readonly toastService = inject(MatSnackBar);
  private readonly translate = inject(TranslateService);

  public addOutcome(request: OutcomeRequest): Observable<boolean> {
    return this.httpClient.post(`${this.baseApiUrl}`, request).pipe(
      map(() => {
        this.openOutcomeToast(true, 'ADD', request.targetTitle);
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        this.openOutcomeToast(false, 'ADD', request.targetTitle, error.error);
        return of(false);
      })
    );
  }

  public getOutcomeList(): Observable<Outcome[]> {
    return this.httpClient.get<Outcome[]>(`${this.baseApiUrl}`);
  }

  public updateOutcome(outcomeId: number, request: OutcomeRequest): Observable<boolean> {
    return this.httpClient.put(`${this.baseApiUrl}/${outcomeId}`, request).pipe(
      map(() => {
        this.openOutcomeToast(true, 'UPDATED', request.targetTitle);
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        this.openOutcomeToast(false, 'UPDATED', request.targetTitle, error.error);
        return of(false);
      })
    );
  }

  public removeOutcome(outcomeId: number, outcomeTitle: string) {
    return this.httpClient.delete(`${this.baseApiUrl}/${outcomeId}`, { responseType: 'text' }).pipe(
      map(() => {
        this.openOutcomeToast(true, 'REMOVED', outcomeTitle);
        return true;
      }),
      catchError((error: HttpErrorResponse) => {
        this.openOutcomeToast(false, 'REMOVED', outcomeTitle, error.error);
        return of(false);
      })
    );
  }

  private openOutcomeToast(isSuccess: boolean, action: string, name: string, error?: string): void {
    const message = `OUTCOME.TOAST.${isSuccess ? 'SUCCESS' : 'ERROR'}_${action}`;
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
