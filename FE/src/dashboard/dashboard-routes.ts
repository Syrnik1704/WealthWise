import { Routes } from '@angular/router';
import { adminPanelRoute } from '../admin/admin-panel.routes';
import { userRoleGuard } from '../app/guards';
import { adminRoleGuard } from '../app/guards/admin.guard';
import { guestGuard } from '../app/guards/guest.guard';
import { calenderRoute } from '../calender';
import { outcomeRoute } from '../outcomes/outcomes.routes';
import { savingGoalsRoute } from '../saving-goals/saving-goals.routes';
import { UserRole } from '../shared';
import { subscriptionsRoute } from '../subscriptions/subscriptions.routes';
import { MenuComponent } from './components';
import { AdminDashboardComponent } from './components/admin-dashboard';
import { GuestDashboardComponent } from './components/guest-dashboard';
import { UserDashboardComponent } from './components/user-dashboard';

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
        component: AdminDashboardComponent,
        canMatch: [adminRoleGuard(UserRole.ADMIN)],
      },
      outcomeRoute,
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
