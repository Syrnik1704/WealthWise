import { MediaMatcher } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenav, MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { IsActiveMatchOptions, RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { MenuItemService } from '../../services';

@Component({
  selector: 'ww-menu',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    TranslateModule,
    MatButtonModule,
    RouterModule,
    MatSidenavModule,
    MatToolbarModule,
    MatIconModule,
    MatListModule,
  ],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss'],
})
export class MenuComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild('snav') sideNav?: MatSidenav;
  protected readonly mobileQuery = inject(MediaMatcher).matchMedia('(max-width: 600px)');
  private readonly cdRef = inject(ChangeDetectorRef);
  private readonly menuItemsService = inject(MenuItemService);
  private mediaListener = () => {
    this.cdRef.detectChanges();
  };
  protected readonly headerMenuItems$ = this.menuItemsService.headerMenuItems;
  protected readonly footerMenuItems$ = this.menuItemsService.footerMenuItems;
  protected readonly routerLinkMatch: IsActiveMatchOptions = {
    matrixParams: 'exact',
    queryParams: 'exact',
    paths: 'exact',
    fragment: 'exact',
  };

  public ngOnInit(): void {
    this.mobileQuery.addListener(this.mediaListener);
    this.menuItemsService.initWatchUser();
  }

  public ngAfterViewInit(): void {
    this.menuItemsService.sideNav = this.sideNav;
  }

  public ngOnDestroy(): void {
    this.mobileQuery.removeListener(this.mediaListener);
  }

  protected onClickMenuItem(): void {
    if (this.mobileQuery.matches) {
      this.sideNav?.close();
    }
  }
}
