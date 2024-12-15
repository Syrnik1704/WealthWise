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
import { OutcomeAddEditForm, OutcomeAddEditFormKeys } from './outcome-add-edit-form.model';
import { OutcomeAddEditDialogContent } from './outcome-add-edit-modal-content.model';

@Component({
  selector: 'ww-outcome-add-edit-modal',
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
  templateUrl: './Outcome-add-edit-modal.component.html',
  styles: [
    `
      .saving-Outcome-form {
        display: flex;
        gap: 24px;
        flex-direction: column;
        justify-content: center;
      }

      .saving-Outcome-actions {
        display: flex;
        gap: 16px;
        align-items: center;
        justify-content: center;
      }
    `,
  ],
})
export class OutcomeAddEditModalComponent implements OnInit {
  protected modelForm?: FormGroup<OutcomeAddEditForm>;
  protected readonly OutcomeAddEditFormKeys = OutcomeAddEditFormKeys;
  protected content: OutcomeAddEditDialogContent = inject(MAT_DIALOG_DATA);
  protected readonly intervalService = inject(IntervalService);
  private dialogRef = inject<MatDialogRef<SavingOutcomeRequest | undefined>>(
    MatDialogRef<SavingOutcomeRequest | undefined>
  );
  private readonly formBuilder = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  protected intervalOptions = this.intervalService.intervalOptions();

  public ngOnInit(): void {
    this.initFormModel();
    this.modelForm?.controls[OutcomeAddEditFormKeys.CYCLICAL_PAYMENT_AMOUNT].valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(value => {
        if (value) {
          this.modelForm?.controls[OutcomeAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL].enable();
        } else {
          this.modelForm?.controls[OutcomeAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL].disable();
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

  private prepareReuestData(): SavingOutcomeRequest | undefined {
    const formValue = this.modelForm?.getRawValue();
    if (!formValue) {
      return;
    }
    return {
      ...formValue,
      cyclicalPaymentCron: formValue[OutcomeAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL]?.value,
    };
  }

  private initFormModel(): void {
    this.modelForm = this.formBuilder.group({
      [OutcomeAddEditFormKeys.TITLE]: new FormControl<string>(
        this.content.savingOutcome?.targetTitle ?? '',
        {
          validators: [Validators.required, Validators.maxLength(80), Validators.minLength(3)],
          nonNullable: true,
        }
      ),
      [OutcomeAddEditFormKeys.AMOUNT]: new FormControl<number>(
        this.content.savingOutcome?.targetAmount ?? 1,
        {
          validators: [Validators.required, Validators.max(10000000), Validators.min(1)],
          nonNullable: true,
        }
      ),
      [OutcomeAddEditFormKeys.DATE]: new FormControl<Date>(
        this.content.savingOutcome?.targetDate ?? new Date(),
        { validators: [Validators.required], nonNullable: true }
      ),
      [OutcomeAddEditFormKeys.DESCRIPTION]: new FormControl<string | undefined>(
        this.content.savingOutcome?.description,
        { validators: [Validators.maxLength(500)], nonNullable: true }
      ),
      [OutcomeAddEditFormKeys.CURRENT_AMOUNT]: new FormControl<number>(
        this.content.savingOutcome?.currentAmount ?? 0,
        { nonNullable: true }
      ),
      [OutcomeAddEditFormKeys.CYCLICAL_PAYMENT_AMOUNT]: new FormControl<number | undefined>(
        this.content.savingOutcome?.cyclicalPaymentAmount,
        { nonNullable: true, validators: [Validators.min(1)] }
      ),
      [OutcomeAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL]: new FormControl<LabeledValue | undefined>(
        {
          value: this.intervalOptions.find(
            interval => interval?.value === this.content.savingOutcome?.cyclicalPaymentCron
          ),
          disabled: !this.content.savingOutcome?.cyclicalPaymentAmount,
        },
        { nonNullable: true }
      ),
    });
  }
}
