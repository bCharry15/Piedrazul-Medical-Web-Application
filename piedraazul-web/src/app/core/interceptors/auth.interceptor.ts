import {
  inject,
} from '@angular/core';

import {
  HttpInterceptorFn,
} from '@angular/common/http';

import {
  from,
  switchMap,
} from 'rxjs';

import {
  Auth,
} from '../auth/auth';


const API_URL =
  'http://localhost:8081';


/*
 * Endpoints públicos del backend.
 *
 * Estas peticiones NO deben incluir
 * Authorization: Bearer ...
 */
const PUBLIC_ENDPOINTS: string[] = [

  `${API_URL}/api/public/`,

  `${API_URL}/api/agenda/health`,

  `${API_URL}/api/auth/login`,

  `${API_URL}/api/auth/forgot-password`,

  `${API_URL}/api/auth/reset-password`,
];


export const authInterceptor:
  HttpInterceptorFn =
  (request, next) => {

    const auth =
      inject(Auth);


    /*
     * Si la petición no pertenece
     * al backend PiedraAzul,
     * no hacemos nada.
     */
    if (
      !request.url.startsWith(
        API_URL
      )
    ) {

      return next(request);
    }


    /*
     * Comprobamos si la petición
     * corresponde a un endpoint público.
     */
    const isPublicEndpoint =
      PUBLIC_ENDPOINTS.some(
        (endpoint) =>
          request.url.startsWith(
            endpoint
          )
      );


    /*
     * Los endpoints públicos NO reciben
     * token de autenticación.
     *
     * Esto es especialmente importante
     * para:
     *
     * POST /api/public/patients/register
     */
    if (
      isPublicEndpoint
    ) {

      return next(request);
    }


    /*
     * Las demás peticiones del backend
     * sí requieren autenticación.
     *
     * Verificamos / renovamos el token
     * antes de continuar.
     */
    return from(
      auth.getValidToken()
    ).pipe(

      switchMap(
        (token) => {

          /*
           * Si por alguna razón no existe
           * token, enviamos la petición
           * sin Authorization.
           *
           * Spring Security decidirá si
           * el endpoint exige autenticación.
           */
          if (
            !token
          ) {

            return next(
              request
            );
          }


          /*
           * Petición autenticada.
           */
          const authenticatedRequest =
            request.clone({

              setHeaders: {

                Authorization:
                  `Bearer ${token}`,

              },
            });


          return next(
            authenticatedRequest
          );
        }
      )
    );
  };