import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule, provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule } from '@ngx-translate/core';
import { IntervalService, LabeledValue } from '../../../shared';
import { SavingGoalRequest } from '../../models';
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
    ReactiveFormsModule,
    MatDatepickerModule,
    MatSelectModule,
    MatOptionModule,
  ],
  providers: [provideNativeDateAdapter()],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './goal-add-edit-modal.component.html',
  styles: [
    `
      .saving-goal-form {
        display: flex;
        gap: 24px;
        flex-direction: column;
        justify-content: center;
      }

      .saving-goal-actions {
        display: flex;
        gap: 16px;
        align-items: center;
        justify-content: center;
      }
    `,
  ],
})
export class GoalAddEditModalComponent implements OnInit {
  protected modelForm?: FormGroup<GoalAddEditForm>;
  protected readonly GoalAddEditFormKeys = GoalAddEditFormKeys;
  protected content: GoalAddEditDialogContent = inject(MAT_DIALOG_DATA);
  protected readonly intervalService = inject(IntervalService);
  private dialogRef = inject<MatDialogRef<SavingGoalRequest | undefined>>(
    MatDialogRef<SavingGoalRequest | undefined>
  );
  private readonly formBuilder = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  protected intervalOptions = this.intervalService.intervalOptions();

  public ngOnInit(): void {
    this.initFormModel();
    this.modelForm?.controls[GoalAddEditFormKeys.CYCLICAL_PAYMENT_AMOUNT].valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(value => {
        if (value) {
          this.modelForm?.controls[GoalAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL].enable();
        } else {
          this.modelForm?.controls[GoalAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL].disable();
        }
      });
  }

  protected saveForm(): void {
    if (this.modelForm?.invalid) {
      return;
    }
    this.dialogRef.close(this.prepareReuestData());
  }

  protected onCancel(): void {
    this.dialogRef.close();
  }

  private prepareReuestData(): SavingGoalRequest | undefined {
    const formValue = this.modelForm?.getRawValue();
    if (!formValue) {
      return;
    }
    return {
      ...formValue,
      targetDate: formValue[GoalAddEditFormKeys.DATE].toISOString(), //TODO: zero time
      cyclicalPaymentCron: formValue[GoalAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL]?.value,
    };
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
      [GoalAddEditFormKeys.DATE]: new FormControl<Date>(
        this.content.savingGoal?.targetDate
          ? new Date(this.content.savingGoal.targetDate)
          : new Date(),
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
        { nonNullable: true, validators: [Validators.min(1)] }
      ),
      [GoalAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL]: new FormControl<LabeledValue | undefined>(
        {
          value: this.intervalOptions.find(
            interval => interval?.value === this.content.savingGoal?.cyclicalPaymentCron
          ),
          disabled: !this.content.savingGoal?.cyclicalPaymentAmount,
        },
        { nonNullable: true }
      ),
    });
  }
}
