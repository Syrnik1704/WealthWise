import { CommonModule, DatePipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Observable, of, switchMap } from 'rxjs';
import { ConfirmDialogComponent, ConfirmDialogContent, IntervalService } from '../../../shared';
import { Subscription, SubscriptionRequest } from '../../models';
import { SubscriptionsApiService } from '../../services/subscriptions-api.service';
import { SubscriptionAddEditDialogContent } from '../subscription-add-edit-modal/subscription-add-edit-modal-content.model';
import { SubscriptionAddEditModalComponent } from '../subscription-add-edit-modal/subscription-add-edit-modal.component';
import { SubscriptionCyclicalPaymentDatePipe } from '../../pipes/subscription-cyclical-payment-date.pipe';

@Component({
  selector: 'ww-subscription-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatListModule,
    TranslateModule,
    CommonModule,
    DatePipe,
    MatCardModule,
    MatIconModule,
    MatMenuModule,
    MatButtonModule,
    SubscriptionCyclicalPaymentDatePipe
  ],
  templateUrl: './subscriptions-list.component.html',
  styleUrl: './subscriptions-list.component.scss',
})
export class SubscriptionsListComponent {
  private readonly subscriptionsApiService = inject(SubscriptionsApiService);
  protected readonly intervalService = inject(IntervalService);
  protected subscriptionsData$: Observable<Subscription[]> = this.subscriptionsApiService.getSubscriptionsList();
  private readonly cdRef = inject(ChangeDetectorRef);
  private readonly dialogService = inject(MatDialog);
  private readonly destroyRef = inject(DestroyRef);
  private readonly translate = inject(TranslateService);

  public refreshTable(): void {
    this.subscriptionsData$ = this.subscriptionsApiService.getSubscriptionsList();
    this.cdRef.detectChanges();
  }

  protected editSubscription(subscription: Subscription): void {
    const dialogData: SubscriptionAddEditDialogContent = {
      isEdit: true,
      subscription,
    };
    const dialogRef = this.dialogService.open<
      SubscriptionAddEditModalComponent,
      SubscriptionAddEditDialogContent,
      SubscriptionRequest
    >(SubscriptionAddEditModalComponent, {
      hasBackdrop: true,
      closeOnNavigation: true,
      data: dialogData,
      maxWidth: '100vw',
      minWidth: '40vw',
    });

    dialogRef
      .afterClosed()
      .pipe(
        switchMap(result => {
          if (!result) {
            return of(false);
          }
          return this.subscriptionsApiService.updateGoal(subscription.subscriptionId, result);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(result => {
        if (result) {
          this.refreshTable();
        }
      });
  }

  protected removeSubscription(subscription: Subscription): void {
    const dialogData: ConfirmDialogContent = {
      header: 'SUBSCRIPTIONS.MODALS.REMOVE.HEADER',
      message: `${this.translate.instant('SUBSCRIPTIONS.MODALS.REMOVE.MESSAGE')} ${subscription.subscriptionTitle}`,
    };
    const dialogRef = this.dialogService.open<
      ConfirmDialogComponent,
      ConfirmDialogContent,
      boolean
    >(ConfirmDialogComponent, {
      hasBackdrop: true,
      closeOnNavigation: true,
      data: dialogData,
      minWidth: '40vw',
    });

    dialogRef
      .afterClosed()
      .pipe(
        switchMap(result => {
          if (!result) {
            return of(false);
          }
          return this.subscriptionsApiService.removeSubscription(subscription.subscriptionId, subscription.subscriptionTitle);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(result => {
        if (result) {
          this.refreshTable();
        }
      });
  }

}
