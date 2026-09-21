import {
  Injectable,
  signal,
} from '@angular/core';

import {
  LanguageCode,
  TranslationKey,
  translations,
} from './translations';

@Injectable({
  providedIn: 'root',
})
export class LanguageService {
  private readonly storageKey =
    'piedraazul-language';
    

  private readonly currentLanguage =
    signal<LanguageCode>(
      this.loadInitialLanguage()
    );

  readonly language =
    this.currentLanguage.asReadonly();

  constructor() {
    this.updateDocumentLanguage(
      this.currentLanguage()
    );
  }

  setLanguage(
    language: LanguageCode
  ): void {
    this.currentLanguage.set(language);

    if (
      typeof localStorage !== 'undefined'
    ) {
      localStorage.setItem(
        this.storageKey,
        language
      );
    }

    this.updateDocumentLanguage(language);
  }

  toggleLanguage(): void {
    this.setLanguage(
      this.currentLanguage() === 'es'
        ? 'en'
        : 'es'
    );
  }

  t(
    key: TranslationKey
  ): string {
    return translations[
      this.currentLanguage()
    ][key];
  }

  status(
    status: string
  ): string {
    switch (
      status?.toUpperCase()
    ) {
      case 'PROGRAMADA':
        return this.t('scheduled');

      case 'CONFIRMADA':
        return this.t('confirmed');

      case 'ATENDIDA':
        return this.t('attended');

      case 'COMPLETADA':
        return this.t('completed');

      case 'CANCELADA':
        return this.t('cancelled');

      case 'NO_VINO':
        return this.t('noShow');

      default:
        return status;
    }
  }

  private loadInitialLanguage():
    LanguageCode {
    if (
      typeof localStorage === 'undefined'
    ) {
      return 'es';
    }

    const savedLanguage =
      localStorage.getItem(
        this.storageKey
      );

    if (
      savedLanguage === 'es' ||
      savedLanguage === 'en'
    ) {
      return savedLanguage;
    }

    return 'es';
  }

  private updateDocumentLanguage(
    language: LanguageCode
  ): void {
    if (
      typeof document !== 'undefined'
    ) {
      document.documentElement.lang =
        language;
    }
  }
}