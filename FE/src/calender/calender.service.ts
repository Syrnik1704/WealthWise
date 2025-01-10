import { inject, Injectable } from '@angular/core';
import { CalendarEvent } from 'angular-calendar';
import { SavingGoalApiService } from '../saving-goals/services';

import { EventColor } from 'calendar-utils';
import * as cronParser from 'cron-parser';
import { map, Observable } from 'rxjs';
import { SavingGoal } from '../saving-goals/models';

const colors: Record<string, EventColor> = {
  yellow: {
    primary: '#e3bc08',
    secondary: '#FDF1BA',
  },
  green: {
    primary: '#34e308',
    secondary: '#d6f7cf',
  },
};

@Injectable({ providedIn: 'root' })
export class CalenderService {
  private readonly savingGoalApiService = inject(SavingGoalApiService);

  public getCalendarEvents(): Observable<CalendarEvent[]> {
    return this.savingGoalApiService.getGoalList().pipe(
      map(savingGoals => {
        return savingGoals.reduce((acc, curr) => {
          acc.push(...this.getEventsFromSavingGoal(curr));
          return acc;
        }, new Array<CalendarEvent>());
      })
    );
  }

  private getEventsFromSavingGoal(savingGoal: SavingGoal): CalendarEvent[] {
    return this.getRunDates(
      savingGoal.creationDate,
      savingGoal.targetDate,
      savingGoal.cyclicalPaymentCron
    ).map(date => {
      return {
        start: date,
        color:
          savingGoal.currentAmount === savingGoal.targetAmount ? colors['green'] : colors['yellow'],
        title: `${savingGoal.targetTitle}: income ${savingGoal.cyclicalPaymentAmount ?? savingGoal.currentAmount}`,
      };
    });
  }

  private getRunDates(creationDate: string, targetDate: string, interval?: string): Date[] {
    const dates: Date[] = [];
    if (!interval) {
      return [new Date(creationDate)];
    }
    const endDate =
      new Date().getTime() < new Date(targetDate).getTime() ? new Date() : new Date(targetDate);
    const intervals = cronParser.parseExpression(interval, {
      currentDate: new Date(creationDate),
      endDate,
    });
    while (intervals.hasNext()) {
      dates.push(intervals.next().toDate());
    }
    return dates;
  }
}
