import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { DashboardComponent } from '../dashboard';

@Component({
  selector: 'ww-root',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [RouterOutlet, DashboardComponent, TranslateModule],
  template: `<ww-dashboard />`,
  styles: [],
})
export class AppComponent {}
