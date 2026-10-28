import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppointmentService } from '../../../core/services/appointment';
import { PatientAppointment } from '../../models/appointment';

@Component({
  selector: 'app-patient-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <section class="history">
      <h2>Historial de citas por cédula</h2>

      <form (ngSubmit)="search()">
        <label for="history-document">Cédula del paciente</label>

        <div class="search">
          <input
            id="history-document"
            name="documentNumber"
            [(ngModel)]="documentNumber"
            [disabled]="loading()"
            placeholder="Ingresa la cédula"
            required
          />

          <button
            type="submit"
            [disabled]="loading() || !documentNumber.trim()"
          >
            {{ loading() ? 'Consultando…' : 'Consultar historial' }}
          </button>
        </div>
      </form>

      @if (error()) {
        <p role="alert">{{ error() }}</p>
      }

      @if (searched() && !loading() && !error()) {
        <p aria-live="polite">
          Cédula consultada: {{ searchedDocument() }}.
          Citas encontradas: {{ appointments().length }}.
        </p>

        @if (appointments().length === 0) {
          <p>No se encontraron citas para esta cédula.</p>
        } @else {
          <div class="table-container">
            <table>
              <caption>Historial del paciente consultado</caption>
              <thead>
                <tr>
                  <th scope="col">Paciente</th>
                  <th scope="col">Profesional</th>
                  <th scope="col">Fecha</th>
                  <th scope="col">Hora</th>
                  <th scope="col">Estado</th>
                </tr>
              </thead>
              <tbody>
                @for (appointment of appointments(); track appointment.id) {
                  <tr>
                    <td>{{ appointment.patientName }}</td>
                    <td>{{ appointment.doctorName }}</td>
                    <td>{{ appointment.date | date:'dd/MM/yyyy' }}</td>
                    <td>{{ appointment.time }}</td>
                    <td>{{ appointment.status }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      }
    </section>
  `,
  styles: [`
    :host { display: block; }
    .history {
      margin: 24px;
      padding: 24px;
      background: white;
      border: 1px solid #cfe1ec;
      border-radius: 16px;
      color: #123b56;
    }
    label { display: block; margin-bottom: 8px; }
    .search { display: flex; gap: 12px; flex-wrap: wrap; }
    input, button {
      padding: 12px;
      border: 1px solid #b8cfdf;
      border-radius: 8px;
      font: inherit;
    }
    input { flex: 1; min-width: 180px; }
    button { background: #08749b; color: white; cursor: pointer; }
    button:disabled { opacity: .6; cursor: default; }
    .table-container { overflow-x: auto; }
    table { width: 100%; border-collapse: collapse; }
    caption { text-align: left; padding: 12px 0; }
    th, td {
      text-align: left;
      padding: 12px;
      border-bottom: 1px solid #dce8ef;
    }
    [role="alert"] { color: #b42318; }
  `],
})
export class PatientHistory {
  private readonly appointmentService = inject(AppointmentService);

  documentNumber = '';
  loading = signal(false);
  searched = signal(false);
  searchedDocument = signal('');
  error = signal('');
  appointments = signal<PatientAppointment[]>([]);

  search(): void {
    const document = this.documentNumber.trim();

    if (!document || this.loading()) {
      return;
    }

    this.loading.set(true);
    this.searched.set(false);
    this.error.set('');
    this.appointments.set([]);
    this.searchedDocument.set(document);

    this.appointmentService
      .getByPatientDocumentNumber(document)
      .subscribe({
        next: (appointments) => {
          this.appointments.set(appointments);
          this.searched.set(true);
          this.loading.set(false);
        },
        error: () => {
          this.error.set(
            'No se pudo consultar el historial. Revisa la conexión y los permisos.'
          );
          this.loading.set(false);
        },
      });
  }
}