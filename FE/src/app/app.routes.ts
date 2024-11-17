import { Routes } from '@angular/router';
import { dashboardRoutes } from '../dashboard/dashboard-routes';

import { UserRole } from '../shared';
import { guestGuard } from './guards/guest.guard';
import { userRoleGuard } from './guards/user.guard';
import { AfterLoginComponent } from './view/AfterLoginTest/afterLogin.component';
import { LoginComponent } from './view/login/login.component';
import { RegisterComponent } from './view/register/register.component';

export const routes: Routes = [
  ...dashboardRoutes,
  { path: 'login', component: LoginComponent, canActivate: [guestGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [guestGuard] },
  {
    path: 'USER',
    canActivate: [userRoleGuard(UserRole.USER)],
    children: [{ path: 'afterLogin', component: AfterLoginComponent }],
  },
];
