import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { routes } from './app.routes';
import { LOCALE_ID } from '@angular/core';
import { AuthInterceptor } from './core/guards/auth.interceptor';


export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
     provideRouter(routes),provideHttpClient(), { provide: LOCALE_ID, useValue: 'vi' } ,
      provideHttpClient(withInterceptorsFromDi()),
          { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }

    
    ]
};
