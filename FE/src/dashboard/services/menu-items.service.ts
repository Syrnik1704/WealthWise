import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatSidenav } from '@angular/material/sidenav';
import { Store } from '@ngxs/store';
import { ReplaySubject } from 'rxjs';
import { AuthService } from '../../app/services/auth/auth.service';
import { User, UserRole, UserSelectors } from '../../shared';
import { MenuItem } from '../components/menu/menu-item';

@Injectable({ providedIn: 'root' })
export class MenuItemService {
  private readonly footerMenuItemSubject = new ReplaySubject<MenuItem[]>();
  private readonly headerMenuItemSubject = new ReplaySubject<MenuItem[]>();
  public readonly headerMenuItems = this.headerMenuItemSubject.asObservable();
  public readonly footerMenuItems = this.footerMenuItemSubject.asObservable();
  private _headerMenuItems: MenuItem[] = [];
  private _footerMenuItems: MenuItem[] = [];
  private readonly authService = inject(AuthService);
  private readonly store = inject(Store);
  private readonly destroyRef = inject(DestroyRef);
  public sideNav?: MatSidenav;

  public initWatchUser(): void {
    this.store
      .select(UserSelectors.user)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((user?: User) => {
        switch (user?.role) {
          case UserRole.USER:
            this.setLoggerUserMenuItems();
            break;
          case UserRole.ADMIN:
            this.setAdminUserMenuItems();
            break;
          default:
            this.setGuestUserMenuItems();
        }
      });
  }

  private setAdminUserMenuItems(): void {
    this._footerMenuItems = [
      {
        label: 'COMMON.LOGOUT',
        action: () => {
          this.authService.logout();
          this.sideNav?.close();
        },
      },
    ];
    this._headerMenuItems = [
      {
        route: 'admin-panel',
        label: 'COMMON.ADMIN_PANEL',
      },
    ];
    this.emitMenuItems();
  }

  private setLoggerUserMenuItems(): void {
    this._footerMenuItems = [
      {
        label: 'COMMON.LOGOUT',
        action: () => {
          this.authService.logout();
          this.sideNav?.close();
        },
      },
    ];
    this._headerMenuItems = [
      {
        route: 'saving-goals',
        label: 'COMMON.SAVING_GOALS',
      },
      {
        route: 'outcomes',
        label: 'COMMON.OUTCOMES',
      },
      {
        route: 'incomes',
        label: 'COMMON.INCOMES',
      },
      {
        route: 'subscription',
        label: 'COMMON.SUBSCRIPTION_MANAGMENT',
      },
    ];
    this.emitMenuItems();
  }

  private setGuestUserMenuItems(): void {
    this._footerMenuItems = [
      {
        route: 'login',
        label: 'COMMON.LOGIN',
      },
      {
        route: 'register',
        label: 'COMMON.REGISTER',
      },
    ];
    this._headerMenuItems = [
      {
        route: '',
        fragment: 'reason',
        label: 'COMMON.REASONS',
        icon: 'verified',
      },
      {
        route: '',
        fragment: 'features',
        label: 'COMMON.FEATURES',
        icon: 'checklist',
      },
      {
        route: '',
        fragment: 'userInterface',
        label: 'COMMON.UI',
        icon: 'preview',
      },
    ];
    this.emitMenuItems();
  }

  private emitMenuItems(): void {
    this.headerMenuItemSubject.next(this._headerMenuItems);
    this.footerMenuItemSubject.next(this._footerMenuItems);
  }
}
