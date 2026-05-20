export interface CompanyRegisterRequest {
  companyName: string;
  companyAddress: string;
  companyEmail: string;
  adminFirstName: string;
  adminLastName: string;
  adminEmail: string;
}

export interface CompanyRegisterResponse {
  companyId: number;
  companyName: string;
  adminTemporaryPassword: string;
  message: string;
}
