import { CommonModule, DatePipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Observable, of, switchMap } from 'rxjs';
import { ConfirmDialogComponent, ConfirmDialogContent, IntervalService } from '../../../shared';
import { Income } from '../../models';
import { IncomeApiService } from '../../services';
import { IncomeFormComponent } from '../income-form';

@Component({
  selector: 'ww-incomes-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatListModule,
    TranslateModule,
    CommonModule,
    DatePipe,
    MatCardModule,
    MatIconModule,
    MatButtonModule,
    IncomeFormComponent,
  ],
  templateUrl: './incomes-list.component.html',
  styleUrl: './incomes-list.component.scss',
})
export class IncomesListComponent {
  private readonly apiService = inject(IncomeApiService);
  protected readonly intervalService = inject(IntervalService);
  protected incomes$: Observable<Income[]> = this.apiService.getIncomeList();
  private readonly cdRef = inject(ChangeDetectorRef);
  private readonly dialogService = inject(MatDialog);
  private readonly destroyRef = inject(DestroyRef);
  private readonly translate = inject(TranslateService);

  public refreshTable(): void {
    this.incomes$ = this.apiService.getIncomeList();
    this.cdRef.detectChanges();
  }

  protected remove(income: Income): void {
    const dialogData: ConfirmDialogContent = {
      header: 'INCOMES.MODALS.REMOVE.HEADER',
      message: `${this.translate.instant('INCOMES.MODALS.REMOVE.MESSAGE')} ${income.name}`,
    };
    const dialogRef = this.dialogService.open<
      ConfirmDialogComponent,
      ConfirmDialogContent,
      boolean
    >(ConfirmDialogComponent, {
      hasBackdrop: true,
      closeOnNavigation: true,
      data: dialogData,
      minWidth: '40vw',
    });

    dialogRef
      .afterClosed()
      .pipe(
        switchMap(result => {
          if (!result) {
            return of(false);
          }
          return this.apiService.removeIncome(income.idIncomes, income.name);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(result => {
        if (result) {
          this.refreshTable();
        }
      });
  }
}
