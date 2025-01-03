import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { TranslateModule } from '@ngx-translate/core';
import { FastIncome } from '../models/fast-income';

@Component({
  selector: 'ww-fast-income-dialog',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    TranslateModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
  ],
  template: `
    <h2 mat-dialog-title>
      {{ 'SHARED.FAST_INCOME_MODAL.HEADER' | translate: { goal: config.goal } }}
    </h2>

    <mat-dialog-content>
      <mat-form-field>
        <mat-label>{{ 'SHARED.FAST_INCOME_MODAL.AMOUNT' | translate }}</mat-label>
        <input
          #amountControl="ngModel"
          [(ngModel)]="amount"
          required
          matInput
          type="number"
          [max]="config.maxIncome" />
        @if (amountControl.hasError('max')) {
          <mat-error>{{
            'COMMON.ERRORS.MAX'
              | translate
                : {
                    max: config.maxIncome,
                  }
          }}</mat-error>
        }
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-button [mat-dialog-close]="false">
        {{ 'COMMON.BUTTONS.CANCEL' | translate }}
      </button>
      <button
        mat-raised-button
        [disabled]="amountControl.invalid"
        [mat-dialog-close]="amount"
        cdkFocusInitial>
        {{ 'COMMON.BUTTONS.SAVE' | translate }}
      </button>
    </mat-dialog-actions>
  `,
})
export class FastIncomeDialogComponent {
  readonly config = inject<FastIncome>(MAT_DIALOG_DATA);
  protected amount = 0;
}
