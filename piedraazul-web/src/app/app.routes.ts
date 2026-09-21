import {
  Routes,
} from '@angular/router';

import {
  homeGuard,
  roleGuard,
} from './core/guards/role.guard';


export const routes:
  Routes = [

  {
    path: '',
    pathMatch: 'full',

    canActivate: [
      homeGuard,
    ],

    loadComponent: () =>
      import(
        './shared/components/login-home/login-home'
      ).then(
        (m) =>
          m.LoginHome
      ),
  },


  /*
   * Registro público de pacientes.
   */
  {
    path:
      'register',

    loadComponent: () =>
      import(
        './shared/components/register/register'
      ).then(
        (m) =>
          m.Register
      ),
  },


  {
    path:
      'admin',

    canActivate: [
      roleGuard(
        'ADMIN'
      ),
    ],

    loadComponent: () =>
      import(
        './features/admin/pages/admin-home/admin-home'
      ).then(
        (m) =>
          m.AdminHome
      ),
  },


  {
    path:
      'scheduler',

    canActivate: [
      roleGuard(
        'SCHEDULER'
      ),
    ],

    loadComponent: () =>
      import(
        './features/scheduler/pages/scheduler-home/scheduler-home'
      ).then(
        (m) =>
          m.SchedulerHome
      ),
  },


  {
    path:
      'doctor',

    canActivate: [
      roleGuard(
        'DOCTOR'
      ),
    ],

    loadComponent: () =>
      import(
        './features/doctor/pages/doctor-home/doctor-home'
      ).then(
        (m) =>
          m.DoctorHome
      ),
  },


  {
    path:
      'patient',

    canActivate: [
      roleGuard(
        'PATIENT'
      ),
    ],

    loadComponent: () =>
      import(
        './features/patient/pages/patient-home/patient-home'
      ).then(
        (m) =>
          m.PatientHome
      ),
  },


  {
    path:
      'unauthorized',

    loadComponent: () =>
      import(
        './shared/components/unauthorized/unauthorized'
      ).then(
        (m) =>
          m.Unauthorized
      ),
  },


  {
    path:
      '**',

    loadComponent: () =>
      import(
        './shared/components/not-found/not-found'
      ).then(
        (m) =>
          m.NotFound
      ),
  },
];