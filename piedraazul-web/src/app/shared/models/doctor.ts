export interface Doctor {
  id: number;
  userId: number;
  username: string;
  fullName: string;
  specialty: string;
  intervalMinutes: number;
  active: boolean;
  userStatus: string;
  userRole: string;
}