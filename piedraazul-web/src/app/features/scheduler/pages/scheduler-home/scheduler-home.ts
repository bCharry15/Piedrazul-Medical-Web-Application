import { CommonModule } from '@angular/common';

import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject,
} from '@angular/core';

import { FormsModule } from '@angular/forms';

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
  Availability,
} from '../../../../shared/models/availability';

import {
  PatientAppointment,
} from '../../../../shared/models/appointment';

import {
  Doctor,
} from '../../../../shared/models/doctor';


type SchedulerAction =
  'RESCHEDULE' |
  'CANCEL' |
  null;


@Component({
  imports: [
    CommonModule,
    FormsModule,
  ],

  selector:
    'app-scheduler-home',

  styleUrl:
    './scheduler-home.scss',

  templateUrl:
    './scheduler-home.html',
})
export class SchedulerHome
  implements OnInit {

  readonly i18n =
    inject(LanguageService);

  private readonly auth =
    inject(Auth);

  private readonly doctorService =
    inject(DoctorService);

  private readonly appointmentService =
    inject(AppointmentService);

  private readonly availabilityService =
    inject(AvailabilityService);

  private readonly cdr =
    inject(ChangeDetectorRef);


  doctors:
    Doctor[] = [];

  appointments:
    PatientAppointment[] = [];


  selectedDoctorId:
    number | null =
    null;

  selectedDate =
    this.getTodayLocalDate();

  readonly minDate =
    this.getTodayLocalDate();


  loadingDoctors =
    false;

  loadingAppointments =
    false;

  exportingSchedule =
    false;


  doctorsError =
    '';

  appointmentsError =
    '';

  success =
    '';


  actionAppointment:
    PatientAppointment | null =
    null;

  action:
    SchedulerAction =
    null;

  actionError =
    '';


  rescheduleDate =
    '';

  rescheduleReason =
    '';

  rescheduleAvailability:
    Availability | null =
    null;

  selectedRescheduleTime =
    '';

  loadingRescheduleAvailability =
    false;

  processingAction =
    false;


  cancellationReason =
    '';


  ngOnInit():
    void {

    this.loadDoctors();
  }


  /*
   * ==========================================
   * DASHBOARD
   * ==========================================
   */


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


  get schedulerUsername():
    string {

    const username =
      (
        this.auth.getUsername() ??
        ''
      ).trim();

    return (
      username ||
      this.text(
        'Agendador',
        'Scheduler'
      )
    );
  }


  get totalAppointments():
    number {

    return (
      this.appointments.length
    );
  }


  get scheduledAppointments():
    number {

    return this.appointments
      .filter(
        appointment =>
          appointment.status ===
          'PROGRAMADA'
      )
      .length;
  }


  get confirmedAppointments():
    number {

    return this.appointments
      .filter(
        appointment =>
          appointment.status ===
          'CONFIRMADA'
      )
      .length;
  }


  get attendedAppointments():
    number {

    return this.appointments
      .filter(
        appointment =>
          appointment.status ===
            'ATENDIDA' ||
          appointment.status ===
            'COMPLETADA'
      )
      .length;
  }


  get cancelledAppointments():
    number {

    return this.appointments
      .filter(
        appointment =>
          appointment.status ===
          'CANCELADA'
      )
      .length;
  }


  get selectedDateIsToday():
    boolean {

    return (
      this.selectedDate ===
      this.getTodayLocalDate()
    );
  }


  selectedDoctor():
    Doctor | undefined {

    return this.doctors.find(
      doctor =>
        doctor.id ===
        this.selectedDoctorId
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
      parts.length !== 3
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


  patientInitials(
    patientName: string
  ): string {

    if (!patientName) {
      return 'P';
    }

    const parts =
      patientName
        .trim()
        .split(/\s+/)
        .filter(Boolean);

    return parts
      .slice(0, 2)
      .map(
        part =>
          part
            .charAt(0)
            .toUpperCase()
      )
      .join('');
  }


  getObservationEntries(
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
        note =>
          note.trim()
      )
      .filter(
        note =>
          note.length > 0
      );
  }


  scrollToSection(
    id: string
  ): void {

    document
      .getElementById(id)
      ?.scrollIntoView({
        behavior:
          'smooth',

        block:
          'start',
      });
  }


  goToToday():
    void {

    this.selectedDate =
      this.getTodayLocalDate();

    this.onDateChange();

    if (
      this.selectedDoctorId !==
      null
    ) {

      this.loadAppointments();
    }

    setTimeout(
      () => {

        this.scrollToSection(
          'schedule-search'
        );
      },
      50
    );
  }


  /*
   * ==========================================
   * MÉDICOS
   * ==========================================
   */


  loadDoctors():
    void {

    this.loadingDoctors =
      true;

    this.doctorsError =
      '';


    this.doctorService
      .getAll()
      .subscribe({

        next:
          (doctors) => {

            this.doctors =
              doctors.filter(
                doctor =>
                  doctor.active &&
                  doctor.userStatus ===
                    'ACTIVE'
              );

            this.loadingDoctors =
              false;

            this.cdr
              .detectChanges();
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.doctorsError =
              this.i18n.t(
                'couldNotLoadDoctors'
              );

            this.loadingDoctors =
              false;

            this.cdr
              .detectChanges();
          },
      });
  }


  /*
   * ==========================================
   * AGENDA
   * ==========================================
   */


  loadAppointments():
    void {

    if (
      this.selectedDoctorId ===
        null ||
      !this.selectedDate
    ) {
      return;
    }


    this.loadingAppointments =
      true;

    this.appointmentsError =
      '';

    this.appointments =
      [];

    this.closeAction();


    this.appointmentService
      .getByDoctorAndDate(
        this.selectedDoctorId,
        this.selectedDate
      )
      .subscribe({

        next:
          (response) => {

            this.appointments =
              response.appointments;

            this.loadingAppointments =
              false;

            this.cdr
              .detectChanges();
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.appointmentsError =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotLoadSchedulerAppointments'
                )
              );

            this.loadingAppointments =
              false;

            this.cdr
              .detectChanges();
          },
      });
  }


  onDoctorChange():
    void {

    this.appointments =
      [];

    this.appointmentsError =
      '';

    this.success =
      '';

    this.closeAction();
  }


  onDateChange():
    void {

    this.appointments =
      [];

    this.appointmentsError =
      '';

    this.success =
      '';

    this.closeAction();
  }


  /*
   * ==========================================
   * REGLAS DE GESTIÓN
   * ==========================================
   */


  canManageAppointment(
    appointment:
      PatientAppointment
  ): boolean {

    return [
      'PROGRAMADA',
      'CONFIRMADA',
    ].includes(
      appointment.status
    );
  }


  /*
   * ==========================================
   * REPROGRAMAR
   * ==========================================
   */


  startReschedule(
    appointment:
      PatientAppointment
  ): void {

    this.actionAppointment =
      appointment;

    this.action =
      'RESCHEDULE';

    this.actionError =
      '';

    this.success =
      '';

    this.rescheduleDate =
      appointment.date;

    this.rescheduleReason =
      '';

    this.rescheduleAvailability =
      null;

    this.selectedRescheduleTime =
      '';

    this.cancellationReason =
      '';


    setTimeout(
      () => {

        this.scrollToSection(
          'scheduler-action-panel'
        );
      },
      50
    );
  }


  checkRescheduleAvailability():
    void {

    if (
      !this.actionAppointment
    ) {
      return;
    }


    if (
      !this.rescheduleDate
    ) {

      this.actionError =
        this.i18n.t(
          'pleaseSelectNewDate'
        );

      return;
    }


    this.actionError =
      '';

    this.selectedRescheduleTime =
      '';

    this.rescheduleAvailability =
      null;

    this.loadingRescheduleAvailability =
      true;


    this.availabilityService
      .getByDoctorAndDate(
        this.actionAppointment
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

            this.cdr
              .detectChanges();
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.actionError =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotLoadNewAvailability'
                )
              );

            this.loadingRescheduleAvailability =
              false;

            this.cdr
              .detectChanges();
          },
      });
  }


  confirmReschedule():
    void {

    if (
      !this.actionAppointment
    ) {
      return;
    }


    if (
      !this.rescheduleDate
    ) {

      this.actionError =
        this.i18n.t(
          'pleaseSelectNewDate'
        );

      return;
    }


    if (
      !this.selectedRescheduleTime
    ) {

      this.actionError =
        this.i18n.t(
          'pleaseSelectNewTime'
        );

      return;
    }


    const responsible =
      (
        this.auth
          .getUsername() ??
        ''
      )
        .trim() ||
      'scheduler';


    const reason =
      this.rescheduleReason
        .trim() ||
      this.i18n.t(
        'schedulerRescheduleDefaultReason'
      );


    this.processingAction =
      true;

    this.actionError =
      '';


    this.appointmentService
      .reschedule(
        this.actionAppointment.id,
        {
          newDate:
            this.rescheduleDate,

          newTime:
            this.selectedRescheduleTime,

          responsible,

          reason,
        }
      )
      .subscribe({

        next:
          () => {

            this.processingAction =
              false;

            this.closeAction();

            this.success =
              this.i18n.t(
                'rescheduleSuccess'
              );

            this.loadAppointments();

            this.cdr
              .detectChanges();
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.processingAction =
              false;

            this.actionError =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotReschedule'
                )
              );

            this.cdr
              .detectChanges();
          },
      });
  }


  /*
   * ==========================================
   * CANCELAR
   * ==========================================
   */


  startCancel(
    appointment:
      PatientAppointment
  ): void {

    this.actionAppointment =
      appointment;

    this.action =
      'CANCEL';

    this.actionError =
      '';

    this.success =
      '';

    this.cancellationReason =
      '';

    this.rescheduleAvailability =
      null;

    this.selectedRescheduleTime =
      '';


    setTimeout(
      () => {

        this.scrollToSection(
          'scheduler-action-panel'
        );
      },
      50
    );
  }


  confirmCancellation():
    void {

    if (
      !this.actionAppointment
    ) {
      return;
    }


    const reason =
      this.cancellationReason
        .trim();


    if (!reason) {

      this.actionError =
        this.i18n.t(
          'cancellationReasonRequired'
        );

      return;
    }


    this.processingAction =
      true;

    this.actionError =
      '';


    this.appointmentService
      .cancel(
        this.actionAppointment.id,
        reason
      )
      .subscribe({

        next:
          () => {

            this.processingAction =
              false;

            this.closeAction();

            this.success =
              this.i18n.t(
                'cancellationSuccess'
              );

            this.loadAppointments();

            this.cdr
              .detectChanges();
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.processingAction =
              false;

            this.actionError =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotCancelAppointment'
                )
              );

            this.cdr
              .detectChanges();
          },
      });
  }


  /*
   * ==========================================
   * EXPORTAR CSV
   * ==========================================
   */


  exportSchedule():
    void {

    if (
      this.selectedDoctorId ===
        null ||
      !this.selectedDate
    ) {
      return;
    }


    this.exportingSchedule =
      true;

    this.appointmentsError =
      '';


    this.appointmentService
      .exportByDoctorAndDate(
        this.selectedDoctorId,
        this.selectedDate
      )
      .subscribe({

        next:
          (response) => {

            const blob =
              response.body;


            if (!blob) {

              this.exportingSchedule =
                false;

              this.appointmentsError =
                this.i18n.t(
                  'couldNotExportSchedule'
                );

              this.cdr
                .detectChanges();

              return;
            }


            let fileName =
              `appointments_doctor_${this.selectedDoctorId}_${this.selectedDate}.csv`;


            const disposition =
              response.headers.get(
                'Content-Disposition'
              );


            if (disposition) {

              const match =
                disposition.match(
                  /filename="?([^"]+)"?/i
                );

              if (
                match &&
                match[1]
              ) {

                fileName =
                  match[1];
              }
            }


            const url =
              window.URL
                .createObjectURL(
                  blob
                );


            const link =
              document
                .createElement(
                  'a'
                );

            link.href =
              url;

            link.download =
              fileName;


            document.body
              .appendChild(
                link
              );

            link.click();


            document.body
              .removeChild(
                link
              );


            window.URL
              .revokeObjectURL(
                url
              );


            this.exportingSchedule =
              false;

            this.cdr
              .detectChanges();
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.exportingSchedule =
              false;

            this.appointmentsError =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotExportSchedule'
                )
              );

            this.cdr
              .detectChanges();
          },
      });
  }


  /*
   * ==========================================
   * CERRAR PANEL
   * ==========================================
   */


  closeAction():
    void {

    this.actionAppointment =
      null;

    this.action =
      null;

    this.actionError =
      '';

    this.rescheduleDate =
      '';

    this.rescheduleReason =
      '';

    this.rescheduleAvailability =
      null;

    this.selectedRescheduleTime =
      '';

    this.loadingRescheduleAvailability =
      false;

    this.processingAction =
      false;

    this.cancellationReason =
      '';
  }


  /*
   * ==========================================
   * AUXILIARES
   * ==========================================
   */


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
      typeof error?.error ===
      'string'
    ) {
      return error.error;
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