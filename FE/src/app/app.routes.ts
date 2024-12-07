import { Routes } from '@angular/router';
import { dashboardRoutes } from '../dashboard/dashboard-routes';
import { authRoute } from '../auth/auth.routes';

export const routes: Routes = [
  ...dashboardRoutes,
  ...authRoute,
];
