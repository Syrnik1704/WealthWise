import { FormControl } from '@angular/forms';

export enum IncomeFormKeys {
  NAME = 'name',
  AMOUNT = 'value',
  DESCRIPTION = 'description',
}

export interface IncomeForm {
  [IncomeFormKeys.NAME]: FormControl<string>;
  [IncomeFormKeys.AMOUNT]: FormControl<number>;
  [IncomeFormKeys.DESCRIPTION]: FormControl<string>;
}
