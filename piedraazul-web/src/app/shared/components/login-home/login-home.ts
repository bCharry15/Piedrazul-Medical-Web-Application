import {
  CommonModule,
} from '@angular/common';

import {
  Component,
  OnDestroy,
  OnInit,
  inject,
} from '@angular/core';

import {
  Auth,
} from '../../../core/auth/auth';

import {
  LanguageService,
} from '../../../core/i18n/language';


interface ServiceItem {

  titleEs:
    string;

  titleEn:
    string;

  descriptionEs:
    string;

  descriptionEn:
    string;
}


@Component({
  imports: [
    CommonModule,
  ],

  selector:
    'app-login-home',

  templateUrl:
    './login-home.html',

  styleUrl:
    './login-home.scss',
})
export class LoginHome
  implements OnInit, OnDestroy {

  readonly i18n =
    inject(LanguageService);

  private readonly auth =
    inject(Auth);

  currentServiceIndex =
    0;

  private carouselTimer:
    ReturnType<
      typeof setInterval
    > |
    null = null;


  readonly services:
    ServiceItem[] = [

    {
      titleEs:
        'Terapia neural',

      titleEn:
        'Neural therapy',

      descriptionEs:
        'Conoce este servicio de medicina alternativa disponible en PiedraAzul.',

      descriptionEn:
        'Discover this alternative medicine service available at PiedraAzul.',
    },

    {
      titleEs:
        'Quiropráctica',

      titleEn:
        'Chiropractic care',

      descriptionEs:
        'Atención orientada al bienestar y acompañamiento de nuestros pacientes.',

      descriptionEn:
        'Care focused on the well-being and support of our patients.',
    },

    {
      titleEs:
        'Fisioterapia',

      titleEn:
        'Physical therapy',

      descriptionEs:
        'Atención terapéutica para acompañar los procesos de recuperación y bienestar.',

      descriptionEn:
        'Therapeutic care supporting recovery and well-being.',
    },
  ];


  ngOnInit():
    void {

    this.carouselTimer =
      setInterval(
        () => {

          this.nextService();
        },
        5000
      );
  }


  ngOnDestroy():
    void {

    if (
      this.carouselTimer
    ) {

      clearInterval(
        this.carouselTimer
      );
    }
  }


  login():
    void {

    void this.auth.login();
  }


  nextService():
    void {

    this.currentServiceIndex =
      (
        this.currentServiceIndex +
        1
      ) %
      this.services.length;
  }


  previousService():
    void {

    this.currentServiceIndex =
      (
        this.currentServiceIndex -
        1 +
        this.services.length
      ) %
      this.services.length;
  }


  selectService(
    index: number
  ): void {

    this.currentServiceIndex =
      index;
  }


  get currentService():
    ServiceItem {

    return this.services[
      this.currentServiceIndex
    ];
  }


  serviceTitle(
    service: ServiceItem
  ): string {

    return (
      this.i18n.language() ===
      'es'
        ? service.titleEs
        : service.titleEn
    );
  }


  serviceDescription(
    service: ServiceItem
  ): string {

    return (
      this.i18n.language() ===
      'es'
        ? service.descriptionEs
        : service.descriptionEn
    );
  }


  scrollTo(
    sectionId: string
  ): void {

    document
      .getElementById(
        sectionId
      )
      ?.scrollIntoView({
        behavior:
          'smooth',

        block:
          'start',
      });
  }


  text(
    spanish: string,
    english: string
  ): string {

    return (
      this.i18n.language() ===
      'es'
        ? spanish
        : english
    );
  }
}