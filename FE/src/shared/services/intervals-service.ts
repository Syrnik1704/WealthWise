import { Injectable } from '@angular/core';
import { LabeledValue } from '../models';

@Injectable({ providedIn: 'root' })
export class IntervalService {
  private readonly intervalMap: Record<string, string> = {
    '0 0 10 * * *': 'SAVING_GOAL.INTERVAL.DAILY',
    '0 0 10 */2 * *': 'SAVING_GOAL.INTERVAL.EVERY_TWO_DAYS',
    '0 0 10 * * MON': 'SAVING_GOAL.INTERVAL.WEEKLY',
    '0 0 10 */14 * *': 'SAVING_GOAL.INTERVAL.BIWEEKLY',
    '0 0 10 1 * *': 'SAVING_GOAL.INTERVAL.MONTHLY',
  };
  public intervalOptions() {
    return Object.keys(this.intervalMap).map(this.getInterval.bind(this));
  }
  public getLabel(cronPattern: string): string {
    return this.intervalMap[cronPattern];
  }
  public getInterval(cronPattern?: string): LabeledValue | undefined {
    if (!cronPattern || !this.intervalMap[cronPattern]) {
      return;
    }
    return { label: this.intervalMap[cronPattern], value: cronPattern };
  }
}
