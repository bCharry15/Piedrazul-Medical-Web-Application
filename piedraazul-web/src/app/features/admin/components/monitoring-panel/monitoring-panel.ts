import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, forkJoin, map, of } from 'rxjs';
import { environment } from '../../../../../environments/environment';
import { LanguageService } from '../../../../core/i18n/language';

interface Health {
  status: string;
  components?: Record<string, { status: string }>;
}

interface Metric {
  measurements: { statistic: string; value: number }[];
}

@Component({
  selector: 'app-monitoring-panel',
  imports: [CommonModule],
  templateUrl: './monitoring-panel.html',
  styleUrl: './monitoring-panel.scss',
})
export class MonitoringPanel implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly destroyRef = inject(DestroyRef);
  readonly i18n = inject(LanguageService);
  private readonly url = `${environment.apiUrl}/api/monitoring`;

  readonly loading = signal(false);
  readonly health = signal<Health | null>(null);
  readonly uptime = signal<number | null>(null);
  readonly memory = signal<number | null>(null);
  readonly requests = signal<number | null>(null);
  readonly checkedAt = signal<Date | null>(null);
  readonly error = signal('');
  readonly logs = signal('');
  readonly loadingLogs = signal(false);
  readonly logsError = signal('');
  readonly logsCheckedAt = signal<Date | null>(null);

  ngOnInit(): void {
    this.refresh();
  }

  text(es: string, en: string): string {
    return this.i18n.language() === 'es' ? es : en;
  }

  refresh(): void {
    if (this.loading()) return;
    this.loading.set(true);
    this.error.set('');
    this.health.set(null);
    this.uptime.set(null);
    this.memory.set(null);
    this.requests.set(null);
    this.checkedAt.set(null);

    forkJoin({
      health: this.http.get<Health>(`${this.url}/health`).pipe(
        catchError((error: HttpErrorResponse) => {
          // A failed dependency is a valid health result, returned with HTTP 503.
          if (error.status === 503 && error.error?.status) return of(error.error as Health);
          this.error.set(this.message(error));
          return of(null);
        }),
      ),
      uptime: this.metric('process.uptime'),
      memory: this.metric('jvm.memory.used'),
      requests: this.metric('http.server.requests', 'COUNT'),
    }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe((result) => {
      this.health.set(result.health);
      this.uptime.set(result.uptime);
      this.memory.set(result.memory);
      this.requests.set(result.requests);
      this.checkedAt.set(new Date());
      this.loading.set(false);
    });
  }

  loadLogs(): void {
    if (this.loadingLogs()) return;
    this.loadingLogs.set(true);
    this.logsError.set('');
    this.logs.set('');
    this.logsCheckedAt.set(null);
    this.http.get(`${this.url}/logfile`, {
      responseType: 'text',
      headers: new HttpHeaders({ Range: 'bytes=-32768' }),
    }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (text) => {
        // The byte range can start halfway through a UTF-8 character or a line.
        // Drop that first fragment and show a bounded, plain-text tail.
        this.logs.set(text.split(/\r?\n/).slice(1).slice(-100).join('\n'));
        this.logsCheckedAt.set(new Date());
        this.loadingLogs.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.logsError.set(error.status === 404 || error.status === 416
          ? this.text('El archivo de logs todavía no está disponible.', 'The log file is not available yet.')
          : this.message(error));
        this.loadingLogs.set(false);
      },
    });
  }

  statusLabel(status: string | undefined): string {
    if (status === 'UP') return this.text('Operativo', 'Operational');
    if (status === 'DOWN' || status === 'OUT_OF_SERVICE') return this.text('Con fallas', 'Unhealthy');
    return this.text('Sin información', 'Unknown');
  }

  private metric(name: string, statistic = 'VALUE') {
    return this.http.get<Metric>(`${this.url}/metrics/${name}`).pipe(
      // Individual metrics can be absent before their first measurement.
      catchError(() => of(null)),
      map((metric: Metric | null): number | null =>
        metric?.measurements.find((measurement) => measurement.statistic === statistic)?.value ?? null),
    );
  }

  private message(error: HttpErrorResponse): string {
    if (error.status === 401) return this.text('La sesión expiró. Vuelve a iniciar sesión.', 'Your session expired. Sign in again.');
    if (error.status === 403) return this.text('Solo un administrador puede consultar el monitoreo.', 'Monitoring is restricted to administrators.');
    if (error.status === 404) return this.text('El backend aún no tiene desplegada esta función de monitoreo.', 'Monitoring has not been deployed to the backend yet.');
    return this.text('No se pudo consultar el backend. Revisa la conexión.', 'Could not reach the backend. Check the connection.');
  }
}
