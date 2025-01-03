import { AsyncPipe } from '@angular/common';
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
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { Category } from '../../../shared';
import { CategoriesApiService } from '../../../shared/services/categories-service';
import { Outcome, OutcomeRequest } from '../../models';
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
    MatButtonModule,
    MatTooltipModule,
    AsyncPipe,
  ],
  template: `
    @if (modelForm) {
      <form class="outcome-form" [formGroup]="modelForm">
        <mat-form-field class="outcome-form__item">
          <mat-label>{{ 'OUTCOMES.TABLE.NAME' | translate }}</mat-label>
          <input matInput [formControlName]="OutcomeFormKeys.NAME" />
          @if (modelForm.controls[OutcomeFormKeys.NAME].hasError('required')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.REQUIRED' | translate: { value: 'OUTCOMES.TABLE.NAME' | translate }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.REQUIRED' | translate: { value: 'OUTCOMES.TABLE.NAME' | translate }
              }}</mat-error
            >
          }
          @if (modelForm.controls[OutcomeFormKeys.NAME].hasError('minlength')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.MIN_LENGTH'
                  | translate
                    : {
                        minlength:
                          modelForm.controls[OutcomeFormKeys.NAME].errors?.['minlength']
                            .requiredLength,
                      }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.MIN_LENGTH'
                  | translate
                    : {
                        minlength:
                          modelForm.controls[OutcomeFormKeys.NAME].errors?.['minlength']
                            .requiredLength,
                      }
              }}</mat-error
            >
          }
          @if (modelForm.controls[OutcomeFormKeys.NAME].hasError('maxlength')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.MAX_LENGTH'
                  | translate
                    : {
                        maxlength:
                          modelForm.controls[OutcomeFormKeys.NAME].errors?.['maxlength']
                            .requiredLength,
                      }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.MAX_LENGTH'
                  | translate
                    : {
                        minlength:
                          modelForm.controls[OutcomeFormKeys.NAME].errors?.['maxlength']
                            .requiredLength,
                      }
              }}</mat-error
            >
          }
        </mat-form-field>
        <mat-form-field class="outcome-form__item">
          <mat-label>{{ 'OUTCOMES.TABLE.AMOUNT' | translate }}</mat-label>
          <input matInput [formControlName]="OutcomeFormKeys.AMOUNT" />
          @if (modelForm.controls[OutcomeFormKeys.AMOUNT].hasError('min')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.MIN'
                  | translate
                    : {
                        min: modelForm.controls[OutcomeFormKeys.AMOUNT].errors?.['min'].min,
                      }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.MIN'
                  | translate
                    : {
                        min: modelForm.controls[OutcomeFormKeys.AMOUNT].errors?.['min'].min,
                      }
              }}</mat-error
            >
          }
          @if (modelForm.controls[OutcomeFormKeys.AMOUNT].hasError('pattern')) {
            <mat-error [matTooltip]="'COMMON.ERRORS.AMOUNT' | translate" class="text-ellipsis">{{
              'COMMON.ERRORS.AMOUNT' | translate
            }}</mat-error>
          }
          @if (modelForm.controls[OutcomeFormKeys.AMOUNT].hasError('required')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.REQUIRED' | translate: { value: 'OUTCOMES.TABLE.AMOUNT' | translate }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.REQUIRED' | translate: { value: 'OUTCOMES.TABLE.AMOUNT' | translate }
              }}</mat-error
            >
          }
        </mat-form-field>
        <mat-form-field class="outcome-form__item">
          <mat-label>{{ 'OUTCOMES.TABLE.DESCRIPTION' | translate }}</mat-label>
          <textarea matInput [formControlName]="OutcomeFormKeys.DESCRIPTION"></textarea>
          @if (modelForm.controls[OutcomeFormKeys.DESCRIPTION].hasError('required')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.REQUIRED'
                  | translate: { value: 'OUTCOMES.TABLE.DESCRIPTION' | translate }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.REQUIRED'
                  | translate: { value: 'OUTCOMES.TABLE.DESCRIPTION' | translate }
              }}</mat-error
            >
          }
        </mat-form-field>
        <mat-form-field class="outcome-form__item">
          <mat-label>{{ 'OUTCOMES.TABLE.CATEGORY' | translate }}</mat-label>
          <mat-select [formControlName]="OutcomeFormKeys.CATEGORY">
            <mat-option>-</mat-option>
            @for (category of categories | async; track category) {
              <mat-option [value]="category">
                {{ category.name | translate }}
              </mat-option>
            }
          </mat-select>
          @if (modelForm.controls[OutcomeFormKeys.CATEGORY].hasError('required')) {
            <mat-error
              [matTooltip]="
                'COMMON.ERRORS.REQUIRED'
                  | translate: { value: 'OUTCOMES.TABLE.CATEGORY' | translate }
              "
              class="text-ellipsis"
              >{{
                'COMMON.ERRORS.REQUIRED'
                  | translate: { value: 'OUTCOMES.TABLE.CATEGORY' | translate }
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
    .outcome-form {
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
export class OutcomeFormComponent implements OnInit {
  @Input() outcome?: Outcome;
  @Output() outcomeSaved = new EventEmitter<void>();
  protected modelForm?: FormGroup<OutcomeForm>;
  protected readonly OutcomeFormKeys = OutcomeFormKeys;
  protected readonly categories = inject(CategoriesApiService).getCategories();
  protected isEdit = false;
  private readonly apiService = inject(OutcomeApiService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly formBuilder = inject(NonNullableFormBuilder);

  public ngOnInit(): void {
    this.modelForm = this.initFromModel(this.outcome);
    this.isEdit = !!this.outcome;
  }

  protected save(): void {
    if (!this.modelForm || this.modelForm?.invalid) {
      return;
    }
    const request: OutcomeRequest = {
      ...this.modelForm.getRawValue(),
      categoryName: this.modelForm.controls[OutcomeFormKeys.CATEGORY].value?.name ?? '',
    };
    const requestMethod =
      this.isEdit && this.outcome
        ? this.apiService.updateOutcome(this.outcome?.idExpenses, request)
        : this.apiService.addOutcome(request);
    requestMethod.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(res => {
      if (res) {
        this.modelForm?.reset();
        this.outcomeSaved.emit();
      }
    });
  }

  private initFromModel(outcome?: Outcome): FormGroup<OutcomeForm> {
    return this.formBuilder.group({
      [OutcomeFormKeys.NAME]: new FormControl<string>(outcome?.name ?? '', {
        validators: [Validators.required, Validators.maxLength(80), Validators.minLength(3)],
        nonNullable: true,
      }),
      [OutcomeFormKeys.AMOUNT]: new FormControl<number>(outcome?.amount ?? 1, {
        validators: [
          Validators.required,
          Validators.min(0.01),
          Validators.pattern(/^\d*\.?\d{1,2}$/),
        ],
        nonNullable: true,
      }),
      [OutcomeFormKeys.CATEGORY]: new FormControl<Category | null>(outcome?.category ?? null, {
        validators: [Validators.required],
        nonNullable: true,
      }),
      [OutcomeFormKeys.DESCRIPTION]: new FormControl<string>(outcome?.description ?? '', {
        validators: [Validators.required],
        nonNullable: true,
      }),
    });
  }
}
