import { Routes } from '@angular/router';
import { LoginComponent } from '../login';
import { DashboardComponent } from './components';

export const dashboardRoutes: Routes = [
  {
    path: '',
    component: DashboardComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
  },
];
