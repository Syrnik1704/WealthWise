import { Injectable } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { DashboardMenuItem } from '../components/dashboard/dashboard-menu-item';

@Injectable({ providedIn: 'root' })
export class MenuItemService {
  private readonly footerMenuItemSubject = new ReplaySubject<DashboardMenuItem[]>();
  private readonly headerMenuItemSubject = new ReplaySubject<DashboardMenuItem[]>();
  public readonly headerMenuItems = this.headerMenuItemSubject.asObservable();
  public readonly footerMenuItems = this.footerMenuItemSubject.asObservable();
  private _headerMenuItems: DashboardMenuItem[] = [];
  private _footerMenuItems: DashboardMenuItem[] = [];

  public setGuestUserMenuItems(): void {
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
