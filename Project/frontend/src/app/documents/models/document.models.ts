export interface Document {
  id: number;
  companyId: number;
  createdBy: number;
  name: string;
  fileType: string;
  documentType: string;
  storagePath: string;
  uploadDate: string;
  fileSize: number;
  documentStatus: string;
}

export interface DocumentCreateRequest {
  companyId: number;
  createdByUserId: number;
  name: string;
  fileType: string;
  documentType: string;
  storagePath: string;
  fileSize: number;
}

export interface DocumentUpdateRequest {
  name?: string;
  documentType?: string;
  documentStatus?: string;
}

export interface DocumentFilterRequest {
  name?: string;
  documentType?: string;
  documentStatus?: string;
  companyId?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}