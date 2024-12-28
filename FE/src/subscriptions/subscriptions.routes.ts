import { Route } from '@angular/router';
import { userRoleGuard } from '../app/guards';
import { UserRole } from '../shared';
import { SubscriptionsComponent } from './components/subscriptions.component';

export const subscriptionsRoute: Route = {
  path: 'subscriptions',
  component: SubscriptionsComponent,
  canMatch: [userRoleGuard(UserRole.USER)],
};
