import { Route } from '@angular/router';
import { userRoleGuard } from '../app/guards';
import { UserRole } from '../shared';
import { CalenderComponent } from './calender.component';

export const calenderRoute: Route = {
  path: 'calender',
  component: CalenderComponent,
  canMatch: [userRoleGuard(UserRole.USER)],
};
