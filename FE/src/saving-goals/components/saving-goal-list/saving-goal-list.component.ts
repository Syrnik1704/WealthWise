import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnInit,
} from '@angular/core';
import { SavingGoalApiService } from '../../services/saving-goal-api.service';
import { SavingGoal } from '../../models';
import { MatTableModule } from '@angular/material/table';
import { TranslateModule } from '@ngx-translate/core';
import { CommonModule, DatePipe } from '@angular/common';

@Component({
  selector: 'ww-saving-goal-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatTableModule,
    TranslateModule,
    CommonModule
  ],
  templateUrl: './saving-goal-list.component.html',
  styleUrl: './saving-goal-list.component.scss',
  providers: [DatePipe],
})
export class SavingGoalListComponent implements OnInit {
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly savingGoalApiService = inject(SavingGoalApiService);
  protected savingGoalsData: SavingGoal[] = [];

  protected savingGoalsColumns: string[] = [
    'targetTitle',
    'targetAmount',
    'cyclicalPaymentAmount',
    'currentAmount',
    'targetDate',
  ];

  protected savingGoalsHeaders: string[] = [
    'TITLE',
    'AMOUNT',
    'CYCLICAL_PAYMENT_AMOUNT',
    'CURRENT_AMOUNT',
    'DATE',
  ];

  protected displayedColumns: string[] = this.savingGoalsColumns;

  ngOnInit(): void {
    this.savingGoalApiService.getGoalList().subscribe({
      next: (data: SavingGoal[]) => {
        this.savingGoalsData = data;
        this.cdr.markForCheck();
        console.log(data);
      },
      error: err => {
        console.error(err);
      },
    });
  }
}
