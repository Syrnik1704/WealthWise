import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { LandingService } from '../../services';

@Component({
  selector: 'ww-guest-dashboard',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [TranslateModule, CommonModule],
  template: `
    <section id="reason" class="reasons">
      <div class="reasons__goal h3">
        {{ 'DASHBOARD.GOAL' | translate }}
      </div>
    </section>
    <section id="features" class="features">
      <h3>{{ 'DASHBOARD.FEATURES.TITLE' | translate }}</h3>
      @for (feature of landingService.getFeatures() | async; track $index) {
        <div class="features__item h5" [ngStyle]="{ 'text-align': $index % 2 ? 'end' : 'start' }">
          {{ feature }}
        </div>
        <hr class="features__split-line" />
      }
    </section>
    <section id="userInterface" class="ui">
      <h3 class="ui__header">{{ 'COMMON.UI' | translate }}</h3>
      @let images = landingService.getImages() | async;
      @if (images?.length) {
        <div id="carousel" class="carousel slide" data-bs-ride="carousel">
          <div class="carousel-indicators">
            @for (image of images; track $index) {
              <button
                type="button"
                data-bs-target="#carousel"
                [attr.data-bs-slide-to]="$index"
                [ngClass]="{ active: $index === 0 }"
                [attr.aria-label]="'Slide ' + $index"></button>
            }
          </div>
          <div class="carousel-inner">
            @for (image of images; track $index) {
              <div class="carousel-item" [ngClass]="{ active: $index === 0 }">
                <img class="d-block w-100" [src]="image.url" [alt]="image.alt" />
              </div>
            }
          </div>
          <button
            class="carousel-control-prev"
            type="button"
            data-bs-target="#carousel"
            data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Previous</span>
          </button>
          <button
            class="carousel-control-next"
            type="button"
            data-bs-target="#carousel"
            data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Next</span>
          </button>
        </div>
      }
    </section>
  `,
  styleUrl: './guest-dashboard.component.scss',
})
export class GuestDashboardComponent {
  protected readonly landingService = inject(LandingService);
}
