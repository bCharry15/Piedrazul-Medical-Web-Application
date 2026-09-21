import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

import { PatientProfile } from '../../shared/models/patient';

interface PatientProfileApiResponse {
  id: number;
  username: string;
  documentNumber: string;
  documentType: string;
  nombres: string;
  apellidos: string;
  phone: string;
  gender: string;
  dateNacimiento: string | null;
  email: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class PatientService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8081/api/patients';

  getProfileByUsername(username: string): Observable<PatientProfile> {
    return this.http
      .get<PatientProfileApiResponse>(
        `${this.apiUrl}/profile/${encodeURIComponent(username)}`
      )
      .pipe(
        map((response) => ({
          id: response.id,
          username: response.username,
          documentNumber: response.documentNumber,
          documentType: response.documentType,
          firstNames: response.nombres,
          lastNames: response.apellidos,
          phone: response.phone,
          gender: response.gender,
          birthDate: response.dateNacimiento,
          email: response.email,
        }))
      );
  }
}