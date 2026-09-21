import {
  CommonModule,
} from '@angular/common';

import {
  Component,
  inject,
} from '@angular/core';

import {
  Router,
} from '@angular/router';

import {
  Auth,
} from '../../../core/auth/auth';

import {
  LanguageService,
} from '../../../core/i18n/language';

import {
  LanguageCode,
} from '../../../core/i18n/translations';


@Component({
  imports: [
    CommonModule,
  ],

  selector:
    'app-navbar',

  styleUrl:
    './navbar.scss',

  templateUrl:
    './navbar.html',
})
export class Navbar {

  readonly i18n =
    inject(LanguageService);

  private readonly auth =
    inject(Auth);

  private readonly router =
    inject(Router);


  setLanguage(
    language:
      LanguageCode
  ): void {

    this.i18n
      .setLanguage(
        language
      );
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


  get isAuthenticated():
    boolean {

    return this.auth
      .isAuthenticated();
  }


  get username():
    string {

    return this.auth
      .getUsername();
  }


  login():
    void {

    void this.auth
      .login();
  }


  register():
    void {

    void this.router
      .navigateByUrl(
        '/register'
      );
  }


  logout():
    void {

    void this.auth
      .logout();
  }


  goHome():
    void {

    if (
      this.isAuthenticated
    ) {

      this.goToPortal();

      return;
    }

    void this.router
      .navigateByUrl('/');
  }


  goToSection(
    sectionId: string
  ): void {

    if (
      window.location.pathname !==
      '/'
    ) {

      void this.router
        .navigateByUrl('/')
        .then(
          () => {

            setTimeout(
              () => {

                this.scrollToSection(
                  sectionId
                );
              },
              50
            );
          }
        );

      return;
    }

    this.scrollToSection(
      sectionId
    );
  }


  goToPortal():
    void {

    const roles =
      this.auth
        .getRoles()
        .map(
          (role) =>
            role.toUpperCase()
        );


    if (
      roles.includes(
        'ADMIN'
      )
    ) {

      void this.router
        .navigateByUrl(
          '/admin'
        );

      return;
    }


    if (
      roles.includes(
        'SCHEDULER'
      )
    ) {

      void this.router
        .navigateByUrl(
          '/scheduler'
        );

      return;
    }


    if (
      roles.includes(
        'DOCTOR'
      )
    ) {

      void this.router
        .navigateByUrl(
          '/doctor'
        );

      return;
    }


    if (
      roles.includes(
        'PATIENT'
      )
    ) {

      void this.router
        .navigateByUrl(
          '/patient'
        );

      return;
    }


    void this.router
      .navigateByUrl(
        '/unauthorized'
      );
  }


  private scrollToSection(
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
}