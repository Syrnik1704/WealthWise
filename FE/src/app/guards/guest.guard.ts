import { inject } from '@angular/core';
import { CanActivateChildFn } from '@angular/router';
import { Store } from '@ngxs/store';
import { map } from 'rxjs';
import { User, UserSelectors } from '../../shared';

export const guestGuard: CanActivateChildFn = () => {
  return inject(Store)
    .select(UserSelectors.user)
    .pipe(map((user?: User) => !user));
};
