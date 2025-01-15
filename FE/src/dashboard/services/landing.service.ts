import { inject, Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable, of } from 'rxjs';
import { ImageItem } from '../model';

@Injectable({ providedIn: 'root' })
export class LandingService {
  private readonly functionalitiesTranslations = [
    'DASHBOARD.FEATURES.SAVING_GOALS',
    'DASHBOARD.FEATURES.OUTCOME_GROUPING',
    'DASHBOARD.FEATURES.SUBSCRIPTION_MANAGMENT',
    'DASHBOARD.FEATURES.PROGRESS_MONITORING',
    'DASHBOARD.FEATURES.INCOMING_PAYMENT',
  ];
  private readonly images: ImageItem[] = [
    {
      url: 'assets/imgs/ui/1.jpg',
      alt: 'DASHBOARD.GALLERY.FIRST',
    },
    {
      url: 'assets/imgs/ui/2.jpg',
      alt: 'DASHBOARD.GALLERY.SECOND',
    },
    {
      url: 'assets/imgs/ui/3.jpg',
      alt: 'DASHBOARD.GALLERY.THIRD',
    },
  ];
  private readonly translate = inject(TranslateService);
  public getFeatures(): Observable<string[]> {
    return of<string[]>(
      this.functionalitiesTranslations.map(label => this.translate.instant(label))
    );
  }

  public getImages(): Observable<ImageItem[]> {
    return of<ImageItem[]>(
      this.images.map(image => ({ ...image, alt: this.translate.instant(image.alt) }))
    );
  }
}
