import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { environment } from '../../../../../environments/environment';
import { MonitoringPanel } from './monitoring-panel';

describe('MonitoringPanel', () => {
  const url = `${environment.apiUrl}/api/monitoring`;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MonitoringPanel],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  function flushMetrics() {
    http.expectOne(`${url}/metrics/process.uptime`).flush({ measurements: [{ statistic: 'VALUE', value: 3600 }] });
    http.expectOne(`${url}/metrics/jvm.memory.used`).flush({ measurements: [{ statistic: 'VALUE', value: 1048576 }] });
    http.expectOne(`${url}/metrics/http.server.requests`).flush({}, { status: 404, statusText: 'Not Found' });
  }

  it('shows a failed database from HTTP 503 and keeps missing metrics unavailable', () => {
    const fixture = TestBed.createComponent(MonitoringPanel);
    fixture.detectChanges();
    http.expectOne(`${url}/health`).flush({ status: 'DOWN', components: { db: { status: 'DOWN' } } },
      { status: 503, statusText: 'Service Unavailable' });
    flushMetrics();
    fixture.detectChanges();
    expect(fixture.componentInstance.health()?.components?.['db'].status).toBe('DOWN');
    expect(fixture.componentInstance.uptime()).toBe(3600);
    expect(fixture.componentInstance.requests()).toBeNull();
    expect(fixture.nativeElement.querySelectorAll('.down').length).toBe(2);
  });

  it('requests only a bounded log tail and renders HTML as plain text', () => {
    const fixture = TestBed.createComponent(MonitoringPanel);
    fixture.componentInstance.loadLogs();
    const request = http.expectOne(`${url}/logfile`);
    expect(request.request.headers.get('Range')).toBe('bytes=-32768');
    request.flush('partial line\n<script>example</script>\n', { status: 206, statusText: 'Partial Content' });
    fixture.detectChanges();
    // First detection starts the independent health query.
    http.expectOne(`${url}/health`).flush({ status: 'UP' });
    flushMetrics();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('pre').textContent).toContain('<script>example</script>');
    expect(fixture.nativeElement.querySelector('pre script')).toBeNull();
  });

  it('shows an access error instead of a false healthy result', () => {
    const fixture = TestBed.createComponent(MonitoringPanel);
    fixture.detectChanges();
    http.expectOne(`${url}/health`).flush({}, { status: 403, statusText: 'Forbidden' });
    flushMetrics();
    fixture.detectChanges();
    expect(fixture.componentInstance.health()).toBeNull();
    expect(fixture.nativeElement.querySelector('[role="alert"]')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('.up')).toBeNull();
  });
});
