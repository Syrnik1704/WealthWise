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
import { MatMenuModule } from '@angular/material/menu';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Observable, of, switchMap } from 'rxjs';
import { ConfirmDialogComponent, ConfirmDialogContent, IntervalService } from '../../../shared';
import { SavingGoal, SavingGoalRequest } from '../../models';
import { SavingGoalApiService } from '../../services/saving-goal-api.service';
import { GoalAddEditDialogContent } from '../outcome-add-edit-modal/outcome-add-edit-modal-content.model';
import { GoalAddEditModalComponent } from '../outcome-add-edit-modal/outcome-add-edit-modal.component';

@Component({
  selector: 'ww-saving-goal-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatListModule,
    TranslateModule,
    CommonModule,
    DatePipe,
    MatCardModule,
    MatIconModule,
    MatMenuModule,
    MatButtonModule,
  ],
  templateUrl: './saving-goal-list.component.html',
  styleUrl: './saving-goal-list.component.scss',
})
export class SavingGoalListComponent {
  private readonly savingGoalApiService = inject(SavingGoalApiService);
  protected readonly intervalService = inject(IntervalService);
  protected savingGoalsData$: Observable<SavingGoal[]> = this.savingGoalApiService.getGoalList();
  private readonly cdRef = inject(ChangeDetectorRef);
  private readonly dialogService = inject(MatDialog);
  private readonly destroyRef = inject(DestroyRef);
  private readonly translate = inject(TranslateService);

  public refreshTable(): void {
    this.savingGoalsData$ = this.savingGoalApiService.getGoalList();
    this.cdRef.detectChanges();
  }

  protected editGoal(savingGoal: SavingGoal): void {
    const dialogData: GoalAddEditDialogContent = {
      isEdit: true,
      savingGoal,
    };
    const dialogRef = this.dialogService.open<
      GoalAddEditModalComponent,
      GoalAddEditDialogContent,
      SavingGoalRequest
    >(GoalAddEditModalComponent, {
      hasBackdrop: true,
      closeOnNavigation: true,
      data: dialogData,
      maxWidth: '100vw',
      minWidth: '40vw',
    });

    dialogRef
      .afterClosed()
      .pipe(
        switchMap(result => {
          if (!result) {
            return of(false);
          }
          return this.savingGoalApiService.updateGoal(savingGoal.targetId, result);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(result => {
        if (result) {
          this.refreshTable();
        }
      });
  }

  protected removeGoal(savingGoal: SavingGoal): void {
    const dialogData: ConfirmDialogContent = {
      header: 'SAVING_GOAL.MODALS.REMOVE.HEADER',
      message: `${this.translate.instant('SAVING_GOAL.MODALS.REMOVE.MESSAGE')} ${savingGoal.targetTitle}`,
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
          return this.savingGoalApiService.removeGoal(savingGoal.targetId, savingGoal.targetTitle);
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
