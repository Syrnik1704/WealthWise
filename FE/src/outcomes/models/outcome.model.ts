export interface Outcome {
  category: string;
  amount: number;
  name: string;
  description: string;
  idExpenses: number;
  createdDate: Date;
}

export type OutcomeRequest = Omit<Outcome, 'idExpenses' | 'createdDate' | 'category'> & {
  categoryName: string;
};
