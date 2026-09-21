import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

export interface ManagedUser {
  id: number;
  username: string;
  role: string;
  status: string;
}

export interface CreateSchedulerRequest {
  username: string;
  password: string;
}

export interface CreateSchedulerResponse {
  mensaje: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserManagementService {

  private readonly http =
    inject(HttpClient);

  private readonly apiUrl =
    'http://localhost:8081/api/auth';

  getSchedulers():
    Observable<ManagedUser[]> {

    return this.http.get<ManagedUser[]>(
      `${this.apiUrl}/users/role/SCHEDULER`
    );
  }

  createScheduler(
    request: CreateSchedulerRequest
  ): Observable<CreateSchedulerResponse> {

    return this.http.post<CreateSchedulerResponse>(
      `${this.apiUrl}/admin/schedulers`,
      request
    );
  }
}