import {
  CommonModule,
} from '@angular/common';

import {
  ChangeDetectorRef,
  Component,
  inject,
} from '@angular/core';

import {
  FormsModule,
} from '@angular/forms';

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
  RegisterPatientRequest,
  RegistrationService,
} from '../../../core/services/registration';


@Component({
  selector: 'app-register',

  imports: [
    CommonModule,
    FormsModule,
  ],

  templateUrl:
    './register.html',

  styleUrl:
    './register.scss',
})
export class Register {

  readonly i18n =
    inject(LanguageService);

  private readonly auth =
    inject(Auth);

  private readonly router =
    inject(Router);

  private readonly registrationService =
    inject(RegistrationService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  loading = false;

  success = false;

  error = '';

  successMessage = '';

  showPassword = false;

  showConfirmPassword = false;


  form = {

    nombres: '',

    apellidos: '',

    documentType: 'CC',

    documentNumber: '',

    dateNacimiento: '',

    gender: '',

    phone: '',

    email: '',

    username: '',

    password: '',

    confirmPassword: '',
  };


  text(
    spanish: string,
    english: string
  ): string {

    return (
      this.i18n.language() === 'es'
        ? spanish
        : english
    );
  }


  get passwordHasMinLength():
    boolean {

    return (
      this.form.password.length >= 8
    );
  }


  get passwordHasUppercase():
    boolean {

    return /[A-Z]/.test(
      this.form.password
    );
  }


  get passwordHasLowercase():
    boolean {

    return /[a-z]/.test(
      this.form.password
    );
  }


  get passwordHasNumber():
    boolean {

    return /[0-9]/.test(
      this.form.password
    );
  }


  get passwordsMatch():
    boolean {

    return (
      this.form.password !== '' &&
      this.form.password ===
        this.form.confirmPassword
    );
  }


  get passwordValid():
    boolean {

    return (
      this.passwordHasMinLength &&
      this.passwordHasUppercase &&
      this.passwordHasLowercase &&
      this.passwordHasNumber
    );
  }


  togglePassword():
    void {

    this.showPassword =
      !this.showPassword;
  }


  toggleConfirmPassword():
    void {

    this.showConfirmPassword =
      !this.showConfirmPassword;
  }


  register():
    void {

    this.error = '';

    this.successMessage = '';

    const nombres =
      this.form.nombres.trim();

    const apellidos =
      this.form.apellidos.trim();

    const documentType =
      this.form.documentType.trim();

    const documentNumber =
      this.form.documentNumber.trim();

    const dateNacimiento =
      this.form.dateNacimiento.trim();

    const gender =
      this.form.gender.trim();

    const phone =
      this.form.phone.trim();

    const email =
      this.form.email.trim();

    const username =
      this.form.username.trim();

    const password =
      this.form.password;

    const confirmPassword =
      this.form.confirmPassword;


    if (
      !nombres ||
      !apellidos ||
      !documentType ||
      !documentNumber ||
      !gender ||
      !phone ||
      !email ||
      !username ||
      !password ||
      !confirmPassword
    ) {

      this.error =
        this.text(
          'Completa todos los campos obligatorios.',
          'Complete all required fields.'
        );

      return;
    }


    if (
      !this.isValidEmail(
        email
      )
    ) {

      this.error =
        this.text(
          'Ingresa un correo electrónico válido.',
          'Enter a valid email address.'
        );

      return;
    }


    if (
      !this.passwordValid
    ) {

      this.error =
        this.text(
          'La contraseña no cumple todos los requisitos.',
          'The password does not meet all requirements.'
        );

      return;
    }


    if (
      !this.passwordsMatch
    ) {

      this.error =
        this.text(
          'Las contraseñas no coinciden.',
          'Passwords do not match.'
        );

      return;
    }


    const request:
      RegisterPatientRequest = {

      username,

      password,

      confirmPassword,

      nombres,

      apellidos,

      documentType,

      documentNumber,

      email,

      phone,

      gender,

      dateNacimiento:
        dateNacimiento
          ? dateNacimiento
          : null,
    };


    this.loading = true;

    this.cdr.detectChanges();


    this.registrationService
      .register(request)
      .subscribe({

        next:
          (response) => {

            this.loading =
              false;

            this.success =
              true;

            this.error =
              '';

            this.successMessage =
              response.mensaje ||
              this.text(
                'Tu cuenta fue creada correctamente.',
                'Your account was created successfully.'
              );

            this.cdr.detectChanges();

            window.scrollTo({
              top: 0,
              behavior: 'smooth',
            });
          },


        error:
          (error) => {

            console.error(
              'Error registrando paciente:',
              error
            );

            this.loading =
              false;

            this.success =
              false;

            this.error =
              this.extractErrorMessage(
                error
              );

            /*
             * Importante para aplicaciones
             * con detección de cambios
             * sin Zone.js.
             */
            this.cdr.detectChanges();

            window.scrollTo({
              top: 0,
              behavior: 'smooth',
            });
          },
      });
  }


  login():
    void {

    void this.auth.login();
  }


  goHome():
    void {

    void this.router
      .navigateByUrl('/');
  }


  reset():
    void {

    this.form = {

      nombres: '',

      apellidos: '',

      documentType: 'CC',

      documentNumber: '',

      dateNacimiento: '',

      gender: '',

      phone: '',

      email: '',

      username: '',

      password: '',

      confirmPassword: '',
    };

    this.error = '';

    this.success = false;

    this.successMessage = '';

    this.loading = false;

    this.cdr.detectChanges();
  }


  private isValidEmail(
    email: string
  ): boolean {

    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      .test(
        email
      );
  }


  private extractErrorMessage(
    error: any
  ): string {

    if (
      error?.error?.detail
    ) {

      return error.error.detail;
    }


    if (
      error?.error?.message
    ) {

      return error.error.message;
    }


    if (
      error?.error?.error_description
    ) {

      return error.error.error_description;
    }


    if (
      typeof error?.error ===
      'string' &&
      error.error.trim()
    ) {

      return error.error;
    }


    if (
      error?.status === 400
    ) {

      return this.text(
        'Revisa los datos ingresados. Alguno no es válido.',
        'Check the entered information. Some data is invalid.'
      );
    }


    if (
      error?.status === 401
    ) {

      return this.text(
        'El servidor rechazó la solicitud de registro. Verifica la configuración de seguridad.',
        'The server rejected the registration request. Check the security configuration.'
      );
    }


    if (
      error?.status === 409
    ) {

      return this.text(
        'El usuario, correo o número de documento ya está registrado.',
        'The username, email or document number is already registered.'
      );
    }


    if (
      error?.status === 0
    ) {

      return this.text(
        'No fue posible conectar con PiedraAzul.',
        'Could not connect to PiedraAzul.'
      );
    }


    return this.text(
      'No fue posible crear la cuenta.',
      'The account could not be created.'
    );
  }
}