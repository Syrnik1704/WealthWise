import { DIALOG_DATA, DialogRef } from '@angular/cdk/dialog';
import { NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { TranslateModule } from '@ngx-translate/core';
import { LabeledValue } from '../../../shared';
import { GoalAddEditForm, GoalAddEditFormKeys } from './goal-add-edit-form.model';
import { GoalAddEditDialogContent } from './goal-add-edit-modal-content.model';

@Component({
  selector: 'ww-goal-add-edit-modal',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatFormField,
    MatInput,
    MatLabel,
    TranslateModule,
    NgIf,
    ReactiveFormsModule,
    MatDatepickerModule,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './goal-add-edit-modal.component.html',
})
export class GoalAddEditModalComponent implements OnInit {
  protected modelForm?: FormGroup<GoalAddEditForm>;
  protected readonly GoalAddEditFormKeys = GoalAddEditFormKeys;
  protected content: GoalAddEditDialogContent = inject(DIALOG_DATA);
  private dialogRef = inject<DialogRef<string>>(DialogRef<string>);
  private readonly formBuilder = inject(FormBuilder);
  private readonly intervalMap: Record<string, string> = {
    '0 0 10 * * *': 'SAVING_GOAL.INTERVAL.DAILY',
    '0 0 10 */2 * *': 'SAVING_GOAL.INTERVAL.EVERY_TWO_DAYS',
    '0 0 10 * * MON': 'SAVING_GOAL.INTERVAL.WEEKLY',
    '0 0 10 */14 * *': 'SAVING_GOAL.INTERVAL.BIWEEKLY',
    '0 0 10 1 * *': 'SAVING_GOAL.INTERVAL.MONTHLY',
  };

  public ngOnInit(): void {
    this.initFormModel();
  }

  private initFormModel(): void {
    this.modelForm = this.formBuilder.group({
      [GoalAddEditFormKeys.TITLE]: new FormControl<string>(
        this.content.savingGoal?.targetTitle ?? '',
        {
          validators: [Validators.required, Validators.maxLength(80), Validators.minLength(3)],
          nonNullable: true,
        }
      ),
      [GoalAddEditFormKeys.AMOUNT]: new FormControl<number>(
        this.content.savingGoal?.targetAmount ?? 1,
        {
          validators: [Validators.required, Validators.max(10000000), Validators.min(1)],
          nonNullable: true,
        }
      ),
      [GoalAddEditFormKeys.DATE]: new FormControl<Date | undefined>(
        this.content.savingGoal?.targetDate,
        { validators: [Validators.required], nonNullable: true }
      ),
      [GoalAddEditFormKeys.DESCRIPTION]: new FormControl<string | undefined>(
        this.content.savingGoal?.description,
        { validators: [Validators.maxLength(500)], nonNullable: true }
      ),
      [GoalAddEditFormKeys.CURRENT_AMOUNT]: new FormControl<number>(
        this.content.savingGoal?.currentAmount ?? 0,
        { nonNullable: true }
      ),
      [GoalAddEditFormKeys.CYCLICAL_PAYMENT_AMOUNT]: new FormControl<number | undefined>(
        this.content.savingGoal?.cyclicalPaymentAmount,
        { nonNullable: true }
      ),
      [GoalAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL]: new FormControl<LabeledValue | undefined>(
        this.getInterval(this.content.savingGoal?.cyclicalPaymentCron),
        { nonNullable: true }
      ),
    });
  }

  private getInterval(cronPattern?: string): LabeledValue | undefined {
    if (!cronPattern || !this.intervalMap[cronPattern]) {
      return;
    }
    return { label: this.intervalMap[cronPattern], value: cronPattern };
  }
}
