import { FormControl } from '@angular/forms';

export enum SubscriptionAddEditFormKeys {
  TITLE = 'subscriptionTitle',
  AMOUNT = 'amount',
  DATE = 'creationDate',
  CYCLICAL_PAYMENT_DATE = 'cyclicalPaymentDate',
  DESCRIPTION = 'description',
}

export interface SubscriptionAddEditForm {
  [SubscriptionAddEditFormKeys.TITLE]: FormControl<string>;
  [SubscriptionAddEditFormKeys.AMOUNT]: FormControl<number>;
  [SubscriptionAddEditFormKeys.DATE]: FormControl<Date>;
  [SubscriptionAddEditFormKeys.CYCLICAL_PAYMENT_DATE]: FormControl<number>;
  [SubscriptionAddEditFormKeys.DESCRIPTION]: FormControl<string | undefined>;
}