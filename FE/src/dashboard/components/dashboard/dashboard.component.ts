import { MediaMatcher } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule, MatNavList } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { MenuItemService } from '../../services/menu-items.service';

@Component({
  selector: 'ww-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    TranslateModule,
    MatButtonModule,
    RouterModule,
    MatSidenavModule,
    MatToolbarModule,
    MatNavList,
    MatIconModule,
    MatListModule,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit, OnDestroy {
  protected readonly mobileQuery = inject(MediaMatcher).matchMedia('(max-width: 600px)');
  private readonly cdRef = inject(ChangeDetectorRef);
  private readonly menuItemsService = inject(MenuItemService);
  private mediaListener = () => {
    this.cdRef.detectChanges();
  };
  protected readonly headerMenuItems$ = this.menuItemsService.headerMenuItems;
  protected readonly footerMenuItems$ = this.menuItemsService.footerMenuItems;

  public ngOnInit(): void {
    this.mobileQuery.addListener(this.mediaListener);
    this.menuItemsService.setGuestUserMenuItems();
  }

  public ngOnDestroy(): void {
    this.mobileQuery.removeListener(this.mediaListener);
  }
}
