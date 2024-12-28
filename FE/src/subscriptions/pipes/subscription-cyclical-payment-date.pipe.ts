import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'subscriptionCyclicalPaymentDate',
  standalone: true
})
export class SubscriptionCyclicalPaymentDatePipe implements PipeTransform {
  transform(day: number): string {
    const date = new Date();
    date.setMonth(date.getMonth() + 1);
    date.setDate(day);
    const options: Intl.DateTimeFormatOptions = { month: 'long', day: 'numeric' };
    return date.toLocaleDateString(undefined, options);
  }
}