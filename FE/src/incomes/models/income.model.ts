export interface Income {
  value: number;
  name: string;
  description: string;
  idIncomes: number;
  createdDate: string;
  isEdit?: boolean;
}

export type IncomeRequest = Omit<Income, 'idIncomes' | 'createdDate'>;
