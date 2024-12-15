import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { TranslateModule } from '@ngx-translate/core';
import { OutcomeFormComponent } from '../outcome-form';
import { OutcomesListComponent } from '../outcomes-list';

@Component({
  selector: 'ww-outcome-tab',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatButtonModule, TranslateModule, OutcomeFormComponent, OutcomesListComponent],
  template: `
    <div class="h1">{{ 'COMMON.OUTCOMES' | translate }}</div>
    <ww-outcome-form (outcomeSaved)="outcomesList.refreshTable()" />
    <ww-outcomes-list #outcomesList />
  `,
  styles: [
    `
      :host {
        padding: 8px;
        display: flex;
        flex-direction: column;
      }
    `,
  ],
})
export class OutcomeTabComponent {}
