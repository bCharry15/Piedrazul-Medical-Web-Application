import { CommonModule } from '@angular/common';
import { PatientHistory } from '../../../../shared/components/patient-history/patient-history';
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
  AppointmentService,type WeeklyAppointmentReport
} from '../../../../core/services/appointment';

import {
  DoctorService,
} from '../../../../core/services/doctor';

import {
  PatientAppointment,
} from '../../../../shared/models/appointment';

import {
  Doctor,
} from '../../../../shared/models/doctor';

import {
  AvailabilityService,
} from '../../../../core/services/availability';

import {
  Availability,
} from '../../../../shared/models/availability';


@Component({
  imports: [
    CommonModule,
    FormsModule,
    PatientHistory,
  ],

  selector: 'app-doctor-home',

  styleUrl:
    './doctor-home.scss',

  templateUrl:
    './doctor-home.html',
})
export class DoctorHome
  implements OnInit {

  private readonly auth =
    inject(Auth);

  readonly i18n =
    inject(LanguageService);

  private readonly doctorService =
    inject(DoctorService);

  private readonly appointmentService =
    inject(AppointmentService);

  private readonly cdr =
    inject(ChangeDetectorRef);

  private readonly availabilityService =
    inject(AvailabilityService);


  doctor:
    Doctor | null =
    null;

  appointments:
    PatientAppointment[] = [];


  selectedDate =
    this.getTodayLocalDate();


  loadingDoctor =
    false;

  loadingAppointments =
    false;


  error =
    '';

  success =
    '';


  statusActionAppointment:
    PatientAppointment | null =
    null;


  targetStatus:
    'CONFIRMADA' |
    'ATENDIDA' |
    'CANCELADA' |
    null =
    null;


  statusNotes =
    '';

  updatingStatus =
    false;

  statusError =
    '';


    reportYear: number | null = new Date().getFullYear();
  displayedReportYear: number | null = null;
  monthlyReport: { month: number; total: number }[] = [];
  loadingReport = false;
  reportError = '';

    reportScaleMax = 1;

  reportMonthName(month: number): string {
    return new Intl.DateTimeFormat(this.text('es-CO', 'en'), {
      month: 'long',
    }).format(new Date(2000, month - 1, 1));
  }

  loadMonthlyReport(): void {
    const year = this.reportYear;

    if (this.loadingReport || year === null || !Number.isInteger(year)
        || year < 1900 || year > 9998) {
      return;
    }

    this.loadingReport = true;
    this.reportError = '';
    this.displayedReportYear = null;

    this.appointmentService.getMonthlyReport(year).subscribe({
      next: (report) => {
        this.monthlyReport = report;
        this.reportScaleMax = Math.max(1, ...report.map((row) => row.total));
        this.displayedReportYear = year;
        this.loadingReport = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.reportError = this.text(
          'No se pudo cargar el reporte mensual.',
          'Could not load the monthly report.',
        );
        this.loadingReport = false;
        this.cdr.markForCheck();
      },
    });
  }

    weeklyReportDate = this.getTodayLocalDate();
  weeklyReport: WeeklyAppointmentReport | null = null;
  loadingWeeklyReport = false;
  weeklyReportError = '';

  get weeklyAttendancePercent(): number {
    const report = this.weeklyReport;
    return report?.total ? (100 * report.attended) / report.total : 0;
  }

  loadWeeklyReport(): void {
    if (!this.weeklyReportDate || this.loadingWeeklyReport) {
      return;
    }

    this.loadingWeeklyReport = true;
    this.weeklyReportError = '';
    this.weeklyReport = null;

    this.appointmentService.getWeeklyReport(this.weeklyReportDate).subscribe({
      next: (report) => {
        this.weeklyReport = report;
        this.loadingWeeklyReport = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.weeklyReportError = this.text(
          'No se pudo cargar el reporte semanal.',
          'Could not load the weekly report.',
        );
        this.loadingWeeklyReport = false;
        this.cdr.markForCheck();
      },
    });
  }


  exportingSchedule = false;

exportSchedule(): void {
  if (!this.doctor || !this.selectedDate || this.exportingSchedule) {
    return;
  }

  const doctorId = this.doctor.id;
  const date = this.selectedDate;

  this.exportingSchedule = true;
  this.error = '';

  this.appointmentService
    .exportByDoctorAndDate(doctorId, date)
    .subscribe({
      next: (response) => {
        const blob = response.body;

        if (blob) {
          const url = URL.createObjectURL(blob);
          const link = document.createElement('a');

          link.href = url;
          link.download = `appointments_doctor_${doctorId}_${date}.csv`;

          document.body.appendChild(link);
          link.click();
          link.remove();

          setTimeout(() => URL.revokeObjectURL(url), 1000);
        } else {
          this.error = this.text(
            'La exportación no devolvió un archivo.',
            'The export did not return a file.'
          );
        }

        this.exportingSchedule = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = this.text(
          'No se pudo exportar la agenda.',
          'Could not export the schedule.'
        );

        this.exportingSchedule = false;
        this.cdr.detectChanges();
      },
    });
}


  ngOnInit():
    void {

    this.loadDoctorProfile();
  }


  /*
   * ==========================================
   * TEXTOS / DASHBOARD
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


  get doctorFirstName():
    string {

    if (!this.doctor) {
      return '';
    }

    return (
      this.doctor
        .fullName
        ?.trim()
        .split(/\s+/)[0] ??
      ''
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


  patientInitials(
    name: string
  ): string {

    if (!name) {
      return 'P';
    }

    const parts =
      name
        .trim()
        .split(/\s+/)
        .filter(Boolean);

    return parts
      .slice(0, 2)
      .map(
        part =>
          part.charAt(0)
      )
      .join('')
      .toUpperCase();
  }


  scrollToSection(
    id: string
  ): void {

    document
      .getElementById(id)
      ?.scrollIntoView({
        behavior: 'smooth',
        block: 'start',
      });
  }


  goToToday():
    void {

    this.selectedDate =
      this.getTodayLocalDate();

    this.loadAppointments();

    setTimeout(
      () => {
        this.scrollToSection(
          'agenda-medica'
        );
      },
      50
    );
  }


  /*
   * ==========================================
   * PERFIL DEL MÉDICO
   * ==========================================
   */


  loadDoctorProfile():
    void {

    const username =
      (
        this.auth
          .getUsername() ??
        ''
      )
        .trim()
        .toLowerCase();


    if (!username) {

      this.error =
        this.i18n.t(
          'couldNotLoadDoctorProfile'
        );

      return;
    }


    this.loadingDoctor =
      true;

    this.error =
      '';


    this.doctorService
      .getAll()
      .subscribe({

        next:
          (doctors) => {

            const doctor =
              doctors.find(
                currentDoctor =>
                  currentDoctor
                    .username
                    ?.trim()
                    .toLowerCase()
                  ===
                  username
              );


            if (!doctor) {

              this.error =
                this.i18n.t(
                  'doctorProfileNotFound'
                );

              this.loadingDoctor =
                false;

              this.cdr
                .detectChanges();

              return;
            }


            this.doctor =
              doctor;

            this.loadingDoctor =
              false;


            this.loadAppointments();


            this.cdr
              .detectChanges();
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.error =
              this.i18n.t(
                'couldNotLoadDoctorProfile'
              );

            this.loadingDoctor =
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


  loadAppointments(
    preserveSuccess = false
  ): void {

    if (
      !this.doctor ||
      !this.selectedDate
    ) {
      return;
    }


    this.loadingAppointments =
      true;

    this.error =
      '';

    if (!preserveSuccess) {
      this.success =
        '';
    }


    this.cancelStatusChange();

    this.closeReschedule();


    this.appointmentService
      .getByDoctorAndDate(
        this.doctor.id,
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

            this.error =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotLoadDoctorAppointments'
                )
              );

            this.loadingAppointments =
              false;

            this.cdr
              .detectChanges();
          },
      });
  }


  /*
   * ==========================================
   * REGLAS DE ESTADO
   * ==========================================
   */


  canConfirm(
    appointment:
      PatientAppointment
  ): boolean {

    return (
      appointment.status ===
      'PROGRAMADA'
    );
  }


  canMarkAsAttended(
    appointment:
      PatientAppointment
  ): boolean {

    return (
      appointment.status ===
      'CONFIRMADA'
    );
  }


  canCancel(
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


  hasActions(
    appointment:
      PatientAppointment
  ): boolean {

    return (
      this.canConfirm(
        appointment
      ) ||
      this.canMarkAsAttended(
        appointment
      ) ||
      this.canCancel(
        appointment
      ) ||
      this.canReschedule(
        appointment
      )
    );
  }


  /*
   * ==========================================
   * CAMBIO DE ESTADO
   * ==========================================
   */


  startStatusChange(
    appointment:
      PatientAppointment,

    status:
      'CONFIRMADA' |
      'ATENDIDA' |
      'CANCELADA'
  ): void {

    this.closeReschedule();

    this.statusActionAppointment =
      appointment;

    this.targetStatus =
      status;

    this.statusNotes =
      '';

    this.statusError =
      '';

    this.success =
      '';


    setTimeout(
      () => {

        this.scrollToSection(
          'status-panel'
        );
      },
      50
    );
  }


  cancelStatusChange():
    void {

    this.statusActionAppointment =
      null;

    this.targetStatus =
      null;

    this.statusNotes =
      '';

    this.statusError =
      '';

    this.updatingStatus =
      false;
  }


  confirmStatusChange():
    void {

    if (
      !this.statusActionAppointment ||
      !this.targetStatus
    ) {
      return;
    }


    const notes =
      this.statusNotes
        .trim();


    if (
      this.targetStatus ===
        'CANCELADA' &&
      !notes
    ) {

      this.statusError =
        this.i18n.t(
          'cancellationReasonRequired'
        );

      return;
    }


    const finalNotes =
      notes ||
      this.defaultObservationFor(
        this.targetStatus
      );


    this.updatingStatus =
      true;

    this.statusError =
      '';

    this.success =
      '';


    const appointmentId =
      this.statusActionAppointment
        .id;


    this.appointmentService
      .changeStatus(
        appointmentId,
        {
          status:
            this.targetStatus,

          notes:
            finalNotes,
        }
      )
      .subscribe({

        next:
          () => {

            this.updatingStatus =
              false;

            this.success =
              this.i18n.t(
                'statusUpdatedSuccessfully'
              );

            this.cancelStatusChange();

            this.loadAppointments(
              true
            );

            this.cdr
              .detectChanges();
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.updatingStatus =
              false;

            this.statusError =
              this.extractErrorMessage(
                error,
                this.i18n.t(
                  'couldNotUpdateAppointmentStatus'
                )
              );

            this.cdr
              .detectChanges();
          },
      });
  }


  getStatusActionTitle():
    string {

    switch (
      this.targetStatus
    ) {

      case 'CONFIRMADA':

        return this.i18n.t(
          'confirmScheduledAppointment'
        );


      case 'ATENDIDA':

        return this.i18n.t(
          'markAsAttended'
        );


      case 'CANCELADA':

        return this.i18n.t(
          'cancelAppointment'
        );


      default:

        return '';
    }
  }


  getStatusActionDescription():
    string {

    switch (
      this.targetStatus
    ) {

      case 'CONFIRMADA':

        return this.text(
          'Confirma que la cita queda lista para ser atendida.',
          'Confirm that the appointment is ready to be attended.'
        );


      case 'ATENDIDA':

        return this.text(
          'Registra que la atención médica fue realizada.',
          'Record that the medical appointment was completed.'
        );


      case 'CANCELADA':

        return this.text(
          'Indica el motivo por el cual se cancela esta cita.',
          'Enter the reason why this appointment is being cancelled.'
        );


      default:

        return '';
    }
  }


  private defaultObservationFor(
    status:
      'CONFIRMADA' |
      'ATENDIDA' |
      'CANCELADA'
  ): string {

    switch (status) {

      case 'CONFIRMADA':

        return this.i18n.t(
          'confirmationObservationDefault'
        );


      case 'ATENDIDA':

        return this.i18n.t(
          'attendedObservationDefault'
        );


      case 'CANCELADA':

        return this.i18n.t(
          'doctorCancellationDefault'
        );
    }
  }


  /*
   * ==========================================
   * REAGENDAMIENTO
   * ==========================================
   */


  rescheduleAppointment:
    PatientAppointment | null =
    null;

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

  processingReschedule =
    false;

  rescheduleError =
    '';


  canReschedule(
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


  startReschedule(
    appointment:
      PatientAppointment
  ): void {

    this.cancelStatusChange();

    this.rescheduleAppointment =
      appointment;

    this.rescheduleDate =
      appointment.date;

    this.rescheduleReason =
      '';

    this.rescheduleAvailability =
      null;

    this.selectedRescheduleTime =
      '';

    this.rescheduleError =
      '';

    this.success =
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


  closeReschedule():
    void {

    this.rescheduleAppointment =
      null;

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

    this.processingReschedule =
      false;

    this.rescheduleError =
      '';
  }


  checkRescheduleAvailability():
    void {

    if (
      !this.rescheduleAppointment
    ) {
      return;
    }


    if (
      !this.rescheduleDate
    ) {

      this.rescheduleError =
        this.text(
          'Selecciona la nueva fecha.',
          'Select the new date.'
        );

      return;
    }


    this.rescheduleError =
      '';

    this.selectedRescheduleTime =
      '';

    this.rescheduleAvailability =
      null;

    this.loadingRescheduleAvailability =
      true;


    this.availabilityService
      .getByDoctorAndDate(
        this.rescheduleAppointment
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

            this.rescheduleError =
              this.extractErrorMessage(
                error,
                this.text(
                  'No se pudo consultar la disponibilidad.',
                  'Could not load availability.'
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
      !this.rescheduleAppointment
    ) {
      return;
    }


    if (
      !this.rescheduleDate ||
      !this.selectedRescheduleTime
    ) {

      this.rescheduleError =
        this.text(
          'Selecciona la nueva fecha y una franja disponible.',
          'Select the new date and an available time slot.'
        );

      return;
    }


    this.processingReschedule =
      true;

    this.rescheduleError =
      '';


    this.appointmentService
      .reschedule(
        this.rescheduleAppointment
          .id,
        {
          newDate:
            this.rescheduleDate,

          newTime:
            this.selectedRescheduleTime,

          // El backend lo sobrescribe con el médico autenticado.
          responsible:
            this.doctor?.fullName ??
            '',

          reason:
            this.rescheduleReason
              .trim() ||
            this.text(
              'Re-agendada por el médico',
              'Rescheduled by the doctor'
            ),
        }
      )
      .subscribe({

        next:
          () => {

            this.closeReschedule();

            this.success =
              this.text(
                'Cita re-agendada correctamente.',
                'Appointment rescheduled successfully.'
              );

            this.loadAppointments(
              true
            );

            this.cdr
              .detectChanges();
          },


        error:
          (error) => {

            console.error(
              error
            );

            this.processingReschedule =
              false;

            this.rescheduleError =
              this.extractErrorMessage(
                error,
                this.text(
                  'No se pudo re-agendar la cita.',
                  'Could not reschedule the appointment.'
                )
              );

            this.cdr
              .detectChanges();
          },
      });
  }


  /*
   * ==========================================
   * AUXILIARES
   * ==========================================
   */

  getObservationEntries(
  notes:
    string | null | undefined
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