import { Route } from '@angular/router';
import { userRoleGuard } from '../app/guards';
import { UserRole } from '../shared';
import { IncomeTabComponent } from './components';

export const incomesRoute: Route = {
  path: 'incomes',
  component: IncomeTabComponent,
  canMatch: [userRoleGuard(UserRole.USER)],
};
