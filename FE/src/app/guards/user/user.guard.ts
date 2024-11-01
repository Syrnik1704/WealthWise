import { inject } from '@angular/core';
import { CanActivateChildFn, Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { Role } from '../../models/authentication/ERole';



export const USERGuard: CanActivateChildFn = (childRoute, state) => {
  const router = inject(Router);
  const authservice = inject(AuthService);
  const role = authservice.loggedUser.value?.scopes[0];

  if(role === Role.USER)
    return true;
  else
    return router.createUrlTree(['/login']);
};
