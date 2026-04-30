export interface ApiResponse<T> {
  code: string;
  payload: T;
}

export interface PagedResponse<T> {
  code: string;
  payload: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ValidationError {
  code: string;
  message: string;
}