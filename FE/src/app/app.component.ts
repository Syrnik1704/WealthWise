import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { DashboardComponent } from '../dashboard';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
@Component({
  selector: 'ww-root',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [RouterOutlet, DashboardComponent, TranslateModule, MatIconModule, ReactiveFormsModule,MatSnackBarModule],
  template: `<router-outlet/>`,
  styles: [],
})
export class AppComponent { }
