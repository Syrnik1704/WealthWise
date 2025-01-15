import { Routes } from '@angular/router';
import { adminPanelRoute } from '../admin/admin-panel.routes';
import { userRoleGuard } from '../app/guards';
import { adminRoleGuard } from '../app/guards/admin.guard';
import { guestGuard } from '../app/guards/guest.guard';
import { calenderRoute } from '../calender';
import { incomesRoute } from '../incomes/incomes.routes';
import { outcomeRoute } from '../outcomes/outcomes.routes';
import { savingGoalsRoute } from '../saving-goals/saving-goals.routes';
import { UserRole } from '../shared';
import { subscriptionsRoute } from '../subscriptions/subscriptions.routes';
import { MenuComponent } from './components';
import { GuestDashboardComponent } from './components/guest-dashboard';
import { UserDashboardComponent } from './components/user-dashboard';
import { AdminPanelComponent } from '../admin/components/admin-panel.component';

export const dashboardRoutes: Routes = [
  {
    path: '',
    component: MenuComponent,
    children: [
      {
        path: 'dashboard',
        component: GuestDashboardComponent,
        canMatch: [guestGuard],
      },
      {
        path: 'dashboard',
        component: UserDashboardComponent,
        canMatch: [userRoleGuard(UserRole.USER)],
      },
      {
        path: 'dashboard',
        component: AdminPanelComponent,
        canMatch: [adminRoleGuard(UserRole.ADMIN)],
      },
      outcomeRoute,
      incomesRoute,
      savingGoalsRoute,
      subscriptionsRoute,
      calenderRoute,
      adminPanelRoute,
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
    ],
  },
];
