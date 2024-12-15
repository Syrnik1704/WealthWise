import { FormControl } from '@angular/forms';
import { LabeledValue } from '../../../shared';

export enum OutcomeAddEditFormKeys {
  TITLE = 'targetTitle',
  AMOUNT = 'targetAmount',
  DATE = 'targetDate',
  CYCLICAL_PAYMENT_AMOUNT = 'cyclicalPaymentAmount',
  CYCLICAL_PAYMENT_INTERVAL = 'cyclicalPaymentCron',
  CURRENT_AMOUNT = 'currentAmount',
  DESCRIPTION = 'description',
}

export interface OutcomeAddEditForm {
  [OutcomeAddEditFormKeys.TITLE]: FormControl<string>;
  [OutcomeAddEditFormKeys.AMOUNT]: FormControl<number>;
  [OutcomeAddEditFormKeys.DATE]: FormControl<Date>;
  [OutcomeAddEditFormKeys.CYCLICAL_PAYMENT_AMOUNT]: FormControl<number | undefined>;
  [OutcomeAddEditFormKeys.CYCLICAL_PAYMENT_INTERVAL]: FormControl<LabeledValue | undefined>;
  [OutcomeAddEditFormKeys.CURRENT_AMOUNT]: FormControl<number>;
  [OutcomeAddEditFormKeys.DESCRIPTION]: FormControl<string | undefined>;
}
