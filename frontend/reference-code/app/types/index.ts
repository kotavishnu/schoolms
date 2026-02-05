export interface Student {
  id: string;
  firstName: string;
  lastName: string;
  guardianName: string;
  motherName: string;
  phone: string;
  age: number;
  email: string;
  address: string;
  identificationMarks: string;
  status: 'ACTIVE' | 'INACTIVE';
}

export interface Configuration {
  id: string;
  category: string;
  key: string;
  value: string;
  description: string;
  lastUpdated: string;
}