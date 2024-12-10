import { ChangeDetectionStrategy, Component, DestroyRef, inject, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { of, switchMap } from 'rxjs';
import { SavingGoalRequest } from '../models';
import { SavingGoalApiService } from '../services';
import { GoalAddEditDialogContent } from './goal-add-edit-modal/goal-add-edit-modal-content.model';
import { GoalAddEditModalComponent } from './goal-add-edit-modal/goal-add-edit-modal.component';
import { SavingGoalListComponent } from './saving-goal-list/saving-goal-list.component';

@Component({
  selector: 'ww-saving-goals',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatButtonModule, TranslateModule, SavingGoalListComponent, MatDialogModule],
  template: `
    <button mat-raised-button (click)="addGoal()">
      {{ 'SAVING_GOAL.ADD_GOAL' | translate }}
    </button>
    <ww-saving-goal-list #savingGoalList></ww-saving-goal-list>
  `,
  styles: `
    :host {
      display: flex;
      flex-direction: column;
      gap: 24px;
      padding-top: 16px;
      padding-left: 4px;
      padding-right: 4px;
    }
  `,
})
export class SavingGoalsComponent {
  @ViewChild('savingGoalList')
  protected readonly savingGoalList?: SavingGoalListComponent;
  private readonly dialogService = inject(MatDialog);
  private readonly destroyRef = inject(DestroyRef);
  private readonly savingGoalApiService = inject(SavingGoalApiService);

  protected addGoal(): void {
    const dialogData: GoalAddEditDialogContent = {
      isEdit: false,
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
          return this.savingGoalApiService.addGoal(result);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(result => {
        if (result) {
          this.savingGoalList?.refreshTable();
        }
      });
  }
}
