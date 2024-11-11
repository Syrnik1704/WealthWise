import { Routes } from '@angular/router';
import { dashboardRoutes } from '../dashboard/dashboard-routes';

import { USERGuard } from './guards/user/user.guard';
import { AfterLoginComponent } from './view/AfterLoginTest/afterLogin.component';
import { LoginComponent } from './view/login/login.component';
import { RegisterComponent } from './view/register/register.component';

export const routes: Routes = [
  ...dashboardRoutes,
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'USER',
    canActivate: [USERGuard],
    children: [{ path: 'afterLogin', component: AfterLoginComponent }],
  },
];
