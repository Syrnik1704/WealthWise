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
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { TranslateModule } from '@ngx-translate/core';
import { Store } from '@ngxs/store';
import { tap } from 'rxjs';
import { SavingGoal } from '../../../saving-goals/models';
import { SavingGoalApiService } from '../../../saving-goals/services';
import { UserSelectors } from '../../../shared';

@Component({
  selector: 'ww-user-dashboard',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [TranslateModule, MatIconModule, MatProgressBarModule, CommonModule, MatCardModule],
  templateUrl: './user-dashboard.component.html',
  styleUrl: './user-dashboard.component.scss',
})
export class UserDashboardComponent implements OnInit {
  private readonly store = inject(Store);
  private readonly savingGoalApiService = inject(SavingGoalApiService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly cdRef = inject(ChangeDetectorRef);
  protected user = this.store.selectSnapshot(UserSelectors.user);
  protected incomingTargets: SavingGoal[] = [];
  protected mostFulfilledTargets: SavingGoal[] = [];

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
  }
}
