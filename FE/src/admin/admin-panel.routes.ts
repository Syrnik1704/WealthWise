import { Route } from '@angular/router';
import { UserRole } from '../shared';
import { adminRoleGuard } from '../app/guards/admin.guard';
import { AdminPanelComponent } from './components/admin-panel.component';


export const adminPanelRoute: Route = {
  path: 'admin-panel',
  component: AdminPanelComponent,
  canMatch: [adminRoleGuard(UserRole.ADMIN)],
};
