import { FormControl } from '@angular/forms';
import { LabeledValue } from '../../../shared';

export enum GoalAddEditFormKeys {
  TITLE = 'targetTitle',
  AMOUNT = 'targetAmount',
  DATE = 'targetDate',
  CYCLICAL_PAYMENT_AMOUNT = 'cyclicalPaymentAmount',
  CYCLICAL_PAYMENT_INTERVAL = 'cyclicalPaymentCron',
  CURRENT_AMOUNT = 'currentAmount',
  DESCRIPTION = 'description',
}

export interface GoalAddEditForm {
  [GoalAddEditFormKeys.TITLE]: FormControl<string>;
  [GoalAddEditFormKeys.AMOUNT]: FormControl<number>;
  [GoalAddEditFormKeys.DATE]: FormControl<Date>;
  [GoalAddEditFormKeys.CYCLICAL_PAYMENT_AMOUNT]: FormControl<number | undefined>;
  [GoalAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL]: FormControl<LabeledValue | undefined>;
  [GoalAddEditFormKeys.CURRENT_AMOUNT]: FormControl<number>;
  [GoalAddEditFormKeys.DESCRIPTION]: FormControl<string | undefined>;
}
