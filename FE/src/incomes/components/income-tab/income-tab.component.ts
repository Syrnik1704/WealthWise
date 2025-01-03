import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { TranslateModule } from '@ngx-translate/core';
import { IncomeFormComponent } from '../income-form';
import { IncomesListComponent } from '../incomes-list';

@Component({
  selector: 'ww-income-tab',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatButtonModule, TranslateModule, IncomeFormComponent, IncomesListComponent],
  template: `
    <div class="h1">{{ 'COMMON.INCOMES' | translate }}</div>
    <ww-income-form (incomeSaved)="incomesList.refreshTable()" />
    <ww-incomes-list #incomesList />
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
export class IncomeTabComponent {}
