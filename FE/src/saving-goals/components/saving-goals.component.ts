import { Dialog } from '@angular/cdk/dialog';
import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { TranslateModule } from '@ngx-translate/core';
import { of, switchMap } from 'rxjs';
import { SavingGoal, SavingGoalRequest } from '../models';
import { SavingGoalApiService } from '../services';
import { GoalAddEditDialogContent } from './goal-add-edit-modal/goal-add-edit-modal-content.model';
import { GoalAddEditModalComponent } from './goal-add-edit-modal/goal-add-edit-modal.component';

@Component({
  selector: 'ww-saving-goals',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatButtonModule, TranslateModule],
  template: `
    <button mat-raised-button (click)="addGoal()">
      {{ 'SAVING_GOAL.ADD_GOAL' | translate }}
    </button>
  `,
  styles: ``,
})
export class SavingGoalsComponent {
  private readonly dialogService = inject(Dialog);
  private readonly destroyRef = inject(DestroyRef);
  private readonly savingGoalApiService = inject(SavingGoalApiService);

  protected addGoal(): void {
    this.openDialog(false);
  }

  private openDialog(isEdit: boolean, savingGoal?: SavingGoal): void {
    const dialogData: GoalAddEditDialogContent = {
      isEdit,
      savingGoal,
    };
    const dialogRef = this.dialogService.open<SavingGoalRequest>(GoalAddEditModalComponent, {
      hasBackdrop: true,
      closeOnNavigation: true,
      closeOnDestroy: true,
      data: dialogData,
    });

    dialogRef.closed
      .pipe(
        switchMap(result => {
          if (!result) {
            return of(false);
          }
          if (isEdit) {
            return savingGoal
              ? this.savingGoalApiService.updateGoal(savingGoal?.targetId, savingGoal)
              : of(false);
          }
          return this.savingGoalApiService.addGoal(result);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(result => {
        if (result) {
          //TODO: refresh table
        }
      });
  }
}
