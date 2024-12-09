import { CommonModule, DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
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
  imports: [MatListModule, TranslateModule, CommonModule, DatePipe],
  templateUrl: './saving-goal-list.component.html',
  styleUrl: './saving-goal-list.component.scss',
})
export class SavingGoalListComponent {
  private readonly savingGoalApiService = inject(SavingGoalApiService);
  protected readonly intervalService = inject(IntervalService);
  protected savingGoalsData$: Observable<SavingGoal[]> = this.getTableData();

  public refreshTable(): void {
    this.savingGoalsData$ = this.getTableData();
  }

  private getTableData(): Observable<SavingGoal[]> {
    return this.savingGoalApiService.getGoalList();
  }
}
