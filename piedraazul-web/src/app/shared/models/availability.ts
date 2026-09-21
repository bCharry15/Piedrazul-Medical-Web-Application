export interface Availability {
  doctorId: number;
  doctor: string;
  date: string;
  intervalMinutes: number;
  startTime: string;
  endTime: string;
  availableSlots: string[];
}