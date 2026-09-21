import { CommonModule } from '@angular/common';

import {
  ChangeDetectorRef,
  Component,
  OnInit,
  inject,
} from '@angular/core';

import { FormsModule } from '@angular/forms';

import {
  LanguageService,
} from '../../../../core/i18n/language';

import {
  CreateDoctorRequest,
  DoctorService,
  UpdateDoctorRequest,
} from '../../../../core/services/doctor';

import {
  AvailabilityConfigurationService,
} from '../../../../core/services/availability-configuration';

import {
  AvailabilityConfiguration,
  DayOfWeek,
  SaveAvailabilityConfigurationRequest,
} from '../../../../shared/models/availability-configuration';

import {
  Doctor,
} from '../../../../shared/models/doctor';

import {
  CreateSchedulerRequest,
  ManagedUser,
  UserManagementService,
} from '../../../../core/services/user-management';

@Component({
  imports: [
    CommonModule,
    FormsModule,
  ],

  selector: 'app-admin-home',

  styleUrl: './admin-home.scss',

  templateUrl: './admin-home.html',
})
export class AdminHome
  implements OnInit {

  readonly i18n =
    inject(LanguageService);

  private readonly doctorService =
    inject(DoctorService);

  private readonly userManagementService =
    inject(UserManagementService);

  private readonly availabilityConfigurationService =
    inject(AvailabilityConfigurationService);

  private readonly cdr =
    inject(ChangeDetectorRef);

  /*
   * ==========================================================
   * MÉDICOS
   * ==========================================================
   */

  doctors: Doctor[] = [];

  loadingDoctors = false;

  savingDoctor = false;

  deletingDoctorId:
    number | null = null;

  error = '';

  success = '';

  showCreateForm = false;

  editingDoctor:
    Doctor | null = null;

  createForm = {
    fullName: '',
    specialty: '',
    intervalMinutes: 15,
    username: '',
    password: '',
  };

  editForm = {
    fullName: '',
    specialty: '',
    intervalMinutes: 15,
  };

  /*
   * ==========================================================
   * AGENDADORES
   * ==========================================================
   */

  schedulers: ManagedUser[] = [];

  loadingSchedulers = false;

  savingScheduler = false;

  showSchedulerForm = false;

  schedulerError = '';

  schedulerSuccess = '';

  schedulerForm = {
    username: '',
    password: '',
    confirmPassword: '',
  };

  /*
   * ==========================================================
   * DISPONIBILIDAD
   * ==========================================================
   */

  selectedAvailabilityDoctorId:
    number | null = null;

  availabilityConfigurations:
    AvailabilityConfiguration[] = [];

  loadingAvailabilityConfigurations =
    false;

  savingAvailabilityConfiguration =
    false;

  availabilityLoaded = false;

  showAvailabilityForm = false;

  editingAvailability:
    AvailabilityConfiguration | null =
      null;

  availabilityError = '';

  availabilitySuccess = '';

  readonly dayOptions: {
    value: DayOfWeek;
    labelKey: string;
  }[] = [
    {
      value: 'MONDAY',
      labelKey: 'monday',
    },
    {
      value: 'TUESDAY',
      labelKey: 'tuesday',
    },
    {
      value: 'WEDNESDAY',
      labelKey: 'wednesday',
    },
    {
      value: 'THURSDAY',
      labelKey: 'thursday',
    },
    {
      value: 'FRIDAY',
      labelKey: 'friday',
    },
    {
      value: 'SATURDAY',
      labelKey: 'saturday',
    },
    {
      value: 'SUNDAY',
      labelKey: 'sunday',
    },
  ];

  availabilityForm = {
    dayOfWeek:
      'MONDAY' as DayOfWeek,

    startTime:
      '09:00',

    endTime:
      '12:00',

    intervalMinutes:
      15,

    weekWindow:
      4,
  };

  /*
   * ==========================================================
   * INICIALIZACIÓN
   * ==========================================================
   */

  ngOnInit(): void {

    this.loadDoctors();
    this.loadSchedulers();
  }

  /*
   * ==========================================================
   * GESTIÓN DE MÉDICOS
   * ==========================================================
   */

  loadDoctors(): void {

    this.loadingDoctors = true;

    this.error = '';

    this.doctorService
      .getAll()
      .subscribe({

        next: (doctors) => {

          this.doctors =
            doctors;

          this.loadingDoctors =
            false;

          /*
           * Si el médico seleccionado para
           * disponibilidad ya no existe,
           * limpiamos la selección.
           */
          if (
            this.selectedAvailabilityDoctorId !==
              null &&
            !this.doctors.some(
              (doctor) =>
                doctor.id ===
                this.selectedAvailabilityDoctorId
            )
          ) {

            this.resetAvailabilitySection();
          }

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(error);

          this.error =
            this.extractErrorMessage(
              error,
              this.i18n.t(
                'couldNotLoadDoctors'
              )
            );

          this.loadingDoctors =
            false;

          this.cdr.detectChanges();
        },
      });
  }

  openCreateForm(): void {

    this.showSchedulerForm = false;

    this.schedulerError = '';

    this.editingDoctor = null;

    this.showCreateForm = true;

    this.error = '';

    this.success = '';

    this.resetCreateForm();
  }

  closeCreateForm(): void {

    this.showCreateForm = false;

    this.resetCreateForm();
  }

  createDoctor(): void {

    this.error = '';

    this.success = '';

    const fullName =
      this.createForm
        .fullName
        .trim();

    const specialty =
      this.createForm
        .specialty
        .trim();

    const username =
      this.createForm
        .username
        .trim();

    const password =
      this.createForm
        .password;

    const intervalMinutes =
      Number(
        this.createForm
          .intervalMinutes
      );

    if (
      !fullName ||
      !specialty ||
      !username ||
      !password
    ) {

      this.error =
        this.i18n.t(
          'completeRequiredFields'
        );

      return;
    }

    if (
      !Number.isFinite(
        intervalMinutes
      ) ||
      intervalMinutes <= 0
    ) {

      this.error =
        this.i18n.t(
          'invalidAppointmentInterval'
        );

      return;
    }

    const request:
      CreateDoctorRequest = {

      fullName,
      specialty,
      username,
      password,
      intervalMinutes,
    };

    this.savingDoctor = true;

    this.doctorService
      .create(request)
      .subscribe({

        next: () => {

          this.savingDoctor =
            false;

          this.showCreateForm =
            false;

          this.resetCreateForm();

          this.success =
            this.i18n.t(
              'doctorCreatedSuccessfully'
            );

          this.loadDoctors();

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(error);

          this.savingDoctor =
            false;

          this.error =
            this.extractErrorMessage(
              error,
              this.i18n.t(
                'couldNotCreateDoctor'
              )
            );

          this.cdr.detectChanges();
        },
      });
  }

  startEdit(
    doctor: Doctor
  ): void {

    this.showSchedulerForm = false;

    this.schedulerError = '';

    this.showCreateForm = false;

    this.editingDoctor =
      doctor;

    this.editForm = {

      fullName:
        doctor.fullName,

      specialty:
        doctor.specialty,

      intervalMinutes:
        doctor.intervalMinutes,
    };

    this.error = '';

    this.success = '';
  }

  cancelEdit(): void {

    this.editingDoctor =
      null;

    this.error = '';
  }

  updateDoctor(): void {

    if (
      !this.editingDoctor
    ) {
      return;
    }

    this.error = '';

    this.success = '';

    const fullName =
      this.editForm
        .fullName
        .trim();

    const specialty =
      this.editForm
        .specialty
        .trim();

    const intervalMinutes =
      Number(
        this.editForm
          .intervalMinutes
      );

    if (
      !fullName ||
      !specialty
    ) {

      this.error =
        this.i18n.t(
          'completeRequiredFields'
        );

      return;
    }

    if (
      !Number.isFinite(
        intervalMinutes
      ) ||
      intervalMinutes <= 0
    ) {

      this.error =
        this.i18n.t(
          'invalidAppointmentInterval'
        );

      return;
    }

    const request:
      UpdateDoctorRequest = {

      fullName,
      specialty,
      intervalMinutes,
    };

    const doctorId =
      this.editingDoctor.id;

    this.savingDoctor = true;

    this.doctorService
      .update(
        doctorId,
        request
      )
      .subscribe({

        next: () => {

          this.savingDoctor =
            false;

          this.editingDoctor =
            null;

          this.success =
            this.i18n.t(
              'doctorUpdatedSuccessfully'
            );

          this.loadDoctors();

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(error);

          this.savingDoctor =
            false;

          this.error =
            this.extractErrorMessage(
              error,
              this.i18n.t(
                'couldNotUpdateDoctor'
              )
            );

          this.cdr.detectChanges();
        },
      });
  }

  deleteDoctor(
    doctor: Doctor
  ): void {

    const confirmed =
      window.confirm(
        this.i18n.t(
          'deleteDoctorQuestion'
        )
      );

    if (!confirmed) {
      return;
    }

    this.error = '';

    this.success = '';

    this.deletingDoctorId =
      doctor.id;

    this.doctorService
      .delete(
        doctor.id
      )
      .subscribe({

        next: () => {

          this.deletingDoctorId =
            null;

          if (
            this.editingDoctor?.id ===
            doctor.id
          ) {

            this.editingDoctor =
              null;
          }

          if (
            this.selectedAvailabilityDoctorId ===
            doctor.id
          ) {

            this.resetAvailabilitySection();
          }

          this.success =
            this.i18n.t(
              'doctorDeletedSuccessfully'
            );

          this.loadDoctors();

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(error);

          this.deletingDoctorId =
            null;

          this.error =
            this.extractErrorMessage(
              error,
              this.i18n.t(
                'couldNotDeleteDoctor'
              )
            );

          this.cdr.detectChanges();
        },
      });
  }

  doctorStatus(
    doctor: Doctor
  ): string {

    if (
      doctor.active &&
      doctor.userStatus ===
        'ACTIVE'
    ) {

      return this.i18n.t(
        'active'
      );
    }

    return this.i18n.t(
      'inactive'
    );
  }

  /*
   * ==========================================================
   * GESTIÓN DE AGENDADORES
   * ==========================================================
   */

  loadSchedulers(): void {

    this.loadingSchedulers = true;

    this.schedulerError = '';

    this.userManagementService
      .getSchedulers()
      .subscribe({

        next: (schedulers) => {

          this.schedulers = schedulers;

          this.loadingSchedulers = false;

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(error);

          this.schedulerError =
            this.extractErrorMessage(
              error,
              'No fue posible cargar los agendadores.'
            );

          this.loadingSchedulers = false;

          this.cdr.detectChanges();
        },
      });
  }

  openSchedulerForm(): void {

    this.showCreateForm = false;

    this.editingDoctor = null;

    this.showSchedulerForm = true;

    this.schedulerError = '';

    this.schedulerSuccess = '';

    this.resetSchedulerForm();
  }

  closeSchedulerForm(): void {

    this.showSchedulerForm = false;

    this.schedulerError = '';

    this.resetSchedulerForm();
  }

  createScheduler(): void {

    this.schedulerError = '';

    this.schedulerSuccess = '';

    const username =
      this.schedulerForm
        .username
        .trim();

    const password =
      this.schedulerForm.password;

    const confirmPassword =
      this.schedulerForm.confirmPassword;

    if (
      !username ||
      !password ||
      !confirmPassword
    ) {

      this.schedulerError =
        this.i18n.t(
          'completeRequiredFields'
        );

      return;
    }

    if (
      password.trim().length < 6
    ) {

      this.schedulerError =
        'La contraseña debe tener mínimo 6 caracteres.';

      return;
    }

    if (
      password !== confirmPassword
    ) {

      this.schedulerError =
        'Las contraseñas no coinciden.';

      return;
    }

    const request:
      CreateSchedulerRequest = {

      username,
      password,
    };

    this.savingScheduler = true;

    this.userManagementService
      .createScheduler(request)
      .subscribe({

        next: (response) => {

          this.savingScheduler = false;

          this.showSchedulerForm = false;

          this.resetSchedulerForm();

          this.schedulerSuccess =
            response.mensaje ||
            'Agendador registrado correctamente.';

          this.loadSchedulers();

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(error);

          this.savingScheduler = false;

          this.schedulerError =
            this.extractErrorMessage(
              error,
              'No fue posible registrar el agendador.'
            );

          this.cdr.detectChanges();
        },
      });
  }

  schedulerStatus(
    scheduler: ManagedUser
  ): string {

    return scheduler.status === 'ACTIVE'
      ? this.i18n.t('active')
      : this.i18n.t('inactive');
  }

  /*
   * ==========================================================
   * GESTIÓN DE DISPONIBILIDAD
   * ==========================================================
   */

  onAvailabilityDoctorChange():
    void {

    this.availabilityConfigurations =
      [];

    this.availabilityLoaded =
      false;

    this.availabilityError =
      '';

    this.availabilitySuccess =
      '';

    this.showAvailabilityForm =
      false;

    this.editingAvailability =
      null;
  }

  loadAvailabilityConfigurations():
    void {

    if (
      this.selectedAvailabilityDoctorId ===
      null
    ) {

      this.availabilityError =
        this.i18n.t(
          'selectDoctorFirst'
        );

      return;
    }

    this.loadingAvailabilityConfigurations =
      true;

    this.availabilityError =
      '';

    this.availabilitySuccess =
      '';

    this.availabilityConfigurationService
      .getByDoctor(
        this.selectedAvailabilityDoctorId
      )
      .subscribe({

        next: (configurations) => {

          this.availabilityConfigurations =
            this.sortAvailabilityConfigurations(
              configurations
            );

          this.availabilityLoaded =
            true;

          this.loadingAvailabilityConfigurations =
            false;

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(error);

          this.loadingAvailabilityConfigurations =
            false;

          this.availabilityLoaded =
            true;

          this.availabilityError =
            this.extractErrorMessage(
              error,
              this.i18n.t(
                'couldNotLoadAvailabilityConfigurations'
              )
            );

          this.cdr.detectChanges();
        },
      });
  }

  openAvailabilityCreateForm():
    void {

    if (
      this.selectedAvailabilityDoctorId ===
      null
    ) {

      this.availabilityError =
        this.i18n.t(
          'selectDoctorFirst'
        );

      return;
    }

    this.editingAvailability =
      null;

    this.showAvailabilityForm =
      true;

    this.availabilityError =
      '';

    this.availabilitySuccess =
      '';

    const doctor =
      this.selectedAvailabilityDoctor();

    this.availabilityForm = {

      dayOfWeek:
        'MONDAY',

      startTime:
        '09:00',

      endTime:
        '12:00',

      intervalMinutes:
        doctor?.intervalMinutes ??
        15,

      weekWindow:
        4,
    };
  }

  startAvailabilityEdit(
    configuration:
      AvailabilityConfiguration
  ): void {

    this.editingAvailability =
      configuration;

    this.showAvailabilityForm =
      true;

    this.availabilityError =
      '';

    this.availabilitySuccess =
      '';

    this.availabilityForm = {

      dayOfWeek:
        configuration.dayOfWeek,

      startTime:
        this.toTimeInputValue(
          configuration.startTime
        ),

      endTime:
        this.toTimeInputValue(
          configuration.endTime
        ),

      intervalMinutes:
        configuration.intervalMinutes,

      weekWindow:
        configuration.weekWindow,
    };
  }

  closeAvailabilityForm():
    void {

    this.showAvailabilityForm =
      false;

    this.editingAvailability =
      null;

    this.availabilityError =
      '';
  }

  saveAvailabilityConfiguration():
    void {

    if (
      this.selectedAvailabilityDoctorId ===
      null
    ) {

      this.availabilityError =
        this.i18n.t(
          'selectDoctorFirst'
        );

      return;
    }

    this.availabilityError =
      '';

    this.availabilitySuccess =
      '';

    const startTime =
      this.availabilityForm
        .startTime;

    const endTime =
      this.availabilityForm
        .endTime;

    const intervalMinutes =
      Number(
        this.availabilityForm
          .intervalMinutes
      );

    const weekWindow =
      Number(
        this.availabilityForm
          .weekWindow
      );

    if (
      !this.availabilityForm
        .dayOfWeek ||
      !startTime ||
      !endTime
    ) {

      this.availabilityError =
        this.i18n.t(
          'completeRequiredFields'
        );

      return;
    }

    if (
      startTime >= endTime
    ) {

      this.availabilityError =
        this.i18n.t(
          'invalidAvailabilityTimeRange'
        );

      return;
    }

    if (
      !Number.isFinite(
        intervalMinutes
      ) ||
      intervalMinutes <= 0
    ) {

      this.availabilityError =
        this.i18n.t(
          'invalidAppointmentInterval'
        );

      return;
    }

    if (
      !Number.isFinite(
        weekWindow
      ) ||
      weekWindow <= 0
    ) {

      this.availabilityError =
        this.i18n.t(
          'invalidWeekWindow'
        );

      return;
    }

    const request:
      SaveAvailabilityConfigurationRequest = {

      doctorId:
        this.selectedAvailabilityDoctorId,

      dayOfWeek:
        this.availabilityForm
          .dayOfWeek,

      startTime,

      endTime,

      intervalMinutes,

      weekWindow,
    };

    this.savingAvailabilityConfiguration =
      true;

    if (
      this.editingAvailability
    ) {

      this.updateAvailabilityConfiguration(
        this.editingAvailability.id,
        request
      );

      return;
    }

    this.createAvailabilityConfiguration(
      request
    );
  }

  selectedAvailabilityDoctor():
    Doctor | undefined {

    if (
      this.selectedAvailabilityDoctorId ===
      null
    ) {
      return undefined;
    }

    return this.doctors.find(
      (doctor) =>
        doctor.id ===
        this.selectedAvailabilityDoctorId
    );
  }

  availabilityStatus(
    configuration:
      AvailabilityConfiguration
  ): string {

    return configuration.active
      ? this.i18n.t('active')
      : this.i18n.t('inactive');
  }

  dayLabel(
    day: DayOfWeek
  ): string {

    const option =
      this.dayOptions.find(
        (current) =>
          current.value === day
      );

    if (!option) {
      return day;
    }

    return this.i18n.t(
      option.labelKey
    );
  }

  /*
   * ==========================================================
   * MÉTODOS PRIVADOS
   * ==========================================================
   */

  private createAvailabilityConfiguration(
    request:
      SaveAvailabilityConfigurationRequest
  ): void {

    this.availabilityConfigurationService
      .create(request)
      .subscribe({

        next: () => {

          this.savingAvailabilityConfiguration =
            false;

          this.showAvailabilityForm =
            false;

          this.editingAvailability =
            null;

          this.availabilitySuccess =
            this.i18n.t(
              'availabilityCreatedSuccessfully'
            );

          this.loadAvailabilityConfigurations();

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(error);

          this.savingAvailabilityConfiguration =
            false;

          this.availabilityError =
            this.extractErrorMessage(
              error,
              this.i18n.t(
                'couldNotCreateAvailability'
              )
            );

          this.cdr.detectChanges();
        },
      });
  }

  private updateAvailabilityConfiguration(
    availabilityId: number,
    request:
      SaveAvailabilityConfigurationRequest
  ): void {

    this.availabilityConfigurationService
      .update(
        availabilityId,
        request
      )
      .subscribe({

        next: () => {

          this.savingAvailabilityConfiguration =
            false;

          this.showAvailabilityForm =
            false;

          this.editingAvailability =
            null;

          this.availabilitySuccess =
            this.i18n.t(
              'availabilityUpdatedSuccessfully'
            );

          this.loadAvailabilityConfigurations();

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(error);

          this.savingAvailabilityConfiguration =
            false;

          this.availabilityError =
            this.extractErrorMessage(
              error,
              this.i18n.t(
                'couldNotUpdateAvailability'
              )
            );

          this.cdr.detectChanges();
        },
      });
  }

  private sortAvailabilityConfigurations(
    configurations:
      AvailabilityConfiguration[]
  ): AvailabilityConfiguration[] {

    const order:
      DayOfWeek[] = [
        'MONDAY',
        'TUESDAY',
        'WEDNESDAY',
        'THURSDAY',
        'FRIDAY',
        'SATURDAY',
        'SUNDAY',
      ];

    return [
      ...configurations,
    ].sort(
      (first, second) =>
        order.indexOf(
          first.dayOfWeek
        ) -
        order.indexOf(
          second.dayOfWeek
        )
    );
  }

  private toTimeInputValue(
    value: string
  ): string {

    if (!value) {
      return '';
    }

    return value.substring(
      0,
      5
    );
  }

  private resetAvailabilitySection():
    void {

    this.selectedAvailabilityDoctorId =
      null;

    this.availabilityConfigurations =
      [];

    this.availabilityLoaded =
      false;

    this.showAvailabilityForm =
      false;

    this.editingAvailability =
      null;

    this.availabilityError =
      '';

    this.availabilitySuccess =
      '';
  }

  private resetCreateForm():
    void {

    this.createForm = {
      fullName: '',
      specialty: '',
      intervalMinutes: 15,
      username: '',
      password: '',
    };
  }

  private resetSchedulerForm():
    void {

    this.schedulerForm = {
      username: '',
      password: '',
      confirmPassword: '',
    };
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
}