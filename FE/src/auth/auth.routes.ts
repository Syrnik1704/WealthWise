import { Routes } from '@angular/router';
import { LoginFormComponent } from './components/login-form/login-form.component';
import { guestGuard } from '../app/guards/guest.guard';
import { RegisterFormComponent } from './components/register-form/register-form.component';

export const authRoute: Routes = [
  {
    path: 'login',
    component: LoginFormComponent,
    canMatch: [guestGuard],
  },
  {
    path: 'register',
    component: RegisterFormComponent,
    canMatch: [guestGuard]
  },
];
