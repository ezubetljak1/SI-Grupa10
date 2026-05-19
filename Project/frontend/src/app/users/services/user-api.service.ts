import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiResponse, PagedResponse } from '../../models/api.models';
import {
  UserCreateRequest,
  UserResponse,
  UserRoleChangeRequest,
  UserStatusChangeRequest,
  UserUpdateRequest,
} from '../models/user.models';

@Injectable({
  providedIn: 'root',
})
export class UserApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/company/users';

  list(params: { page?: number; size?: number; search?: string } = {}): Observable<PagedResponse<UserResponse>> {
    let httpParams = new HttpParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        httpParams = httpParams.set(key, String(value));
      }
    });

    return this.http.get<PagedResponse<UserResponse>>(this.baseUrl, { params: httpParams });
  }

  getCurrentUser(): Observable<ApiResponse<UserResponse>> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.baseUrl}/me`);
  }

  create(payload: UserCreateRequest): Observable<ApiResponse<UserResponse>> {
    return this.http.post<ApiResponse<UserResponse>>(this.baseUrl, payload);
  }

  update(id: number, payload: UserUpdateRequest): Observable<ApiResponse<UserResponse>> {
    return this.http.patch<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}`, payload);
  }

  changeRole(id: number, payload: UserRoleChangeRequest): Observable<ApiResponse<UserResponse>> {
    return this.http.patch<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/role`, payload);
  }

  changeStatus(
    id: number,
    payload: UserStatusChangeRequest
  ): Observable<ApiResponse<UserResponse>> {
    return this.http.patch<ApiResponse<UserResponse>>(`${this.baseUrl}/${id}/status`, payload);
  }

  resetPassword(id: number): Observable<ApiResponse<string>> {
    return this.http.post<ApiResponse<string>>(`${this.baseUrl}/${id}/reset-password`, {});
  }
}
