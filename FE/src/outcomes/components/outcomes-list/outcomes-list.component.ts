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
import { Outcome } from '../../models';
import { OutcomeApiService } from '../../services/outcomes-api.service';
import { OutcomeFormComponent } from '../outcome-form';

@Component({
  selector: 'ww-outcomes-list',
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
    OutcomeFormComponent,
  ],
  templateUrl: './outcomes-list.component.html',
  styleUrl: './outcomes-list.component.scss',
})
export class OutcomesListComponent {
  private readonly apiService = inject(OutcomeApiService);
  protected readonly intervalService = inject(IntervalService);
  protected outcomes$: Observable<Outcome[]> = this.apiService.getOutcomeList();
  private readonly cdRef = inject(ChangeDetectorRef);
  private readonly dialogService = inject(MatDialog);
  private readonly destroyRef = inject(DestroyRef);
  private readonly translate = inject(TranslateService);

  public refreshTable(): void {
    this.outcomes$ = this.apiService.getOutcomeList();
    this.cdRef.detectChanges();
  }

  protected remove(outcome: Outcome): void {
    const dialogData: ConfirmDialogContent = {
      header: 'OUTCOMES.MODALS.REMOVE.HEADER',
      message: `${this.translate.instant('OUTCOMES.MODALS.REMOVE.MESSAGE')} ${outcome.name}`,
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
          return this.apiService.removeOutcome(outcome.idExpenses, outcome.name);
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
