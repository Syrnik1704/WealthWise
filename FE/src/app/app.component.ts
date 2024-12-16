import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { AuthService } from '../auth/services/auth.service';

@Component({
  selector: 'ww-root',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [RouterOutlet, TranslateModule],
  template: `<router-outlet />`,
  styles: [
    `
      :host {
        width: 100%;
        height: 100%;
        display: block;
      }
    `,
  ],
})
export class AppComponent implements OnInit {
  private readonly authService = inject(AuthService);
  public ngOnInit(): void {
    this.authService.initAuthState();
  }
}
