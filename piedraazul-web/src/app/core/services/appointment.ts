import {
  HttpClient,
  HttpResponse,
} from '@angular/common/http';

import {
  Injectable,
  inject,
} from '@angular/core';

import {
  map,
  Observable,
} from 'rxjs';

import {
  ChangeAppointmentStatusRequest,
  ChangedAppointmentStatus,
  CreateAppointmentRequest,
  CreatedAppointment,
  DoctorAppointmentsResponse,
  PatientAppointment,
  RescheduleAppointmentRequest,
  RescheduledAppointment,
} from '../../shared/models/appointment';

interface CreateAppointmentApiRequest {
  numeroDocument: string;
  tipoDocument: string;
  nombres: string;
  apellidos: string;
  celular: string;
  gender: string;
  dateNacimiento: string | null;
  correo: string | null;
  doctorId: number;
  date: string;
  time: string;
  notes: string;
}

interface CreateAppointmentApiResponse {
  id: number;
  numeroDocument: string;
  nombrePatient: string;
  nombreDoctor: string;
  date: string;
  time: string;
  status: string;
}

interface PatientAppointmentApiResponse {
  id: number;
  patientId: number;
  patient: string;
  doctorId: number;
  doctor: string;
  date: string;
  time: string;
  status: string;
  notes: string | null;
}

interface PatientAppointmentWrapper {
  value: PatientAppointmentApiResponse[];
  Count?: number;
}

interface DoctorAppointmentsApiResponse {
  doctorId: number;
  doctor: string;
  date: string;
  cantidad: number;
  appointments: PatientAppointmentApiResponse[];
}

interface RescheduleAppointmentApiRequest {
  dateNueva: string;
  timeNueva: string;
  responsable: string;
  motivo: string;
}

interface RescheduleAppointmentApiResponse {
  id: number;
  patientId: number;
  patient: string;
  doctorId: number;
  doctor: string;
  date: string;
  time: string;
  status: string;
  notes: string;

  dateAnterior: string;
  timeAnterior: string;

  dateNueva: string;
  timeNueva: string;

  mensaje: string;
}

interface ChangeAppointmentStatusApiRequest {
  status: string;
  notes: string;
}

interface ChangeAppointmentStatusApiResponse {
  id: number;
  patientId: number;
  patient: string;
  doctorId: number;
  doctor: string;
  date: string;
  time: string;

  statusAnterior: string;
  statusNuevo: string;

  status: string;
  notes: string;

  mensaje: string;
}

@Injectable({
  providedIn: 'root',
})
export class AppointmentService {

  private readonly http =
    inject(HttpClient);

  private readonly apiUrl =
    'http://localhost:8081/api/appointments';

  create(
    request: CreateAppointmentRequest
  ): Observable<CreatedAppointment> {

    const payload: CreateAppointmentApiRequest = {
      numeroDocument:
        request.documentNumber,

      tipoDocument:
        request.documentType,

      nombres:
        request.firstNames,

      apellidos:
        request.lastNames,

      celular:
        request.phone,

      gender:
        request.gender,

      dateNacimiento:
        request.birthDate,

      correo:
        request.email,

      doctorId:
        request.doctorId,

      date:
        request.date,

      time:
        request.time,

      notes:
        request.notes,
    };

    return this.http
      .post<CreateAppointmentApiResponse>(
        this.apiUrl,
        payload
      )
      .pipe(
        map((response) => ({
          id:
            response.id,

          documentNumber:
            response.numeroDocument,

          patientName:
            response.nombrePatient,

          doctorName:
            response.nombreDoctor,

          date:
            response.date,

          time:
            response.time,

          status:
            response.status,
        }))
      );
  }

  getByPatientDocumentNumber(
    documentNumber: string
  ): Observable<PatientAppointment[]> {

    return this.http
      .get<
        PatientAppointmentApiResponse[] |
        PatientAppointmentWrapper
      >(
        `${this.apiUrl}/patient/${encodeURIComponent(
          documentNumber
        )}`
      )
      .pipe(
        map((response) => {

          const appointments =
            Array.isArray(response)
              ? response
              : response.value ?? [];

          return appointments.map(
            (appointment) =>
              this.mapAppointment(
                appointment
              )
          );
        })
      );
  }

  getByDoctorAndDate(
    doctorId: number,
    date: string
  ): Observable<DoctorAppointmentsResponse> {

    return this.http
      .get<DoctorAppointmentsApiResponse>(
        this.apiUrl,
        {
          params: {
            doctorId:
              doctorId.toString(),

            date,
          },
        }
      )
      .pipe(
        map((response) => ({
          doctorId:
            response.doctorId,

          doctorName:
            response.doctor,

          date:
            response.date,

          count:
            response.cantidad,

          appointments:
            (
              response.appointments ??
              []
            ).map(
              (appointment) =>
                this.mapAppointment(
                  appointment
                )
            ),
        }))
      );
  }

  exportByDoctorAndDate(
    doctorId: number,
    date: string
  ): Observable<HttpResponse<Blob>> {

    return this.http.get(
      `${this.apiUrl}/export`,
      {
        params: {
          doctorId:
            doctorId.toString(),

          date,
        },

        observe: 'response',

        responseType: 'blob',
      }
    );
  }

  reschedule(
    appointmentId: number,
    request: RescheduleAppointmentRequest
  ): Observable<RescheduledAppointment> {

    const payload:
      RescheduleAppointmentApiRequest = {

      dateNueva:
        request.newDate,

      timeNueva:
        request.newTime,

      responsable:
        request.responsible,

      motivo:
        request.reason,
    };

    return this.http
      .put<RescheduleAppointmentApiResponse>(
        `${this.apiUrl}/${appointmentId}/reschedule`,
        payload
      )
      .pipe(
        map((response) => ({
          id:
            response.id,

          patientId:
            response.patientId,

          patientName:
            response.patient,

          doctorId:
            response.doctorId,

          doctorName:
            response.doctor,

          date:
            response.date,

          time:
            response.time,

          status:
            response.status,

          notes:
            response.notes,

          previousDate:
            response.dateAnterior,

          previousTime:
            response.timeAnterior,

          newDate:
            response.dateNueva,

          newTime:
            response.timeNueva,

          message:
            response.mensaje,
        }))
      );
  }

  changeStatus(
    appointmentId: number,
    request: ChangeAppointmentStatusRequest
  ): Observable<ChangedAppointmentStatus> {

    const payload:
      ChangeAppointmentStatusApiRequest = {

      status:
        request.status,

      notes:
        request.notes,
    };

    return this.http
      .put<ChangeAppointmentStatusApiResponse>(
        `${this.apiUrl}/${appointmentId}/status`,
        payload
      )
      .pipe(
        map((response) => ({
          id:
            response.id,

          patientId:
            response.patientId,

          patientName:
            response.patient,

          doctorId:
            response.doctorId,

          doctorName:
            response.doctor,

          date:
            response.date,

          time:
            response.time,

          previousStatus:
            response.statusAnterior,

          newStatus:
            response.statusNuevo,

          status:
            response.status,

          notes:
            response.notes,

          message:
            response.mensaje,
        }))
      );
  }

  cancel(
    appointmentId: number,
    reason: string
  ): Observable<ChangedAppointmentStatus> {

    return this.changeStatus(
      appointmentId,
      {
        status:
          'CANCELADA',

        notes:
          reason,
      }
    );
  }

  private mapAppointment(
    appointment:
      PatientAppointmentApiResponse
  ): PatientAppointment {

    return {
      id:
        appointment.id,

      patientId:
        appointment.patientId,

      patientName:
        appointment.patient,

      doctorId:
        appointment.doctorId,

      doctorName:
        appointment.doctor,

      date:
        appointment.date,

      time:
        appointment.time,

      status:
        appointment.status,

      notes:
        appointment.notes ?? '',
    };
  }
}