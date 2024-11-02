// import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
// import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
// import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
// import { provideRouter } from '@angular/router';
// import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
// import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
// import { TranslateHttpLoader } from '@ngx-translate/http-loader';
// import { routes } from './app.routes';
// import { TokenInterceptor } from './services/auth/token.interceptor';

// export const httpLoaderFactory: (http: HttpClient) => TranslateHttpLoader = (http: HttpClient) =>
//   new TranslateHttpLoader(http, './assets/i18n/', '.json');

// export const appConfig: ApplicationConfig = {
//   providers: [
//     provideZoneChangeDetection({ eventCoalescing: true }),
//     provideRouter(routes),
//     provideAnimationsAsync(),
//     provideHttpClient(withInterceptorsFromDi()),
//     {
//       provide: HTTP_INTERCEPTORS,
//       useClass: TokenInterceptor,
//       multi: true
//     },
//     importProvidersFrom([
//       TranslateModule.forRoot({
//         loader: {
//           provide: TranslateLoader,
//           useFactory: httpLoaderFactory,
//           deps: [HttpClient],
//         },
//         defaultLanguage: 'en-us',
//       }),
//     ]),
//   ],
// };
import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { HTTP_INTERCEPTORS, HttpClient, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { routes } from './app.routes';
import { TokenInterceptor } from './services/auth/token.interceptor';

export const httpLoaderFactory: (http: HttpClient) => TranslateHttpLoader = (http: HttpClient) =>
  new TranslateHttpLoader(http, './assets/i18n/', '.json');

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimationsAsync(),
    provideHttpClient(withInterceptorsFromDi()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    importProvidersFrom([
      TranslateModule.forRoot({
        loader: {
          provide: TranslateLoader,
          useFactory: httpLoaderFactory,
          deps: [HttpClient],
        },
        defaultLanguage: 'en-us',
      }),
    ]),
  ],
};