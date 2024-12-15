export interface Outcome {
  category: string;
  value: number;
}

export type SavingGoalRequest = Omit<SavingGoal, 'targetId'>;
