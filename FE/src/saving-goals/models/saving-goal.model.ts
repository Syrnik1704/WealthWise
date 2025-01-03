export interface SavingGoal {
  targetId: number;
  targetTitle: string;
  targetAmount: number;
  targetDate: string;
  currentAmount: number;
  creationDate: string;
  cyclicalPaymentAmount?: number;
  cyclicalPaymentCron?: string;
  description?: string;
  progress?: number;
}

export type SavingGoalRequest = Omit<SavingGoal, 'targetId' | 'creationDate'>;
