import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { TranslateModule } from '@ngx-translate/core';
import { CalendarEvent, CalendarModule, CalendarMonthModule, CalendarView } from 'angular-calendar';
import { isSameDay, isSameMonth } from 'date-fns';
import { Observable } from 'rxjs';
import { CalenderService } from './calender.service';

@Component({
  selector: 'ww-calender',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CalendarMonthModule, TranslateModule, CalendarModule, MatButtonModule, CommonModule],
  templateUrl: './calender.component.html',
  styleUrl: './calender.component.scss',
})
export class CalenderComponent {
  protected viewDate = new Date();
  protected view: CalendarView = CalendarView.Month;
  protected activeDayIsOpen = false;
  private readonly calendarService = inject(CalenderService);
  protected events: Observable<CalendarEvent[]> = this.calendarService.getCalendarEvents();

  protected closeOpenMonthViewDay(): void {
    this.activeDayIsOpen = false;
  }

  protected dayClicked({ date, events }: { date: Date; events: CalendarEvent[] }): void {
    if (isSameMonth(date, this.viewDate)) {
      if (
        (isSameDay(this.viewDate, date) && this.activeDayIsOpen === true) ||
        events.length === 0
      ) {
        this.activeDayIsOpen = false;
      } else {
        this.activeDayIsOpen = true;
      }
      this.viewDate = date;
    }
  }
}
