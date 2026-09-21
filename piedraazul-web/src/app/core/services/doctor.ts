import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';

import { Doctor } from '../../shared/models/doctor';

export interface CreateDoctorRequest {
  fullName: string;
  specialty: string;
  intervalMinutes: number;
  username: string;
  password: string;
}

export interface UpdateDoctorRequest {
  fullName: string;
  specialty: string;
  intervalMinutes: number;
}

interface DoctorApiResponse {
  id: number;
  userId: number;
  username: string;
  nombreCompleto: string;
  especialidad: string;
  intervalMinutes: number;
  active: boolean;
  userStatus: string;
  userRole: string;
}

@Injectable({
  providedIn: 'root',
})
export class DoctorService {

  private readonly http =
    inject(HttpClient);

  private readonly apiUrl =
    'http://localhost:8081/api/doctors';

  getAll(): Observable<Doctor[]> {

    return this.http
      .get<DoctorApiResponse[]>(
        this.apiUrl
      )
      .pipe(
        map((response) =>
          response.map(
            (doctor) =>
              this.mapDoctor(doctor)
          )
        )
      );
  }

  getById(
    doctorId: number
  ): Observable<Doctor> {

    return this.http
      .get<DoctorApiResponse>(
        `${this.apiUrl}/${doctorId}`
      )
      .pipe(
        map((doctor) =>
          this.mapDoctor(doctor)
        )
      );
  }

  create(
    request: CreateDoctorRequest
  ): Observable<Doctor> {

    const payload = {
      nombreCompleto:
        request.fullName.trim(),

      especialidad:
        request.specialty.trim(),

      intervalMinutes:
        request.intervalMinutes,

      username:
        request.username.trim(),

      password:
        request.password,
    };

    return this.http
      .post<DoctorApiResponse>(
        this.apiUrl,
        payload
      )
      .pipe(
        map((doctor) =>
          this.mapDoctor(doctor)
        )
      );
  }

  update(
    doctorId: number,
    request: UpdateDoctorRequest
  ): Observable<Doctor> {

    const payload = {
      nombreCompleto:
        request.fullName.trim(),

      especialidad:
        request.specialty.trim(),

      intervalMinutes:
        request.intervalMinutes,
    };

    return this.http
      .put<DoctorApiResponse>(
        `${this.apiUrl}/${doctorId}`,
        payload
      )
      .pipe(
        map((doctor) =>
          this.mapDoctor(doctor)
        )
      );
  }

  delete(
    doctorId: number
  ): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${doctorId}`
    );
  }

  private mapDoctor(
    doctor: DoctorApiResponse
  ): Doctor {

    return {
      id:
        doctor.id,

      userId:
        doctor.userId,

      username:
        doctor.username,

      fullName:
        doctor.nombreCompleto,

      specialty:
        doctor.especialidad,

      intervalMinutes:
        doctor.intervalMinutes,

      active:
        doctor.active,

      userStatus:
        doctor.userStatus,

      userRole:
        doctor.userRole,
    };
  }
}