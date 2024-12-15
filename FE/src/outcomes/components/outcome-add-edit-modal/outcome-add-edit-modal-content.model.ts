import { SavingGoal } from '../../models';

export interface OutcomeAddEditDialogContent {
  isEdit: boolean;
  savingGoal?: SavingGoal;
}
