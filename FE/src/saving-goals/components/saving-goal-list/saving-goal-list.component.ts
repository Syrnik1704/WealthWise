import { CommonModule, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { IntervalService } from '../../../shared';
import { SavingGoal } from '../../models';
import { SavingGoalApiService } from '../../services/saving-goal-api.service';

@Component({
  selector: 'ww-saving-goal-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [MatListModule, TranslateModule, CommonModule, DatePipe, MatCardModule],
  templateUrl: './saving-goal-list.component.html',
  styleUrl: './saving-goal-list.component.scss',
})
export class SavingGoalListComponent {
  private readonly savingGoalApiService = inject(SavingGoalApiService);
  protected readonly intervalService = inject(IntervalService);
  protected savingGoalsData$: Observable<SavingGoal[]> = this.savingGoalApiService.getGoalList();
  private readonly cdRef = inject(ChangeDetectorRef);

  public refreshTable(): void {
    this.savingGoalsData$ = this.savingGoalApiService.getGoalList();
    this.cdRef.detectChanges();
  }
}
