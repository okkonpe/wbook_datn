// Thêm ở đầu file
// main.ts hoặc polyfills.ts
import { Buffer } from 'buffer';
(window as any).global = window;
(window as any).process = { env: {} };
(window as any).Buffer = Buffer;
import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { registerLocaleData } from '@angular/common';
import localeVi from '@angular/common/locales/vi';

registerLocaleData(localeVi, 'vi');
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
  
