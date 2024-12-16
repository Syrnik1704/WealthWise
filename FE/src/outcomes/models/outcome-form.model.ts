import { FormControl } from '@angular/forms';

export enum OutcomeFormKeys {
  NAME = 'name',
  AMOUNT = 'amount',
  DESCRIPTION = 'description',
  CATEGORY = 'categoryName',
}

export interface OutcomeForm {
  [OutcomeFormKeys.NAME]: FormControl<string>;
  [OutcomeFormKeys.AMOUNT]: FormControl<number>;
  [OutcomeFormKeys.DESCRIPTION]: FormControl<string>;
  [OutcomeFormKeys.CATEGORY]: FormControl<string>;
}
