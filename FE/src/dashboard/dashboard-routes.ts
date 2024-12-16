import { Routes } from '@angular/router';
import { userRoleGuard } from '../app/guards';
import { guestGuard } from '../app/guards/guest.guard';
import { outcomeRoute } from '../outcomes/outcomes.routes';
import { savingGoalsRoute } from '../saving-goals/saving-goals.routes';
import { UserRole } from '../shared';
import { MenuComponent } from './components';
import { GuestDashboardComponent } from './components/guest-dashboard';
import { UserDashboardComponent } from './components/user-dashboard';
import { AdminDashboardComponent } from './components/admin-dashboard';

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
        canMatch: [userRoleGuard(UserRole.ADMIN)],
      },
      outcomeRoute,
      savingGoalsRoute,
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
    ],
  },
];
