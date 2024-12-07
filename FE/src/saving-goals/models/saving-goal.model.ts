export interface SavingGoal {
  targetId: number;
  targetTitle: string;
  targetAmount: number;
  targetDate: Date;
  cyclicalPaymentAmount: number;
  cyclicalPaymentCron: string;
  currentAmount: number;
  description: string;
}
