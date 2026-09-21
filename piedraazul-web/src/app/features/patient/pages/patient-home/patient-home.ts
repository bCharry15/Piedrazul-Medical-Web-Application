import {
  CommonModule,
} from '@angular/common';

import {
  Component,
  OnInit,
  inject,
} from '@angular/core';

import {
  FormsModule,
} from '@angular/forms';

import {
  Auth,
} from '../../../../core/auth/auth';

import {
  LanguageService,
} from '../../../../core/i18n/language';

import {
  AppointmentService,
} from '../../../../core/services/appointment';

import {
  AvailabilityService,
} from '../../../../core/services/availability';

import {
  DoctorService,
} from '../../../../core/services/doctor';

import {
  PatientService,
} from '../../../../core/services/patient';

import {
  CreateAppointmentRequest,
  CreatedAppointment,
  PatientAppointment,
  RescheduleAppointmentRequest,
} from '../../../../shared/models/appointment';

import {
  Availability,
} from '../../../../shared/models/availability';

import {
  Doctor,
} from '../../../../shared/models/doctor';

import {
  PatientProfile,
} from '../../../../shared/models/patient';


@Component({
  selector: 'app-patient-home',

  imports: [
    CommonModule,
    FormsModule,
  ],

  templateUrl:
    './patient-home.html',

  styleUrl:
    './patient-home.scss',
})
export class PatientHome
  implements OnInit {

  private readonly auth =
    inject(Auth);

  readonly i18n =
    inject(LanguageService);

  private readonly doctorService =
    inject(DoctorService);

  private readonly availabilityService =
    inject(AvailabilityService);

  private readonly patientService =
    inject(PatientService);

  private readonly appointmentService =
    inject(AppointmentService);


  patient:
    PatientProfile | null =
    null;

  doctors:
    Doctor[] = [];

  appointments:
    PatientAppointment[] = [];


  selectedDoctorId:
    number | null =
    null;

  selectedDate =
    '';

  selectedTime =
    '';

  notes =
    '';


  availability:
    Availability | null =
    null;

  createdAppointment:
    CreatedAppointment | null =
    null;


  loadingProfile =
    false;

  loadingDoctors =
    false;

  loadingAvailability =
    false;

  loadingAppointments =
    false;

  booking =
    false;


  error =
    '';

  success =
    '';

  appointmentsError =
    '';

  appointmentActionSuccess =
    '';

  appointmentActionError =
    '';


  /*
   * Reprogramación
   */

  reschedulingAppointment:
    PatientAppointment | null =
    null;

  rescheduleDate =
    '';

  rescheduleTime =
    '';

  rescheduleReason =
    '';

  rescheduleAvailability:
    Availability | null =
    null;

  loadingRescheduleAvailability =
    false;

  rescheduling =
    false;

  rescheduleError =
    '';

  rescheduleSuccess =
    '';


  /*
   * Cancelación
   */

  cancellingAppointment:
    PatientAppointment | null =
    null;

  cancellationReason =
    '';

  cancelling =
    false;

  cancellationError =
    '';


  readonly minDate =
    this.getTodayLocalDate();


  ngOnInit():
    void {

    this.loadPatientProfile();

    this.loadDoctors();
  }


  /*
   * ==================================================
   * DASHBOARD
   * ==================================================
   */


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


  get patientName():
    string {

    if (!this.patient) {
      return '';
    }

    return [
      this.patient.firstNames,
      this.patient.lastNames,
    ]
      .filter(Boolean)
      .join(' ');
  }


  get patientFirstName():
    string {

    return (
      this.patient
        ?.firstNames
        ?.trim()
        .split(/\s+/)[0] ??
      ''
    );
  }


  get activeAppointments():
    PatientAppointment[] {

    const activeStatuses = [
      'PROGRAMADA',
      'CONFIRMADA',
      'PENDIENTE',
    ];

    return this.appointments
      .filter(
        (appointment) =>
          activeStatuses.includes(
            appointment.status
          )
      );
  }


  get historyAppointments():
    PatientAppointment[] {

    const finalStatuses = [
      'ATENDIDA',
      'COMPLETADA',
      'CANCELADA',
      'NO_VINO',
    ];

    return this.appointments
      .filter(
        (appointment) =>
          finalStatuses.includes(
            appointment.status
          )
      );
  }


  get nextAppointment():
    PatientAppointment | null {

    const now =
      new Date();

    const candidates =
      this.activeAppointments
        .filter(
          (appointment) => {

            const date =
              this.appointmentDateTime(
                appointment
              );

            return (
              date !== null &&
              date.getTime() >=
                now.getTime()
            );
          }
        )
        .sort(
          (a, b) => {

            const aDate =
              this.appointmentDateTime(a);

            const bDate =
              this.appointmentDateTime(b);

            if (
              !aDate ||
              !bDate
            ) {
              return 0;
            }

            return (
              aDate.getTime() -
              bDate.getTime()
            );
          }
        );

    return (
      candidates[0] ??
      null
    );
  }


  formatDate(
    date: string
  ): string {

    if (!date) {
      return '';
    }

    const parts =
      date.split('-');

    if (
      parts.length !==
      3
    ) {
      return date;
    }

    const [
      year,
      month,
      day,
    ] = parts;

    return (
      `${day}/${month}/${year}`
    );
  }


  formatTime(
    time: string
  ): string {

    if (!time) {
      return '';
    }

    return time.substring(
      0,
      5
    );
  }


  scrollToSection(
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


  selectDoctor(
    doctor: Doctor
  ): void {

    this.selectedDoctorId =
      doctor.id;

    this.resetAvailability();

    this.scrollToSection(
      'agendar-cita'
    );
  }


  /*
   * ==================================================
   * PERFIL
   * ==================================================
   */


  loadPatientProfile():
    void {

    const username =
      this.auth
        .getUsername();

    if (!username) {

      this.error =
        this.i18n.t(
          'couldNotIdentifyPatient'
        );

      return;
    }


    this.loadingProfile =
      true;


    this.patientService
      .getProfileByUsername(
        username
      )
      .subscribe({

        next:
          (patient) => {

            this.patient =
              patient;

            this.loadingProfile =
              false;

            this.loadAppointments(
              patient.documentNumber
            );
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.error =
              this.i18n.t(
                'couldNotLoadPatient'
              );

            this.loadingProfile =
              false;
          },
      });
  }


  /*
   * ==================================================
   * MÉDICOS
   * ==================================================
   */


  loadDoctors():
    void {

    this.loadingDoctors =
      true;


    this.doctorService
      .getAll()
      .subscribe({

        next:
          (doctors) => {

            this.doctors =
              doctors.filter(
                (doctor) =>
                  doctor.active
              );

            this.loadingDoctors =
              false;
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.error =
              this.i18n.t(
                'couldNotLoadDoctors'
              );

            this.loadingDoctors =
              false;
          },
      });
  }


  /*
   * ==================================================
   * CITAS
   * ==================================================
   */


  loadAppointments(
    documentNumber?: string
  ): void {

    const document =
      documentNumber ??
      this.patient
        ?.documentNumber;

    if (!document) {
      return;
    }


    this.loadingAppointments =
      true;

    this.appointmentsError =
      '';


    this.appointmentService
      .getByPatientDocumentNumber(
        document
      )
      .subscribe({

        next:
          (appointments) => {

            this.appointments =
              appointments;

            this.loadingAppointments =
              false;
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.appointmentsError =
              this.i18n.t(
                'couldNotLoadAppointments'
              );

            this.loadingAppointments =
              false;
          },
      });
  }


  /*
   * ==================================================
   * DISPONIBILIDAD
   * ==================================================
   */


  checkAvailability():
    void {

    if (
      this.selectedDoctorId ===
      null
    ) {

      this.error =
        this.i18n.t(
          'pleaseSelectDoctor'
        );

      return;
    }


    if (
      !this.selectedDate
    ) {

      this.error =
        this.i18n.t(
          'pleaseSelectDate'
        );

      return;
    }


    this.loadingAvailability =
      true;

    this.error =
      '';

    this.success =
      '';

    this.availability =
      null;

    this.selectedTime =
      '';

    this.createdAppointment =
      null;


    this.availabilityService
      .getByDoctorAndDate(
        this.selectedDoctorId,
        this.selectedDate
      )
      .subscribe({

        next:
          (availability) => {

            this.availability =
              availability;

            this.loadingAvailability =
              false;
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.error =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotLoadAvailability'
                )
              );

            this.loadingAvailability =
              false;
          },
      });
  }


  selectTime(
    time: string
  ): void {

    this.selectedTime =
      time;

    this.error =
      '';

    this.success =
      '';
  }


  /*
   * ==================================================
   * CREAR CITA
   * ==================================================
   */


  bookAppointment():
    void {

    if (!this.patient) {

      this.error =
        this.i18n.t(
          'patientProfileUnavailable'
        );

      return;
    }


    if (
      this.selectedDoctorId ===
      null
    ) {

      this.error =
        this.i18n.t(
          'pleaseSelectDoctor'
        );

      return;
    }


    if (
      !this.selectedDate
    ) {

      this.error =
        this.i18n.t(
          'pleaseSelectDate'
        );

      return;
    }


    if (
      !this.selectedTime
    ) {

      this.error =
        this.i18n.t(
          'pleaseSelectTime'
        );

      return;
    }


    const request:
      CreateAppointmentRequest = {

      documentNumber:
        this.patient
          .documentNumber,

      documentType:
        this.patient
          .documentType,

      firstNames:
        this.patient
          .firstNames,

      lastNames:
        this.patient
          .lastNames,

      phone:
        this.patient.phone,

      gender:
        this.patient.gender,

      birthDate:
        this.patient.birthDate,

      email:
        this.patient.email,

      doctorId:
        this.selectedDoctorId,

      date:
        this.selectedDate,

      time:
        this.selectedTime,

      notes:
        this.notes.trim(),
    };


    this.booking =
      true;

    this.error =
      '';

    this.success =
      '';


    this.appointmentService
      .create(
        request
      )
      .subscribe({

        next:
          (appointment) => {

            this.createdAppointment =
              appointment;

            this.success =
              `${this.i18n.t(
                'appointmentCreatedSuccessfully'
              )} #${appointment.id}`;

            this.booking =
              false;

            this.notes =
              '';

            this.selectedTime =
              '';

            this.refreshAvailability();

            this.loadAppointments(
              this.patient!
                .documentNumber
            );
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.error =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotCreateAppointment'
                )
              );

            this.booking =
              false;
          },
      });
  }


  resetAvailability():
    void {

    this.availability =
      null;

    this.selectedTime =
      '';

    this.createdAppointment =
      null;

    this.error =
      '';

    this.success =
      '';
  }


  /*
   * ==================================================
   * REPROGRAMAR
   * ==================================================
   */


  canReschedule(
    appointment:
      PatientAppointment
  ): boolean {

    const finalStatuses = [
      'ATENDIDA',
      'COMPLETADA',
      'CANCELADA',
      'NO_VINO',
    ];

    return (
      !finalStatuses.includes(
        appointment.status
      )
    );
  }


  startReschedule(
    appointment:
      PatientAppointment
  ): void {

    if (
      !this.canReschedule(
        appointment
      )
    ) {
      return;
    }


    this.cancelCancellation();

    this.appointmentActionSuccess =
      '';

    this.appointmentActionError =
      '';

    this.reschedulingAppointment =
      appointment;

    this.rescheduleDate =
      appointment.date;

    this.rescheduleTime =
      '';

    this.rescheduleReason =
      '';

    this.rescheduleAvailability =
      null;

    this.rescheduleError =
      '';

    this.rescheduleSuccess =
      '';


    setTimeout(
      () => {

        this.scrollToSection(
          'reschedule-panel'
        );
      },
      50
    );
  }


  cancelReschedule():
    void {

    this.reschedulingAppointment =
      null;

    this.rescheduleDate =
      '';

    this.rescheduleTime =
      '';

    this.rescheduleReason =
      '';

    this.rescheduleAvailability =
      null;

    this.rescheduleError =
      '';

    this.rescheduleSuccess =
      '';

    this.loadingRescheduleAvailability =
      false;

    this.rescheduling =
      false;
  }


  resetRescheduleAvailability():
    void {

    this.rescheduleAvailability =
      null;

    this.rescheduleTime =
      '';

    this.rescheduleError =
      '';

    this.rescheduleSuccess =
      '';
  }


  checkRescheduleAvailability():
    void {

    if (
      !this.reschedulingAppointment
    ) {
      return;
    }


    if (
      !this.rescheduleDate
    ) {

      this.rescheduleError =
        this.i18n.t(
          'pleaseSelectNewDate'
        );

      return;
    }


    this.loadingRescheduleAvailability =
      true;

    this.rescheduleError =
      '';

    this.rescheduleSuccess =
      '';

    this.rescheduleAvailability =
      null;

    this.rescheduleTime =
      '';


    this.availabilityService
      .getByDoctorAndDate(
        this.reschedulingAppointment
          .doctorId,
        this.rescheduleDate
      )
      .subscribe({

        next:
          (availability) => {

            this.rescheduleAvailability =
              availability;

            this.loadingRescheduleAvailability =
              false;
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.rescheduleError =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotLoadNewAvailability'
                )
              );

            this.loadingRescheduleAvailability =
              false;
          },
      });
  }


  selectRescheduleTime(
    time: string
  ): void {

    this.rescheduleTime =
      time;

    this.rescheduleError =
      '';

    this.rescheduleSuccess =
      '';
  }


  confirmReschedule():
    void {

    if (
      !this.reschedulingAppointment
    ) {
      return;
    }


    if (
      !this.rescheduleDate
    ) {

      this.rescheduleError =
        this.i18n.t(
          'pleaseSelectNewDate'
        );

      return;
    }


    if (
      !this.rescheduleTime
    ) {

      this.rescheduleError =
        this.i18n.t(
          'pleaseSelectNewTime'
        );

      return;
    }


    const username =
      this.auth
        .getUsername() ??
      'patient';


    const request:
      RescheduleAppointmentRequest = {

      newDate:
        this.rescheduleDate,

      newTime:
        this.rescheduleTime,

      responsible:
        username,

      reason:
        this.rescheduleReason
          .trim() ||
        this.i18n.t(
          'rescheduleRequestedByPatient'
        ),
    };


    this.rescheduling =
      true;

    this.rescheduleError =
      '';

    this.rescheduleSuccess =
      '';


    this.appointmentService
      .reschedule(
        this.reschedulingAppointment
          .id,
        request
      )
      .subscribe({

        next:
          (response) => {

            this.rescheduleSuccess =
              response.message ||
              this.i18n.t(
                'confirmReschedule'
              );

            this.rescheduling =
              false;

            if (
              this.patient
            ) {

              this.loadAppointments(
                this.patient
                  .documentNumber
              );
            }

            this.refreshAvailability();

            this.rescheduleDate =
              response.newDate;

            this.rescheduleTime =
              '';

            this.rescheduleAvailability =
              null;
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.rescheduleError =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotReschedule'
                )
              );

            this.rescheduling =
              false;
          },
      });
  }


  /*
   * ==================================================
   * CANCELACIÓN
   * ==================================================
   */


  canCancel(
    appointment:
      PatientAppointment
  ): boolean {

    return [
      'PROGRAMADA',
      'CONFIRMADA',
      'PENDIENTE',
    ].includes(
      appointment.status
    );
  }


  startCancellation(
    appointment:
      PatientAppointment
  ): void {

    if (
      !this.canCancel(
        appointment
      )
    ) {
      return;
    }


    this.cancelReschedule();

    this.appointmentActionSuccess =
      '';

    this.appointmentActionError =
      '';

    this.cancellingAppointment =
      appointment;

    this.cancellationReason =
      '';

    this.cancellationError =
      '';

    this.cancelling =
      false;


    setTimeout(
      () => {

        this.scrollToSection(
          'cancel-panel'
        );
      },
      50
    );
  }


  cancelCancellation():
    void {

    this.cancellingAppointment =
      null;

    this.cancellationReason =
      '';

    this.cancellationError =
      '';

    this.cancelling =
      false;
  }


  confirmCancellation():
    void {

    if (
      !this.cancellingAppointment
    ) {
      return;
    }


    const reason =
      this.cancellationReason
        .trim();

    if (!reason) {
      return;
    }


    this.cancelling =
      true;

    this.cancellationError =
      '';

    this.appointmentActionSuccess =
      '';

    this.appointmentActionError =
      '';


    const appointmentId =
      this.cancellingAppointment
        .id;


    this.appointmentService
      .cancel(
        appointmentId,
        reason
      )
      .subscribe({

        next:
          () => {

            this.cancelling =
              false;

            this.appointmentActionSuccess =
              this.i18n.t(
                'cancellationSuccess'
              );

            this.cancelCancellation();

            if (
              this.patient
            ) {

              this.loadAppointments(
                this.patient
                  .documentNumber
              );
            }

            this.refreshAvailability();
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.cancelling =
              false;

            this.cancellationError =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotCancelAppointment'
                )
              );
          },
      });
  }

  getAppointmentNotes(
  notes:
    string |
    null |
    undefined
): string[] {

  if (
    !notes ||
    !notes.trim()
  ) {

    return [];
  }

  return notes
    .split('|')
    .map(
      (note) =>
        note.trim()
    )
    .filter(
      (note) =>
        note.length > 0
    );
}


  /*
   * ==================================================
   * AUXILIARES
   * ==================================================
   */


  private refreshAvailability():
    void {

    if (
      this.selectedDoctorId ===
        null ||
      !this.selectedDate
    ) {
      return;
    }


    this.availabilityService
      .getByDoctorAndDate(
        this.selectedDoctorId,
        this.selectedDate
      )
      .subscribe({

        next:
          (availability) => {

            this.availability =
              availability;
          },


        error:
          (error) => {

            console.error(
              error
            );
          },
      });
  }


  private appointmentDateTime(
    appointment:
      PatientAppointment
  ): Date | null {

    if (
      !appointment.date ||
      !appointment.time
    ) {
      return null;
    }


    const value =
      new Date(
        `${appointment.date}T${appointment.time}`
      );


    if (
      Number.isNaN(
        value.getTime()
      )
    ) {
      return null;
    }


    return value;
  }


  private extractErrorMessage(
    error: any,
    fallback: string
  ): string {

    if (
      error?.error?.message
    ) {
      return error.error.message;
    }


    if (
      error?.error?.detail
    ) {
      return error.error.detail;
    }


    if (
      error?.message
    ) {
      return error.message;
    }


    return fallback;
  }


  private getTodayLocalDate():
    string {

    const today =
      new Date();

    const year =
      today.getFullYear();

    const month =
      String(
        today.getMonth() + 1
      ).padStart(
        2,
        '0'
      );

    const day =
      String(
        today.getDate()
      ).padStart(
        2,
        '0'
      );


    return (
      `${year}-${month}-${day}`
    );
  }
}