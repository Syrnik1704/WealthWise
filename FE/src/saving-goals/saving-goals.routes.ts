import { Route } from '@angular/router';
import { userRoleGuard } from '../app/guards';
import { UserRole } from '../shared';
import { SavingGoalsComponent } from './components/saving-goals.component';

export const savingGoalsRoute: Route = {
  path: 'saving-goals',
  component: SavingGoalsComponent,
  canMatch: [userRoleGuard(UserRole.USER)],
};
