export interface SavingGoal {
  targetId: number;
  targetTitle: string;
  targetAmount: number;
  targetDate: Date;
  currentAmount: number;
  cyclicalPaymentAmount?: number;
  cyclicalPaymentCron?: string;
  description?: string;
}

export type SavingGoalRequest = Omit<SavingGoal, 'targetId'>;
