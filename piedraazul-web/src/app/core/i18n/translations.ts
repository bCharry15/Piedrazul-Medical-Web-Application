export type LanguageCode =
  | 'es'
  | 'en';

export type TranslationDictionary =
  Record<string, string>;

export const translations:
  Record<
    LanguageCode,
    TranslationDictionary
  > = {

  /*
   * ==========================================================
   * ESPAÑOL
   * ==========================================================
   */

  es: {

    /*
     * GENERAL
     */

    appName:
      'PiedraAzul',

    language:
      'Idioma',

    spanish:
      'Español',

    english:
      'English',

    id:
      'ID',

    name:
      'Nombre',

    document:
      'Documento',

    email:
      'Correo',

    date:
      'Fecha',

    time:
      'Hora',

    status:
      'Estado',

    notes:
      'Observaciones',

    actions:
      'Acciones',

    doctor:
      'Médico',

    patient:
      'Paciente',

    patientName:
      'Paciente',

    specialty:
      'Especialidad',

    username:
      'Usuario',

    password:
      'Contraseña',

    minutes:
      'minutos',

    weeks:
      'semanas',

    at:
      'a las',

    refresh:
      'Actualizar',

    refreshing:
      'Actualizando...',

    loading:
      'Cargando...',

    edit:
      'Editar',

    delete:
      'Eliminar',

    deleting:
      'Eliminando...',

    close:
      'Cerrar',

    cancel:
      'Cancelar',

    save:
      'Guardar',

    saveChanges:
      'Guardar cambios',

    savingChanges:
      'Guardando cambios...',

    active:
      'Activo',

    inactive:
      'Inactivo',

    noNotes:
      'Sin observaciones',

    noAvailable:
      'No disponible',

    /*
     * ESTADOS DE CITAS
     */

    scheduled:
      'PROGRAMADA',

    confirmed:
      'CONFIRMADA',

    attended:
      'ATENDIDA',

    completed:
      'COMPLETADA',

    cancelled:
      'CANCELADA',

    noShow:
      'NO ASISTIÓ',

    /*
     * PACIENTE
     */

    patientPortal:
      'Portal del paciente',

    patientPortalSubtitle:
      'Agenda y administra tus citas médicas.',

    bookAppointment:
      'Agendar una cita',

    doctorOrTherapist:
      'Médico o terapeuta',

    selectDoctor:
      'Seleccione un médico',

    checkAvailability:
      'Consultar disponibilidad',

    checkingAvailability:
      'Consultando disponibilidad...',

    availableSchedules:
      'Horarios disponibles',

    availableSlots:
      'Horarios disponibles',

    noAvailableSlots:
      'No hay horarios disponibles.',

    noAvailableTimes:
      'No hay horarios disponibles para la fecha seleccionada.',

    appointmentInterval:
      'Intervalo entre citas',

    createAppointment:
      'Crear cita',

    creatingAppointment:
      'Creando cita...',

    confirmAppointment:
      'Confirmar cita',

    cancelAppointment:
      'Cancelar cita',

    reschedule:
      'Cambiar fecha u hora',

    rescheduleAppointment:
      'Reprogramar cita',

    myAppointments:
      'Mis citas',

    loadingAppointments:
      'Cargando citas...',

    noAppointments:
      'Aún no tienes citas registradas.',

    noAppointmentsForDate:
      'No tienes citas registradas para esta fecha.',

    appointmentCreated:
      'Cita creada',

    appointmentCreatedSuccessfully:
      'La cita fue creada correctamente.',

    appointmentConfirmedSuccessfully:
      'La cita fue confirmada correctamente.',

    appointmentAttendedSuccessfully:
      'La cita fue marcada como atendida correctamente.',

    appointmentCancelledSuccessfully:
      'La cita fue cancelada correctamente.',

    appointmentRescheduledSuccessfully:
      'La cita fue reprogramada correctamente.',

    cancellationSuccess:
      'La cita fue cancelada correctamente.',

    rescheduleSuccess:
      'La cita fue reprogramada correctamente.',

    cancellingAppointment:
      'Cancelando cita...',

    reschedulingAppointment:
      'Reprogramando cita...',

    confirmAppointmentQuestion:
      '¿Deseas confirmar esta cita?',

    cancelAppointmentQuestion:
      '¿Deseas cancelar esta cita?',

    rescheduleAppointmentQuestion:
      '¿Deseas reprogramar esta cita?',

    optionalReason:
      'Motivo opcional',

    reason:
      'Motivo',

    newDate:
      'Nueva fecha',

    newTime:
      'Nueva hora',

    pleaseSelectDoctor:
      'Debe seleccionar un médico.',

    pleaseSelectDate:
      'Debe seleccionar una fecha.',

    pleaseSelectTime:
      'Debe seleccionar una hora.',

    pleaseSelectNewDate:
      'Debe seleccionar una nueva fecha.',

    pleaseSelectNewTime:
      'Debe seleccionar una nueva hora.',

    rescheduleRequestedByPatient:
      'Reprogramación solicitada por el paciente',

    couldNotLoadNewAvailability:
      'No se pudo consultar la nueva disponibilidad.',

    couldNotIdentifyPatient:
      'No se pudo identificar al paciente autenticado.',

    couldNotLoadPatient:
      'No se pudo cargar la información del paciente.',

    couldNotLoadAppointments:
      'No se pudieron cargar las citas.',

    couldNotCreateAppointment:
      'No se pudo crear la cita.',

    couldNotConfirmAppointment:
      'No se pudo confirmar la cita.',

    couldNotCancelAppointment:
      'No se pudo cancelar la cita.',

    couldNotRescheduleAppointment:
      'No se pudo reprogramar la cita.',

    patientProfileUnavailable:
      'La información del paciente no está disponible.',

    noActiveDoctors:
      'No hay médicos activos disponibles.',

    patientInformation:
      'Paciente',

    appointmentDetails:
      'Detalles de la cita',

    /*
     * MÉDICO
     */

    doctorPortal:
      'Portal del médico',

    doctorPortalSubtitle:
      'Consulta y administra tus citas médicas.',

    doctorInformation:
      'Información del médico',

    appointmentDuration:
      'Duración de las citas',

    appointmentDurationMinutes:
      'Duración de la cita (minutos)',

    appointmentsOfDay:
      'Citas del día',

    consultAppointments:
      'Consultar citas',

    consultingAppointments:
      'Consultando citas...',

    markAsAttended:
      'Marcar como atendida',

    markAttended:
      'Marcar como atendida',

    changeStatus:
      'Cambiar estado',

    changeAppointmentStatus:
      'Cambiar estado de la cita',

    currentStatus:
      'Estado actual',

    newStatus:
      'Nuevo estado',

          appointmentsForDate:
      'Citas del día',

    confirmScheduledAppointment:
      'Confirmar cita',

    notAvailable:
      'No disponible',

    observationPlaceholder:
      'Escriba una observación',

    observation:
      'Observación',

    confirmStatusChange:
      'Confirmar cambio',

    updatingStatus:
      'Actualizando estado...',

    statusUpdatedSuccessfully:
      'El estado de la cita fue actualizado correctamente.',

    couldNotUpdateAppointmentStatus:
      'No se pudo actualizar el estado de la cita.',

    couldNotLoadDoctorProfile:
      'No se pudo cargar la información del médico.',

    doctorProfileNotFound:
      'No se encontró un perfil de médico asociado al usuario autenticado.',

    couldNotLoadDoctorAppointments:
      'No se pudieron cargar las citas del médico.',

    /*
     * SCHEDULER / AGENDADOR
     */

    schedulerPortal:
      'Portal del agendador',

    schedulerPortalSubtitle:
      'Consulta y administra la agenda médica.',

    consultSchedule:
      'Consultar agenda',

    scheduleResults:
      'Agenda del médico',

    couldNotLoadSchedulerAppointments:
      'No se pudieron cargar las citas de la agenda.',

    exportScheduleCsv:
      'Exportar agenda CSV',

    exportingSchedule:
      'Exportando agenda...',

    couldNotExportSchedule:
      'No se pudo exportar la agenda.',

    changeDateOrTime:
      'Cambiar fecha u hora',

    schedulerRescheduleReason:
      'Cambio solicitado por agenda',

    schedulerCancellationReason:
      'Paciente solicita cancelar la cita.',

    /*
     * ADMINISTRADOR
     */

    adminPortal:
      'Portal del administrador',

    adminPortalSubtitle:
      'Administra médicos y configuraciones del sistema.',

    doctorManagement:
      'Gestión de médicos',

    doctorManagementDescription:
      'Registra, consulta, modifica o elimina médicos.',

    registerDoctor:
      'Registrar médico',

    registerDoctorDescription:
      'Registra un médico y crea su usuario de acceso al sistema.',

    createDoctor:
      'Crear médico',

    creatingDoctor:
      'Creando médico...',

    editDoctor:
      'Editar médico',

    noDoctorsRegistered:
      'No hay médicos registrados.',

    loadingDoctors:
      'Cargando médicos...',

    doctorCredentialsNotEditable:
      'El usuario y la contraseña no se modifican desde esta edición.',

    completeRequiredFields:
      'Complete todos los campos obligatorios.',

    invalidAppointmentInterval:
      'El intervalo de atención debe ser mayor que cero.',

    doctorCreatedSuccessfully:
      'El médico fue creado correctamente.',

    doctorUpdatedSuccessfully:
      'El médico fue actualizado correctamente.',

    doctorDeletedSuccessfully:
      'El médico fue eliminado correctamente.',

    couldNotLoadDoctors:
      'No se pudieron cargar los médicos.',

    couldNotCreateDoctor:
      'No se pudo crear el médico.',

    couldNotUpdateDoctor:
      'No se pudo actualizar el médico.',

    couldNotDeleteDoctor:
      'No se pudo eliminar el médico.',

    deleteDoctorQuestion:
      '¿Deseas eliminar este médico?',

    /*
     * CONFIGURACIÓN DE DISPONIBILIDAD
     */

    availabilityManagement:
      'Configuración de disponibilidad',

    availabilityManagementDescription:
      'Configura los días, horarios, intervalos y ventana de agendamiento de cada médico.',

    consultAvailabilityConfiguration:
      'Consultar configuración',

    loadingAvailabilityConfigurations:
      'Cargando configuraciones de disponibilidad...',

    addAvailability:
      'Agregar horario',

    editAvailability:
      'Editar horario',

    saveAvailability:
      'Guardar configuración',

    selectDoctorFirst:
      'Debe seleccionar un médico primero.',

    selectDoctorToConsultAvailability:
      'Seleccione un médico para consultar su configuración de disponibilidad.',

    noAvailabilityConfigured:
      'El médico no tiene horarios de disponibilidad configurados.',

    dayOfWeek:
      'Día de la semana',

    schedule:
      'Horario',

    schedulingWindow:
      'Ventana de agendamiento',

    schedulingWindowWeeks:
      'Ventana de agendamiento (semanas)',

    appointmentIntervalMinutes:
      'Intervalo entre citas (minutos)',

    startTime:
      'Hora inicial',

    endTime:
      'Hora final',

    monday:
      'Lunes',

    tuesday:
      'Martes',

    wednesday:
      'Miércoles',

    thursday:
      'Jueves',

    friday:
      'Viernes',

    saturday:
      'Sábado',

    sunday:
      'Domingo',

    invalidAvailabilityTimeRange:
      'La hora inicial debe ser anterior a la hora final.',

    invalidWeekWindow:
      'La ventana de agendamiento debe ser mayor que cero.',

    availabilityCreatedSuccessfully:
      'El horario de disponibilidad fue creado correctamente.',

    couldNotCreateAvailability:
      'No se pudo crear el horario de disponibilidad.',

    availabilityUpdatedSuccessfully:
      'El horario de disponibilidad fue actualizado correctamente.',

    couldNotUpdateAvailability:
      'No se pudo actualizar el horario de disponibilidad.',

    couldNotLoadAvailabilityConfigurations:
      'No se pudieron cargar las configuraciones de disponibilidad.',

    /*
     * OTROS
     */

    unauthorized:
      'No autorizado',

    unauthorizedMessage:
      'No tienes permisos para acceder a esta sección.',

    notFound:
      'Página no encontrada',

    notFoundMessage:
      'La página solicitada no existe.',

    notRegistered:
      'No registrado',
  },

  /*
   * ==========================================================
   * ENGLISH
   * ==========================================================
   */

  en: {

    /*
     * GENERAL
     */

    appName:
      'PiedraAzul',

    language:
      'Language',

    spanish:
      'Spanish',

    english:
      'English',

    id:
      'ID',

    name:
      'Name',

    document:
      'Document',

    email:
      'Email',

    date:
      'Date',

    time:
      'Time',

    status:
      'Status',

    notes:
      'Notes',

    actions:
      'Actions',

    doctor:
      'Doctor',

    patient:
      'Patient',

    patientName:
      'Patient',

    specialty:
      'Specialty',

    username:
      'Username',

    password:
      'Password',

    minutes:
      'minutes',

    weeks:
      'weeks',

    at:
      'at',

    refresh:
      'Refresh',

    refreshing:
      'Refreshing...',

    loading:
      'Loading...',

    edit:
      'Edit',

    delete:
      'Delete',

    deleting:
      'Deleting...',

    close:
      'Close',

    cancel:
      'Cancel',

    save:
      'Save',

    saveChanges:
      'Save changes',

    savingChanges:
      'Saving changes...',

    active:
      'Active',

    inactive:
      'Inactive',

    noNotes:
      'No notes',

    noAvailable:
      'Not available',

    /*
     * APPOINTMENT STATUSES
     */

    scheduled:
      'SCHEDULED',

    confirmed:
      'CONFIRMED',

    attended:
      'ATTENDED',

    completed:
      'COMPLETED',

    cancelled:
      'CANCELLED',

    noShow:
      'NO SHOW',

    /*
     * PATIENT
     */

    patientPortal:
      'Patient Portal',

    patientPortalSubtitle:
      'Schedule and manage your medical appointments.',

    bookAppointment:
      'Schedule an appointment',

    doctorOrTherapist:
      'Doctor or therapist',

    selectDoctor:
      'Select a doctor',

    checkAvailability:
      'Check availability',

    checkingAvailability:
      'Checking availability...',

    availableSchedules:
      'Available times',

    availableSlots:
      'Available times',

    noAvailableSlots:
      'No available times.',

    noAvailableTimes:
      'There are no available times for the selected date.',

    appointmentInterval:
      'Appointment interval',

    createAppointment:
      'Create appointment',

    creatingAppointment:
      'Creating appointment...',

    confirmAppointment:
      'Confirm appointment',

    cancelAppointment:
      'Cancel appointment',

    reschedule:
      'Change date or time',

    rescheduleAppointment:
      'Reschedule appointment',

    myAppointments:
      'My appointments',

    loadingAppointments:
      'Loading appointments...',

    noAppointments:
      'You do not have any appointments yet.',

    noAppointmentsForDate:
      'You do not have appointments for this date.',

    appointmentCreated:
      'Appointment created',

    appointmentCreatedSuccessfully:
      'The appointment was created successfully.',

    appointmentConfirmedSuccessfully:
      'The appointment was confirmed successfully.',

    appointmentAttendedSuccessfully:
      'The appointment was marked as attended successfully.',

    appointmentCancelledSuccessfully:
      'The appointment was cancelled successfully.',

    appointmentRescheduledSuccessfully:
      'The appointment was rescheduled successfully.',

    cancellationSuccess:
      'The appointment was cancelled successfully.',

    rescheduleSuccess:
      'The appointment was rescheduled successfully.',

    cancellingAppointment:
      'Cancelling appointment...',

    reschedulingAppointment:
      'Rescheduling appointment...',

    confirmAppointmentQuestion:
      'Do you want to confirm this appointment?',

    cancelAppointmentQuestion:
      'Do you want to cancel this appointment?',

    rescheduleAppointmentQuestion:
      'Do you want to reschedule this appointment?',

    optionalReason:
      'Optional reason',

    reason:
      'Reason',

    newDate:
      'New date',

    newTime:
      'New time',

    pleaseSelectDoctor:
      'Please select a doctor.',

    pleaseSelectDate:
      'Please select a date.',

    pleaseSelectTime:
      'Please select a time.',

    pleaseSelectNewDate:
      'Please select a new date.',

    pleaseSelectNewTime:
      'Please select a new time.',

    rescheduleRequestedByPatient:
      'Rescheduling requested by the patient',

    couldNotLoadNewAvailability:
      'Could not load the new availability.',

    couldNotIdentifyPatient:
      'Could not identify the authenticated patient.',

    couldNotLoadPatient:
      'Could not load patient information.',

    couldNotLoadAppointments:
      'Could not load appointments.',

    couldNotCreateAppointment:
      'Could not create the appointment.',

    couldNotConfirmAppointment:
      'Could not confirm the appointment.',

    couldNotCancelAppointment:
      'Could not cancel the appointment.',

    couldNotRescheduleAppointment:
      'Could not reschedule the appointment.',

    patientProfileUnavailable:
      'Patient information is not available.',

    noActiveDoctors:
      'There are no active doctors available.',

    patientInformation:
      'Patient',

    appointmentDetails:
      'Appointment details',

    /*
     * DOCTOR
     */

    doctorPortal:
      'Doctor Portal',

    doctorPortalSubtitle:
      'View and manage your medical appointments.',

    doctorInformation:
      'Doctor information',

    appointmentDuration:
      'Appointment duration',

    appointmentDurationMinutes:
      'Appointment duration (minutes)',

    appointmentsOfDay:
      'Appointments of the day',

    consultAppointments:
      'View appointments',

    consultingAppointments:
      'Loading appointments...',

    markAsAttended:
      'Mark as attended',

    markAttended:
      'Mark as attended',

    changeStatus:
      'Change status',

    changeAppointmentStatus:
      'Change appointment status',

    currentStatus:
      'Current status',

    newStatus:
      'New status',

    observation:
      'Observation',

    confirmStatusChange:
      'Confirm change',

    updatingStatus:
      'Updating status...',

    statusUpdatedSuccessfully:
      'The appointment status was updated successfully.',

    couldNotUpdateAppointmentStatus:
      'Could not update the appointment status.',

    couldNotLoadDoctorProfile:
      'Could not load doctor information.',

    doctorProfileNotFound:
      'No doctor profile was found for the authenticated user.',

    couldNotLoadDoctorAppointments:
      'Could not load the doctor appointments.',

    /*
     * SCHEDULER
     */

    schedulerPortal:
      'Scheduler Portal',

    schedulerPortalSubtitle:
      'View and manage the medical schedule.',

    consultSchedule:
      'View schedule',

    scheduleResults:
      'Doctor schedule',

    couldNotLoadSchedulerAppointments:
      'Could not load schedule appointments.',

    exportScheduleCsv:
      'Export schedule CSV',

    exportingSchedule:
      'Exporting schedule...',

    couldNotExportSchedule:
      'Could not export the schedule.',

    changeDateOrTime:
      'Change date or time',

    schedulerRescheduleReason:
      'Change requested by scheduler',

    schedulerCancellationReason:
      'Patient requested appointment cancellation.',

    /*
     * ADMIN
     */

    adminPortal:
      'Administrator Portal',

    adminPortalSubtitle:
      'Manage doctors and system configurations.',

    doctorManagement:
      'Doctor management',

    doctorManagementDescription:
      'Register, view, edit or delete doctors.',

    registerDoctor:
      'Register doctor',

    registerDoctorDescription:
      'Register a doctor and create their system access account.',

    createDoctor:
      'Create doctor',

    creatingDoctor:
      'Creating doctor...',

    editDoctor:
      'Edit doctor',

    noDoctorsRegistered:
      'No doctors are registered.',

    loadingDoctors:
      'Loading doctors...',

    doctorCredentialsNotEditable:
      'The username and password cannot be modified from this form.',

    completeRequiredFields:
      'Complete all required fields.',

    invalidAppointmentInterval:
      'The appointment interval must be greater than zero.',

    doctorCreatedSuccessfully:
      'The doctor was created successfully.',

    doctorUpdatedSuccessfully:
      'The doctor was updated successfully.',

    doctorDeletedSuccessfully:
      'The doctor was deleted successfully.',

    couldNotLoadDoctors:
      'Could not load doctors.',

    couldNotCreateDoctor:
      'Could not create the doctor.',

    couldNotUpdateDoctor:
      'Could not update the doctor.',

    couldNotDeleteDoctor:
      'Could not delete the doctor.',

    deleteDoctorQuestion:
      'Do you want to delete this doctor?',

    /*
     * AVAILABILITY CONFIGURATION
     */

    availabilityManagement:
      'Availability configuration',

    availabilityManagementDescription:
      'Configure the days, schedules, intervals and scheduling window for each doctor.',

    consultAvailabilityConfiguration:
      'View configuration',

    loadingAvailabilityConfigurations:
      'Loading availability configurations...',

    addAvailability:
      'Add schedule',

    editAvailability:
      'Edit schedule',

    saveAvailability:
      'Save configuration',

    selectDoctorFirst:
      'You must select a doctor first.',

    selectDoctorToConsultAvailability:
      'Select a doctor to view their availability configuration.',

    noAvailabilityConfigured:
      'The doctor does not have availability schedules configured.',

    dayOfWeek:
      'Day of week',

    schedule:
      'Schedule',

    schedulingWindow:
      'Scheduling window',

    schedulingWindowWeeks:
      'Scheduling window (weeks)',

    appointmentIntervalMinutes:
      'Appointment interval (minutes)',

    startTime:
      'Start time',

    endTime:
      'End time',

    monday:
      'Monday',

    tuesday:
      'Tuesday',

    wednesday:
      'Wednesday',

    thursday:
      'Thursday',

    friday:
      'Friday',

    saturday:
      'Saturday',

    sunday:
      'Sunday',

    invalidAvailabilityTimeRange:
      'The start time must be earlier than the end time.',

    invalidWeekWindow:
      'The scheduling window must be greater than zero.',

    availabilityCreatedSuccessfully:
      'The availability schedule was created successfully.',

    couldNotCreateAvailability:
      'Could not create the availability schedule.',

    availabilityUpdatedSuccessfully:
      'The availability schedule was updated successfully.',

    couldNotUpdateAvailability:
      'Could not update the availability schedule.',

    couldNotLoadAvailabilityConfigurations:
      'Could not load availability configurations.',

    /*
     * OTHER
     */

    unauthorized:
      'Unauthorized',

    unauthorizedMessage:
      'You do not have permission to access this section.',

    notFound:
      'Page not found',

    notFoundMessage:
      'The requested page does not exist.',

    notRegistered:
      'Not registered',

          appointmentsForDate:
      'Appointments of the day',

    confirmScheduledAppointment:
      'Confirm appointment',

    notAvailable:
      'Not available',

    observationPlaceholder:
      'Enter an observation',
  },
};

export type TranslationKey =
  string;