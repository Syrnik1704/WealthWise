import { inject } from '@angular/core';
import { CanActivateChildFn, Router } from '@angular/router';
import { Store } from '@ngxs/store';
import { map } from 'rxjs';
import { User, UserRole, UserSelectors } from '../../shared';

export const userRoleGuard = (userRole: UserRole): CanActivateChildFn => {
  return () => {
    const router = inject(Router);
    return inject(Store)
      .select(UserSelectors.user)
      .pipe(
        map((user?: User) => {
          if (user?.role === userRole) {
            return true;
          }
          return router.createUrlTree(['/not-found']);
        })
      );
  };
};
