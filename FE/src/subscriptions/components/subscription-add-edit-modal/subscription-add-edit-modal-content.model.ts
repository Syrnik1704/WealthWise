import { Subscription } from '../../models';

export interface SubscriptionAddEditDialogContent {
  isEdit: boolean;
  subscription?: Subscription;
}
