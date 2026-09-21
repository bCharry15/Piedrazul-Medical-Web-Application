import {
  HttpClient,
} from '@angular/common/http';

import {
  Injectable,
  inject,
} from '@angular/core';

import {
  Observable,
  map,
} from 'rxjs';

import {
  AvailabilityConfiguration,
  DayOfWeek,
  SaveAvailabilityConfigurationRequest,
} from '../../shared/models/availability-configuration';

interface DoctorReferenceApiResponse {
  id: number;
}

interface AvailabilityConfigurationApiResponse {
  id: number;

  doctor:
    | DoctorReferenceApiResponse
    | null;

  diaSemana: DayOfWeek;

  timeInicio: string;

  timeFin: string;

  intervaloMinutos: number;

  ventanaSemanas: number;

  activo: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class AvailabilityConfigurationService {

  private readonly http =
    inject(HttpClient);

  private readonly apiUrl =
    'http://localhost:8081/api/availability-configurations';

  getByDoctor(
    doctorId: number
  ): Observable<AvailabilityConfiguration[]> {

    return this.http
      .get<
        AvailabilityConfigurationApiResponse[]
      >(
        `${this.apiUrl}/doctor/${doctorId}`
      )
      .pipe(
        map((response) =>
          response.map(
            (configuration) =>
              this.mapConfiguration(
                configuration
              )
          )
        )
      );
  }

  create(
    request:
      SaveAvailabilityConfigurationRequest
  ): Observable<AvailabilityConfiguration> {

    const payload =
      this.createPayload(
        request
      );

    return this.http
      .post<
        AvailabilityConfigurationApiResponse
      >(
        this.apiUrl,
        payload
      )
      .pipe(
        map((response) =>
          this.mapConfiguration(
            response
          )
        )
      );
  }

  update(
    availabilityId: number,
    request:
      SaveAvailabilityConfigurationRequest
  ): Observable<AvailabilityConfiguration> {

    const payload =
      this.createPayload(
        request
      );

    return this.http
      .put<
        AvailabilityConfigurationApiResponse
      >(
        `${this.apiUrl}/${availabilityId}`,
        payload
      )
      .pipe(
        map((response) =>
          this.mapConfiguration(
            response
          )
        )
      );
  }

  private createPayload(
    request:
      SaveAvailabilityConfigurationRequest
  ) {

    return {
      doctorId:
        request.doctorId,

      diaSemana:
        request.dayOfWeek,

      timeInicio:
        request.startTime,

      timeFin:
        request.endTime,

      intervaloMinutos:
        request.intervalMinutes,

      ventanaSemanas:
        request.weekWindow,
    };
  }

  private mapConfiguration(
    response:
      AvailabilityConfigurationApiResponse
  ): AvailabilityConfiguration {

    return {
      id:
        response.id,

      doctorId:
        response.doctor?.id ?? 0,

      dayOfWeek:
        response.diaSemana,

      startTime:
        response.timeInicio,

      endTime:
        response.timeFin,

      intervalMinutes:
        response.intervaloMinutos,

      weekWindow:
        response.ventanaSemanas,

      active:
        response.activo,
    };
  }
}