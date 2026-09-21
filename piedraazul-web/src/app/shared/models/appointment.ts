export interface CreateAppointmentRequest {
  documentNumber: string;
  documentType: string;
  firstNames: string;
  lastNames: string;
  phone: string;
  gender: string;
  birthDate: string | null;
  email: string | null;
  doctorId: number;
  date: string;
  time: string;
  notes: string;
}

export interface CreatedAppointment {
  id: number;
  documentNumber: string;
  patientName: string;
  doctorName: string;
  date: string;
  time: string;
  status: string;
}

export interface PatientAppointment {
  id: number;
  patientId: number;
  patientName: string;
  doctorId: number;
  doctorName: string;
  date: string;
  time: string;
  status: string;
  notes: string;
}

export interface DoctorAppointmentsResponse {
  doctorId: number;
  doctorName: string;
  date: string;
  count: number;
  appointments: PatientAppointment[];
}

export interface RescheduleAppointmentRequest {
  newDate: string;
  newTime: string;
  responsible: string;
  reason: string;
}

export interface RescheduledAppointment {
  id: number;
  patientId: number;
  patientName: string;
  doctorId: number;
  doctorName: string;
  date: string;
  time: string;
  status: string;
  notes: string;

  previousDate: string;
  previousTime: string;

  newDate: string;
  newTime: string;

  message: string;
}

export interface ChangeAppointmentStatusRequest {
  status: string;
  notes: string;
}

export interface ChangedAppointmentStatus {
  id: number;
  patientId: number;
  patientName: string;
  doctorId: number;
  doctorName: string;
  date: string;
  time: string;

  previousStatus: string;
  newStatus: string;

  status: string;
  notes: string;
  message: string;
}