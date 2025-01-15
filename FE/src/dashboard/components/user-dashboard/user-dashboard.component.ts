import { CdkAccordionModule } from '@angular/cdk/accordion';
import { CommonModule } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  inject,
  OnInit,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslateModule } from '@ngx-translate/core';
import { Store } from '@ngxs/store';
import { ChartDataset, ChartOptions } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { map, tap } from 'rxjs';
import { Outcome } from '../../../outcomes/models';
import { OutcomeApiService } from '../../../outcomes/services/outcomes-api.service';
import { SavingGoal } from '../../../saving-goals/models';
import { SavingGoalApiService } from '../../../saving-goals/services';
import { UserSelectors } from '../../../shared';
import { SubscriptionsApiService } from '../../../subscriptions/services';
import { UserApiService } from '../../services/user.service';
@Component({
  selector: 'ww-user-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    TranslateModule,
    MatIconModule,
    MatProgressBarModule,
    CommonModule,
    MatCardModule,
    CdkAccordionModule,
    MatButtonModule,
    MatTooltipModule,
    BaseChartDirective,
    MatDatepickerModule,
    FormsModule,
    MatFormFieldModule,
    MatIconModule,
    MatInput,
    MatLabel,
  ],
  providers: [provideNativeDateAdapter()],
  templateUrl: './user-dashboard.component.html',
  styleUrl: './user-dashboard.component.scss',
})
export class UserDashboardComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly savingGoalApiService = inject(SavingGoalApiService);
  private readonly outcomesApiService = inject(OutcomeApiService);
  private readonly subscriptionApiService = inject(SubscriptionsApiService);
  private readonly userApiService = inject(UserApiService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly cdRef = inject(ChangeDetectorRef);
  protected user = this.store.selectSnapshot(UserSelectors.user);
  protected incomingTargets: SavingGoal[] = [];
  protected mostFulfilledTargets: SavingGoal[] = [];
  protected outcomeRange = { startDate: this.getDateMinusDays(30), endDate: new Date() };
  protected pieChartOptions: ChartOptions<'pie'> = {
    responsive: false,
  };
  protected pieChartLabels: string[] = [];
  protected pieChartDatasets: ChartDataset<'pie', number[]>[] = [{ data: [] }];
  protected incominSubscriptionPayment: {
    subscriptionTitle: string;
    amount: number;
    cyclicalPaymentDate: number;
    paymentIn: number;
  }[] = [];
  protected totalExpenses?: number;
  protected userBalance: number = 0;
  private outcomes: Outcome[] = [];

  public ngOnInit(): void {
    this.savingGoalApiService
      .getGoalList()
      .pipe(
        tap(goals => {
          this.incomingTargets = goals.slice(0, 3);
          this.mostFulfilledTargets = goals
            .filter(e => e.targetAmount > e.currentAmount)
            .sort((a, b) => {
              if (a.progress === undefined || b.progress === undefined) {
                return 0;
              }
              return -(a.progress - b.progress);
            })
            .slice(0, 3);
          this.cdRef.detectChanges();
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
    this.outcomesApiService
      .getOutcomeList()
      .pipe(
        tap(outcomes => {
          this.outcomes = outcomes;
          this.preparePieData();
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();
    this.subscriptionApiService
      .getSubscriptionsList()
      .pipe(
        map(subscriptions => {
          const todayDay = new Date().getDate();
          return subscriptions.map(sub => ({
            subscriptionTitle: sub.subscriptionTitle,
            amount: sub.amount,
            cyclicalPaymentDate: sub.cyclicalPaymentDate,
            paymentIn: sub.cyclicalPaymentDate - todayDay,
          }));
        }),
        tap(subs => {
          this.incominSubscriptionPayment = subs.filter(
            sub => sub.paymentIn < 7 && sub.paymentIn > 0
          );
          this.cdRef.detectChanges();
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe();

    this.userApiService.loadBalance().pipe(
      tap(balance => this.userBalance = balance),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();
  }

  protected preparePieData(): void {
    const outcomesMap: Record<string, number> = this.outcomes.reduce(
      (acc, outcome) => {
        const creationDate = new Date(outcome.createdDate);
        if (this.isDateBetween(creationDate, this.outcomeRange)) {
          const categoryName = outcome.category.name;
          if (acc[categoryName]) {
            acc[categoryName] += outcome.amount;
          } else {
            acc[categoryName] = outcome.amount;
          }
        }
        return acc;
      },
      {} as Record<string, number>
    );
    this.totalExpenses = Object.values(outcomesMap).reduce((acc, curr) => acc + curr, 0);
    this.pieChartLabels = Object.keys(outcomesMap);
    this.pieChartDatasets = [{ data: Object.values(outcomesMap) }];
    this.cdRef.detectChanges();
  }

  private isDateBetween(date: Date, range: { startDate: Date; endDate: Date }): boolean {
    return date.getTime() < range.endDate.getTime() && date.getTime() > range.startDate.getTime();
  }

  private getDateMinusDays(days: number): Date {
    return new Date(new Date().getTime() - days * 24 * 60 * 60 * 1000);
  }
}
