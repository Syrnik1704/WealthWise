import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { catchError, map, Observable, of } from 'rxjs';
import { environment } from '../../app/environments/environment.development';
import { Income, IncomeRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class IncomeApiService {
  private readonly baseApiUrl = `${environment.apiUrl}/incomes`;
  private readonly httpClient = inject(HttpClient);
  private readonly toastService = inject(MatSnackBar);
  private readonly translate = inject(TranslateService);

  public addIncome(request: IncomeRequest): Observable<boolean> {
    return this.httpClient
      .post(`${this.baseApiUrl}/addIncome`, request, { responseType: 'text' })
      .pipe(
        map(() => {
          this.openIncomeToast(true, 'ADD', request.name);
          return true;
        }),
        catchError((error: HttpErrorResponse) => {
          this.openIncomeToast(false, 'ADD', request.name, error.error);
          return of(false);
        })
      );
  }

  public getIncomeList(): Observable<Income[]> {
    return this.httpClient
      .get<Income[]>(`${this.baseApiUrl}/getIncomes`)
      .pipe(
        map(response =>
          response.sort(
            (a, b) => -(new Date(a.createdDate).getTime() - new Date(b.createdDate).getTime())
          )
        )
      );
  }

  public updateIncome(incomeId: number, request: IncomeRequest): Observable<boolean> {
    return this.httpClient
      .put(`${this.baseApiUrl}/updateIncome/${incomeId}`, request, { responseType: 'text' })
      .pipe(
        map(() => {
          this.openIncomeToast(true, 'UPDATED', request.name);
          return true;
        }),
        catchError((error: HttpErrorResponse) => {
          this.openIncomeToast(false, 'UPDATED', request.name, error.error);
          return of(false);
        })
      );
  }

  public removeIncome(incomeId: number, incomeTitle: string) {
    return this.httpClient
      .delete(`${this.baseApiUrl}/deleteIncome`, { body: [incomeId], responseType: 'text' })
      .pipe(
        map(() => {
          this.openIncomeToast(true, 'REMOVED', incomeTitle);
          return true;
        }),
        catchError((error: HttpErrorResponse) => {
          this.openIncomeToast(false, 'REMOVED', incomeTitle, error.error);
          return of(false);
        })
      );
  }

  private openIncomeToast(isSuccess: boolean, action: string, name: string, error?: string): void {
    const message = `INCOMES.TOAST.${isSuccess ? 'SUCCESS' : 'ERROR'}_${action}`;
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
