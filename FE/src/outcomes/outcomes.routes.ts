import { Route } from '@angular/router';
import { userRoleGuard } from '../app/guards';
import { UserRole } from '../shared';
import { OutcomeTabComponent } from './components';

export const outcomeRoute: Route = {
  path: 'outcomes',
  component: OutcomeTabComponent,
  canMatch: [userRoleGuard(UserRole.USER)],
};
