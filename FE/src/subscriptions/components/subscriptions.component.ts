import { ChangeDetectionStrategy, Component, DestroyRef, inject, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { of, switchMap } from 'rxjs';
import { SubscriptionRequest } from '../models';
import { SubscriptionsListComponent } from './subscriptions-list/subscriptions-list.component';
import { SubscriptionsApiService } from '../services';
import { SubscriptionAddEditDialogContent } from './subscription-add-edit-modal/subscription-add-edit-modal-content.model';
import { SubscriptionAddEditModalComponent } from './subscription-add-edit-modal/subscription-add-edit-modal.component';

@Component({
  selector: 'ww-subscriptions',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatButtonModule, TranslateModule, SubscriptionsListComponent, MatDialogModule],
  template: `
    <div class="h1">{{ 'COMMON.SUBSCRIPTION_MANAGMENT' | translate }}</div>
    <button mat-raised-button (click)="addSubscription()">
      {{ 'SUBSCRIPTIONS.ADD_SUBSCRIPTION' | translate }}
    </button>
    <ww-subscription-list #subscriptionList></ww-subscription-list>
  `,
  styles: `
    :host {
      display: flex;
      flex-direction: column;
      gap: 24px;
      padding-top: 16px;
      padding-left: 4px;
      padding-right: 4px;
    }
  `,
})
export class SubscriptionsComponent {
  @ViewChild('subscriptionList')
  protected readonly subscriptionList?: SubscriptionsListComponent;
  private readonly dialogService = inject(MatDialog);
  private readonly destroyRef = inject(DestroyRef);
  private readonly subscriptionsApiService = inject(SubscriptionsApiService);

  protected addSubscription(): void {
    const dialogData: SubscriptionAddEditDialogContent = {
      isEdit: false,
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
          return this.subscriptionsApiService.addSubscription(result);
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(result => {
        if (result) {
          this.subscriptionList?.refreshTable();
        }
      });
  }
}
