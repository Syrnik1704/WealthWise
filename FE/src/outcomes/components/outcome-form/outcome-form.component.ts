import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  Input,
  OnInit,
} from '@angular/core';
import {
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslateModule } from '@ngx-translate/core';
import { Outcome } from '../../models';
import { OutcomeForm, OutcomeFormKeys } from '../../models/outcome-form.model';
import { OutcomeApiService } from '../../services/outcomes-api.service';

@Component({
  selector: 'ww-outcome-form',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    TranslateModule,
  ],
  template: `
    @if (modelForm) {
      <form class="saving-goal-form" [formGroup]="modelForm">
        <mat-form-field>
          <mat-label>{{ 'OUTCOME.TABLE.NAME' | translate }}</mat-label>
          <input matInput [formControlName]="OutcomeFormKeys.NAME" />
          @if (modelForm.controls[OutcomeFormKeys.NAME].hasError('required')) {
            <mat-error>{{
              'COMMON.ERRORS.REQUIRED' | translate: { value: 'OUTCOME.TABLE.NAME' | translate }
            }}</mat-error>
          }
          @if (modelForm.controls[OutcomeFormKeys.NAME].hasError('minlength')) {
            <mat-error>{{
              'COMMON.ERRORS.MIN_LENGTH'
                | translate
                  : {
                      minlength:
                        modelForm.controls[OutcomeFormKeys.NAME].errors?.['minlength']
                          .requiredLength,
                    }
            }}</mat-error>
          }
          @if (modelForm.controls[OutcomeFormKeys.NAME].hasError('maxlength')) {
            <mat-error>{{
              'COMMON.ERRORS.MAX_LENGTH'
                | translate
                  : {
                      minlength:
                        modelForm.controls[OutcomeFormKeys.NAME].errors?.['maxlength']
                          .requiredLength,
                    }
            }}</mat-error>
          }
        </mat-form-field>
        <mat-form-field>
          <mat-label>{{ 'OUTCOME.TABLE.AMOUNT' | translate }}</mat-label>
          <input matInput [formControlName]="GoalAddEditFormKeys.AMOUNT" />
          @if (modelForm.controls[GoalAddEditFormKeys.AMOUNT].hasError('min')) {
            <mat-error>{{
              'COMMON.ERRORS.MIN'
                | translate
                  : {
                      min: modelForm.controls[GoalAddEditFormKeys.AMOUNT].errors?.['min'].min,
                    }
            }}</mat-error>
          }
          @if (modelForm.controls[GoalAddEditFormKeys.AMOUNT].hasError('max')) {
            <mat-error>{{
              'COMMON.ERRORS.MAX'
                | translate
                  : {
                      max: modelForm.controls[GoalAddEditFormKeys.AMOUNT].errors?.['max'].max,
                    }
            }}</mat-error>
          }
          @if (modelForm.controls[GoalAddEditFormKeys.AMOUNT].hasError('required')) {
            <mat-error>{{
              'COMMON.ERRORS.REQUIRED' | translate: { value: 'OUTCOME.TABLE.AMOUNT' | translate }
            }}</mat-error>
          }
        </mat-form-field>
        <mat-form-field>
          <mat-label>{{ 'OUTCOME.TABLE.DATE' | translate }}</mat-label>
          <input matInput [formControlName]="GoalAddEditFormKeys.DATE" [matDatepicker]="picker" />
          <mat-datepicker-toggle matIconSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
          @if (modelForm.controls[GoalAddEditFormKeys.DATE].hasError('required')) {
            <mat-error>{{
              'COMMON.ERRORS.REQUIRED' | translate: { value: 'OUTCOME.TABLE.DATE' | translate }
            }}</mat-error>
          }
        </mat-form-field>
        <mat-form-field>
          <mat-label>{{ 'OUTCOME.TABLE.CURRENT_AMOUNT' | translate }}</mat-label>
          <input matInput [formControlName]="GoalAddEditFormKeys.CURRENT_AMOUNT" />
        </mat-form-field>
        <mat-form-field>
          <mat-label>{{ 'OUTCOME.TABLE.DESCRIPTION' | translate }}</mat-label>
          <textarea matInput [formControlName]="GoalAddEditFormKeys.DESCRIPTION"></textarea>
          @if (modelForm.controls[GoalAddEditFormKeys.DESCRIPTION].hasError('maxlength')) {
            <mat-error>{{
              'COMMON.ERRORS.MAX_LENGTH'
                | translate
                  : {
                      maxlength:
                        modelForm.controls[GoalAddEditFormKeys.DESCRIPTION].errors?.['maxlength']
                          .requiredLength,
                    }
            }}</mat-error>
          }
        </mat-form-field>
        <mat-form-field>
          <mat-label>{{ 'OUTCOME.TABLE.CYCLICAL_PAYMENT_AMOUNT' | translate }}</mat-label>
          <input matInput [formControlName]="GoalAddEditFormKeys.CYCLICAL_PAYMENT_AMOUNT" />
        </mat-form-field>
        <mat-form-field>
          <mat-label>{{ 'OUTCOME.TABLE.CYCLICAL_PAYMENT_INTERVAL' | translate }}</mat-label>
          <mat-select [formControlName]="GoalAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL">
            <mat-option>-</mat-option>
            @for (interval of intervalOptions; track interval) {
              <mat-option [value]="interval">
                @if (interval && interval?.label) {
                  {{ interval.label | translate }}
                }
              </mat-option>
            }
          </mat-select>
        </mat-form-field>
      </form>
    }
  `,
  styles: ``,
})
export class OutcomeFormComponent implements OnInit {
  @Input() outcome?: Outcome;
  protected modelForm?: FormGroup<OutcomeForm>;
  protected readonly OutcomeFormKeys = OutcomeFormKeys;
  private readonly apiService = inject(OutcomeApiService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly formBuilder = inject(NonNullableFormBuilder);
  private isEdit = false;

  public ngOnInit(): void {
    this.modelForm = this.initFromModel(this.outcome);
    this.isEdit = !!this.outcome;
  }

  private initFromModel(outcome?: Outcome): FormGroup<OutcomeForm> {
    return this.formBuilder.group({
      [OutcomeFormKeys.NAME]: new FormControl<string>(outcome?.name ?? '', {
        validators: [Validators.required, Validators.maxLength(80), Validators.minLength(3)],
        nonNullable: true,
      }),
      [OutcomeFormKeys.AMOUNT]: new FormControl<number>(outcome?.amount ?? 1, {
        validators: [Validators.required, Validators.min(0.01), Validators.pattern(/\d*.\d{1,2}/)],
        nonNullable: true,
      }),
      [OutcomeFormKeys.CATEGORY]: new FormControl<string>(outcome?.category ?? '', {
        validators: [Validators.required],
        nonNullable: true,
      }),
      [OutcomeFormKeys.DESCRIPTION]: new FormControl<string | undefined>(outcome?.description, {
        validators: [Validators.required],
        nonNullable: true,
      }),
    });
  }
}
