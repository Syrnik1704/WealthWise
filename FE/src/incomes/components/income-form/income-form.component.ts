import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { CategoriesApiService } from '../../../shared/services/categories-service';
import { Income, IncomeForm, IncomeFormKeys, IncomeRequest } from '../../models';
import { IncomeApiService } from '../../services/incomes-api.service';

@Component({
  selector: 'ww-income-form',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    TranslateModule,
    MatButtonModule,
    MatTooltipModule,
    CommonModule,
  ],
  template: `
    @if (modelForm) {
      <form class="income-form" [formGroup]="modelForm">
        <mat-form-field class="income-form__item">
          <mat-label>{{ 'INCOMES.TABLE.NAME' | translate }}</mat-label>
          <input matInput [formControlName]="IncomeFormKeys.NAME" />
          @if (modelForm.controls[IncomeFormKeys.NAME].hasError('required')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.REQUIRED' | translate: { value: 'INCOMES.TABLE.NAME' | translate }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.REQUIRED' | translate: { value: 'INCOMES.TABLE.NAME' | translate }
              }}</mat-error
            >
          }
          @if (modelForm.controls[IncomeFormKeys.NAME].hasError('minlength')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.MIN_LENGTH'
                  | translate
                    : {
                        minlength:
                          modelForm.controls[IncomeFormKeys.NAME].errors?.['minlength']
                            .requiredLength,
                      }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.MIN_LENGTH'
                  | translate
                    : {
                        minlength:
                          modelForm.controls[IncomeFormKeys.NAME].errors?.['minlength']
                            .requiredLength,
                      }
              }}</mat-error
            >
          }
          @if (modelForm.controls[IncomeFormKeys.NAME].hasError('maxlength')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.MAX_LENGTH'
                  | translate
                    : {
                        maxlength:
                          modelForm.controls[IncomeFormKeys.NAME].errors?.['maxlength']
                            .requiredLength,
                      }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.MAX_LENGTH'
                  | translate
                    : {
                        minlength:
                          modelForm.controls[IncomeFormKeys.NAME].errors?.['maxlength']
                            .requiredLength,
                      }
              }}</mat-error
            >
          }
        </mat-form-field>
        <mat-form-field class="income-form__item">
          <mat-label>{{ 'INCOMES.TABLE.AMOUNT' | translate }}</mat-label>
          <input matInput [formControlName]="IncomeFormKeys.AMOUNT" />
          @if (modelForm.controls[IncomeFormKeys.AMOUNT].hasError('min')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.MIN'
                  | translate
                    : {
                        min: modelForm.controls[IncomeFormKeys.AMOUNT].errors?.['min'].min,
                      }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.MIN'
                  | translate
                    : {
                        min: modelForm.controls[IncomeFormKeys.AMOUNT].errors?.['min'].min,
                      }
              }}</mat-error
            >
          }
          @if (modelForm.controls[IncomeFormKeys.AMOUNT].hasError('pattern')) {
            <mat-error [matTooltip]="'COMMON.ERRORS.AMOUNT' | translate" class="text-ellipsis">{{
              'COMMON.ERRORS.AMOUNT' | translate
            }}</mat-error>
          }
          @if (modelForm.controls[IncomeFormKeys.AMOUNT].hasError('required')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.REQUIRED' | translate: { value: 'INCOMES.TABLE.AMOUNT' | translate }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.REQUIRED' | translate: { value: 'INCOMES.TABLE.AMOUNT' | translate }
              }}</mat-error
            >
          }
        </mat-form-field>
        <mat-form-field class="income-form__item">
          <mat-label>{{ 'INCOMES.TABLE.DESCRIPTION' | translate }}</mat-label>
          <textarea matInput [formControlName]="IncomeFormKeys.DESCRIPTION"></textarea>
          @if (modelForm.controls[IncomeFormKeys.DESCRIPTION].hasError('required')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.REQUIRED'
                  | translate: { value: 'INCOMES.TABLE.DESCRIPTION' | translate }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.REQUIRED'
                  | translate: { value: 'INCOMES.TABLE.DESCRIPTION' | translate }
              }}</mat-error
            >
          }
        </mat-form-field>
      </form>
      <button
        mat-raised-button
        [disabled]="modelForm?.invalid || modelForm?.pristine"
        (click)="save()">
        {{ (isEdit ? 'COMMON.BUTTONS.SAVE' : 'COMMON.BUTTONS.ADD') | translate }}
      </button>
    }
  `,
  styles: `
    :host {
      display: flex;
      flex-direction: column;
    }
    .income-form {
      display: flex;
      flex-wrap: wrap;
      align-items: start;
      row-gap: 4px;
      column-gap: 4px;
      &__item {
        flex: 1 1 165px;
      }
    }
  `,
})
export class IncomeFormComponent implements OnInit {
  @Input() income?: Income;
  @Output() incomeSaved = new EventEmitter<void>();
  protected modelForm?: FormGroup<IncomeForm>;
  protected readonly IncomeFormKeys = IncomeFormKeys;
  protected readonly categories = inject(CategoriesApiService).getCategories();
  protected isEdit = false;
  private readonly apiService = inject(IncomeApiService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly formBuilder = inject(NonNullableFormBuilder);

  public ngOnInit(): void {
    this.modelForm = this.initFromModel(this.income);
    this.isEdit = !!this.income;
  }

  protected save(): void {
    if (!this.modelForm || this.modelForm?.invalid) {
      return;
    }
    const request: IncomeRequest = {
      ...this.modelForm.getRawValue(),
    };
    const requestMethod =
      this.isEdit && this.income
        ? this.apiService.updateIncome(this.income?.idIncomes, request)
        : this.apiService.addIncome(request);
    requestMethod.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(res => {
      if (res) {
        this.modelForm?.reset();
        this.incomeSaved.emit();
      }
    });
  }

  private initFromModel(income?: Income): FormGroup<IncomeForm> {
    return this.formBuilder.group({
      [IncomeFormKeys.NAME]: new FormControl<string>(income?.name ?? '', {
        validators: [Validators.required, Validators.maxLength(80), Validators.minLength(3)],
        nonNullable: true,
      }),
      [IncomeFormKeys.AMOUNT]: new FormControl<number>(income?.value ?? 1, {
        validators: [
          Validators.required,
          Validators.pattern(/^\d*\.?\d{1,2}$/),
          Validators.min(0.01),
        ],
        nonNullable: true,
      }),
      [IncomeFormKeys.DESCRIPTION]: new FormControl<string>(income?.description ?? '', {
        validators: [Validators.required],
        nonNullable: true,
      }),
    });
  }
}
