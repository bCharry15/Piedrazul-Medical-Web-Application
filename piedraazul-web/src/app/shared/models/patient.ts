export interface PatientProfile {
  id: number;
  username: string;
  documentNumber: string;
  documentType: string;
  firstNames: string;
  lastNames: string;
  phone: string;
  gender: string;
  birthDate: string | null;
  email: string | null;
}