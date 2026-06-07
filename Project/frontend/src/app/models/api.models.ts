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

export type FieldValidationErrors = Record<string, string>;

export type ApiErrorResponse =
  | ApiResponse<string>
  | ValidationError
  | ValidationError[]
  | FieldValidationErrors;

export function extractApiErrorMessage(
  errorBody: unknown,
  fallback = 'Request failed. Please try again.'
): string {
  if (typeof errorBody === 'string') {
    return errorBody;
  }

  if (isValidationErrorArray(errorBody)) {
    return errorBody.map((error) => error.message).join('\n');
  }

  if (isValidationError(errorBody)) {
    return errorBody.message || errorBody.code || fallback;
  }

  if (isApiResponseString(errorBody)) {
    return errorBody.payload || errorBody.code || fallback;
  }

  if (isFieldValidationErrors(errorBody)) {
    return Object.entries(errorBody)
      .map(([field, message]) => `${formatFieldName(field)}: ${message}`)
      .join('\n');
  }

  return fallback;
}

function isValidationErrorArray(value: unknown): value is ValidationError[] {
  return Array.isArray(value) && value.every(isValidationError);
}

function isValidationError(value: unknown): value is ValidationError {
  return (
    isObject(value) &&
    typeof value['code'] === 'string' &&
    typeof value['message'] === 'string'
  );
}

function isApiResponseString(value: unknown): value is ApiResponse<string> {
  return (
    isObject(value) &&
    typeof value['code'] === 'string' &&
    typeof value['payload'] === 'string'
  );
}

function isFieldValidationErrors(value: unknown): value is FieldValidationErrors {
  return (
    isObject(value) &&
    !Array.isArray(value) &&
    !('code' in value) &&
    Object.values(value).every((message) => typeof message === 'string')
  );
}

function isObject(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null;
}

function formatFieldName(field: string): string {
  return field
    .replace(/([A-Z])/g, ' $1')
    .replace(/^./, (letter) => letter.toUpperCase());
}