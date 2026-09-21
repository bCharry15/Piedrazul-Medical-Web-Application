import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

import { Availability } from '../../shared/models/availability';

interface AvailabilityApiResponse {
  doctorId: number;
  doctor: string;
  date: string;
  intervaloMinutos: number;
  timeInicio: string;
  timeFin: string;
  franjasDisponibles: string[];
}

@Injectable({
  providedIn: 'root',
})
export class AvailabilityService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8081/api/availability';

  getByDoctorAndDate(
    doctorId: number,
    date: string
  ): Observable<Availability> {
    const params = new HttpParams()
      .set('doctorId', doctorId)
      .set('date', date);

    return this.http
      .get<AvailabilityApiResponse>(this.apiUrl, { params })
      .pipe(
        map((response) => ({
          doctorId: response.doctorId,
          doctor: response.doctor,
          date: response.date,
          intervalMinutes: response.intervaloMinutos,
          startTime: response.timeInicio,
          endTime: response.timeFin,
          availableSlots: response.franjasDisponibles ?? [],
        }))
      );
  }
}