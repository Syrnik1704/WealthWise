import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { ConfirmDialogContent } from '../models';

@Component({
  selector: 'ww-confirm-dialog',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, TranslateModule],
  template: `
    <h2 mat-dialog-title>{{ config.header | translate }}</h2>
    @if (config.message) {
      <mat-dialog-content>
        {{ config.message | translate }}
      </mat-dialog-content>
    }
    <mat-dialog-actions>
      <button mat-button [mat-dialog-close]="false">
        {{ 'COMMON.BUTTONS.REJECT' | translate }}
      </button>
      <button mat-raised-button [mat-dialog-close]="true" cdkFocusInitial>
        {{ 'COMMON.BUTTONS.CONFIRM' | translate }}
      </button>
    </mat-dialog-actions>
  `,
})
export class ConfirmDialogComponent {
  readonly config = inject<ConfirmDialogContent>(MAT_DIALOG_DATA);
}
