import { SavingGoal } from '../../models';

export interface GoalAddEditDialogContent {
  isEdit: boolean;
  savingGoal?: SavingGoal;
}
