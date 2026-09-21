export type DayOfWeek =
  | 'MONDAY'
  | 'TUESDAY'
  | 'WEDNESDAY'
  | 'THURSDAY'
  | 'FRIDAY'
  | 'SATURDAY'
  | 'SUNDAY';

export interface AvailabilityConfiguration {
  id: number;

  doctorId: number;

  dayOfWeek: DayOfWeek;

  startTime: string;

  endTime: string;

  intervalMinutes: number;

  weekWindow: number;

  active: boolean;
}

export interface SaveAvailabilityConfigurationRequest {
  doctorId: number;

  dayOfWeek: DayOfWeek;

  startTime: string;

  endTime: string;

  intervalMinutes: number;

  weekWindow: number;
}