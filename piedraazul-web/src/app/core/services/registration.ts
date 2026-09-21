import {
  Injectable,
  inject,
} from '@angular/core';

import {
  HttpBackend,
  HttpClient,
} from '@angular/common/http';

import {
  Observable,
} from 'rxjs';


export interface RegisterPatientRequest {

  username: string;

  password: string;

  confirmPassword: string;

  nombres: string;

  apellidos: string;

  documentType: string;

  documentNumber: string;

  email: string;

  phone: string;

  gender: string;

  dateNacimiento:
    string | null;
}


export interface RegisterPatientResponse {

  id: number;

  username: string;

  nombres: string;

  apellidos: string;

  documentType: string;

  documentNumber: string;

  email: string;

  phone: string;

  gender: string;

  dateNacimiento:
    string | null;

  mensaje: string;
}


@Injectable({
  providedIn: 'root',
})
export class RegistrationService {

  /*
   * IMPORTANTE:
   *
   * Creamos un HttpClient directamente
   * desde HttpBackend.
   *
   * De esta forma esta petición NO pasa
   * por authInterceptor ni por ningún
   * otro interceptor de Angular.
   *
   * Esto es correcto porque el registro
   * de pacientes es un endpoint público.
   */
  private readonly http =
    new HttpClient(
      inject(HttpBackend)
    );


  private readonly apiUrl =
    'http://localhost:8081/api/public/patients/register';


  register(
    request:
      RegisterPatientRequest
  ): Observable<
    RegisterPatientResponse
  > {

    return this.http.post<
      RegisterPatientResponse
    >(
      this.apiUrl,
      request
    );
  }
}