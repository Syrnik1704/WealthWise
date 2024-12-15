import { ChangeDetectionStrategy, Component, DestroyRef, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { OutcomeAddEditDialogContent } from '../outcome-add-edit-modal/outcome-add-edit-modal-content.model';
import { OutcomeAddEditModalComponent } from '../outcome-add-edit-modal/outcome-add-edit-modal.component';

@Component({
  selector: 'ww-outcome-tab',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [],
  template: ``,
  styles: [``],
})
export class OutcomeTabComponent {
  private readonly dialogService = inject(MatDialog);
  private readonly destroyRef = inject(DestroyRef);
  private readonly apiService = inject(SavingGoalApiService);

  protected addGoal(): void {
    const dialogData: OutcomeAddEditDialogContent = {
      isEdit: false,
    };
    const dialogRef = this.dialogService.open<
      OutcomeAddEditModalComponent,
      OutcomeAddEditDialogContent,
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
