import { Component, DestroyRef, EventEmitter, Input, Output, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Subscription } from 'rxjs';
import { LanguageService } from '../../../../core/i18n/language';
import { PatientService } from '../../../../core/services/patient';
import { AvailabilityService } from '../../../../core/services/availability';
import { AppointmentService } from '../../../../core/services/appointment';
import { PatientProfile } from '../../../../shared/models/patient';
import { Doctor } from '../../../../shared/models/doctor';
import { Availability } from '../../../../shared/models/availability';
import { CreatedAppointment } from '../../../../shared/models/appointment';

@Component({
  selector: 'app-scheduler-booking',
  imports: [FormsModule],
  templateUrl: './scheduler-booking.html',
  styleUrl: './scheduler-booking.scss',
})
export class SchedulerBooking {
  @Input() doctors: Doctor[] = [];
  @Output() readonly appointmentCreated = new EventEmitter<{
    appointment: CreatedAppointment;
    doctorId: number;
  }>();
  readonly i18n = inject(LanguageService);
  private readonly patients = inject(PatientService);
  private readonly availabilityService = inject(AvailabilityService);
  private readonly appointments = inject(AppointmentService);
  private readonly destroyRef = inject(DestroyRef);
  private patientRequest?: Subscription;
  private availabilityRequest?: Subscription;
  private queriedDoctorId: number | null = null;
  private queriedDate = '';

  documentNumber = '';
  doctorId: number | null = null;
  date = '';
  time = '';
  notes = '';
  readonly today = this.localToday();
  readonly patient = signal<PatientProfile | null>(null);
  readonly availability = signal<Availability | null>(null);
  readonly searchingPatient = signal(false);
  readonly searchingSlots = signal(false);
  readonly booking = signal(false);
  readonly patientError = signal('');
  readonly error = signal('');
  readonly created = signal<CreatedAppointment | null>(null);

  text(es: string, en: string): string {
    return this.i18n.language() === 'es' ? es : en;
  }

  resetPatient(): void {
    this.patientRequest?.unsubscribe();
    this.searchingPatient.set(false);
    this.patient.set(null);
    this.patientError.set('');
    this.error.set('');
  }

  findPatient(): void {
    if (this.booking()) return;
    this.resetPatient();
    const document = this.documentNumber.trim();
    if (!document) {
      this.patientError.set(
        this.text('Ingresa el documento del paciente.', 'Enter the patient document number.'),
      );
      return;
    }
    this.searchingPatient.set(true);
    this.patientRequest = this.patients
      .getByDocumentNumber(document)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (patient) => {
          this.patient.set(patient);
          this.searchingPatient.set(false);
        },
        error: (error) => {
          this.searchingPatient.set(false);
          this.patientError.set(
            error.status === 404
              ? this.text(
                  'No encontramos ese paciente. Debe estar registrado antes de agendar su cita.',
                  'Patient not found. Register the patient before booking.',
                )
              : this.message(
                  error,
                  this.text(
                    'No se pudo buscar el paciente. Intenta nuevamente.',
                    'Could not find the patient. Please retry.',
                  ),
                ),
          );
        },
      });
  }

  resetSlots(): void {
    this.availabilityRequest?.unsubscribe();
    this.searchingSlots.set(false);
    this.availability.set(null);
    this.queriedDoctorId = null;
    this.queriedDate = '';
    this.time = '';
    this.error.set('');
  }

  checkSlots(): void {
    if (this.booking()) return;
    this.resetSlots();
    if (!this.doctorId || !this.date || this.date < this.today) {
      this.error.set(
        this.text(
          'Selecciona un profesional y una fecha desde hoy.',
          'Select a professional and a date from today onward.',
        ),
      );
      return;
    }
    const doctorId = this.doctorId;
    const date = this.date;
    this.searchingSlots.set(true);
    this.availabilityRequest = this.availabilityService
      .getByDoctorAndDate(doctorId, date)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (availability) => {
          this.queriedDoctorId = doctorId;
          this.queriedDate = date;
          this.availability.set(availability);
          this.searchingSlots.set(false);
        },
        error: (error) => {
          this.searchingSlots.set(false);
          this.error.set(
            this.message(
              error,
              this.text(
                'No se pudieron consultar los horarios.',
                'Could not load available times.',
              ),
            ),
          );
        },
      });
  }

  get canBook(): boolean {
    return (
      !this.booking() &&
      !this.created() &&
      !this.searchingPatient() &&
      !this.searchingSlots() &&
      !!this.patient() &&
      this.patient()!.documentNumber === this.documentNumber.trim() &&
      this.doctorId === this.queriedDoctorId &&
      this.date === this.queriedDate &&
      this.date >= this.today &&
      !!this.time &&
      !!this.availability()?.availableSlots.includes(this.time)
    );
  }

  book(): void {
    if (!this.canBook) return;
    const patient = this.patient()!;
    const doctorId = this.doctorId!;
    this.booking.set(true);
    this.error.set('');
    this.appointments
      .create({
        documentNumber: patient.documentNumber,
        documentType: patient.documentType,
        firstNames: patient.firstNames,
        lastNames: patient.lastNames,
        phone: patient.phone,
        gender: patient.gender,
        birthDate: patient.birthDate,
        email: patient.email,
        doctorId,
        date: this.date,
        time: this.time,
        notes: this.notes.trim(),
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (appointment) => {
          this.created.set(appointment);
          this.booking.set(false);
          this.resetSlots();
          this.appointmentCreated.emit({ appointment, doctorId });
        },
        error: (error) => {
          this.booking.set(false);
          // Require a fresh availability query after a rejected booking.
          this.resetSlots();
          this.error.set(
            this.message(
              error,
              this.text(
                'No se pudo crear la cita. Consulta de nuevo los horarios antes de reintentar.',
                'Could not create the appointment. Check availability again before retrying.',
              ),
            ),
          );
        },
      });
  }

  newBooking(): void {
    if (this.booking()) return;
    this.resetPatient();
    this.resetSlots();
    this.created.set(null);
    this.documentNumber = '';
    this.notes = '';
    this.doctorId = null;
    this.date = '';
  }

  private message(error: any, fallback: string): string {
    return (
      error?.error?.message ||
      error?.error?.detail ||
      (typeof error?.error === 'string' ? error.error : '') ||
      fallback
    );
  }

  private localToday(): string {
    const date = new Date();
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  }
}