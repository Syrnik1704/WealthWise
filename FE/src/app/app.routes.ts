import { Routes } from '@angular/router';
import { LoginComponent } from './view/login/login/login.component';
import { DashboardComponent } from '../dashboard';
import { AfterLoginComponent } from './view/AfterLoginTest/afterLogin.component';
import { USERGuard } from './guards/user/user.guard';
import { RegisterComponent } from './view/register/register.component';


export const routes: Routes = [
  { path: '', component: DashboardComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'USER',
    canActivate: [USERGuard],
    children: [
      { path: 'afterLogin', component: AfterLoginComponent },
    ]
  },

];