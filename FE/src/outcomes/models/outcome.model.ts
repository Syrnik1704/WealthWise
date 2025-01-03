import { Category } from '../../shared';

export interface Outcome {
  category: Category;
  amount: number;
  name: string;
  description: string;
  idExpenses: number;
  createdDate: string;
  isEdit?: boolean;
}

export type OutcomeRequest = Omit<Outcome, 'idExpenses' | 'createdDate' | 'category'> & {
  categoryName: string;
};
