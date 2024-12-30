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
import { IntervalService } from '../../../shared';
import { SubscriptionRequest } from '../../models';
import { SubscriptionAddEditForm, SubscriptionAddEditFormKeys } from './subscription-add-edit-form.model';
import { SubscriptionAddEditDialogContent } from './subscription-add-edit-modal-content.model';

@Component({
  selector: 'ww-subscription-add-edit-modal',
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
  templateUrl: './subscription-add-edit-modal.component.html',
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
export class SubscriptionAddEditModalComponent implements OnInit {
  protected modelForm?: FormGroup<SubscriptionAddEditForm>;
  protected readonly SubscriptionAddEditFormKeys = SubscriptionAddEditFormKeys;
  protected content: SubscriptionAddEditDialogContent = inject(MAT_DIALOG_DATA);
  protected readonly intervalService = inject(IntervalService);
  private dialogRef = inject<MatDialogRef<SubscriptionRequest | undefined>>(
    MatDialogRef<SubscriptionRequest | undefined>
  );
  private readonly formBuilder = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  protected intervalOptions = this.intervalService.intervalOptions();

  public ngOnInit(): void {
    this.initFormModel();
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

  private prepareReuestData(): SubscriptionRequest | undefined {
    const formValue = this.modelForm?.getRawValue();
    if (!formValue) {
      return;
    }
    return formValue;
  }

  private initFormModel(): void {
    this.modelForm = this.formBuilder.group({
      [SubscriptionAddEditFormKeys.TITLE]: new FormControl<string>(
        this.content.subscription?.subscriptionTitle ?? '',
        {
          validators: [Validators.required, Validators.maxLength(255), Validators.minLength(1)],
          nonNullable: true,
        }
      ),
      [SubscriptionAddEditFormKeys.AMOUNT]: new FormControl<number>(
        this.content.subscription?.amount ?? 1,
        {
          validators: [Validators.required, Validators.max(10000000), Validators.min(1)],
          nonNullable: true,
        }
      ),
      [SubscriptionAddEditFormKeys.CYCLICAL_PAYMENT_DATE]: new FormControl<number>(
        this.content.subscription?.cyclicalPaymentDate ?? 1,
        {
          validators: [Validators.required, Validators.min(1)],
          nonNullable: true,
        }
      ),
      [SubscriptionAddEditFormKeys.DESCRIPTION]: new FormControl<string | undefined>(
        this.content.subscription?.description,
        { validators: [Validators.maxLength(500)], nonNullable: true }
      ),
      [SubscriptionAddEditFormKeys.DATE]: new FormControl<Date>(
        this.content.subscription?.creationDate ?? new Date(),
        { nonNullable: true }
      ),
    });
  }
}