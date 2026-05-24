import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiResponse } from '../../models/api.models';
import { AssignTaskRequest, TaskResponse } from '../models/task.models';

@Injectable({
  providedIn: 'root',
})
export class TaskApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/tasks';

  assign(documentId: number, payload: AssignTaskRequest): Observable<ApiResponse<TaskResponse>> {
    return this.http.post<ApiResponse<TaskResponse>>(
      `/api/documents/${documentId}/tasks/assign`,
      payload
    );
  }

  getMyTasks(): Observable<ApiResponse<TaskResponse[]>> {
    return this.http.get<ApiResponse<TaskResponse[]>>(`${this.baseUrl}/my`);
  }

  getAll(): Observable<ApiResponse<TaskResponse[]>> {
    return this.http.get<ApiResponse<TaskResponse[]>>(this.baseUrl);
  }

  start(id: number): Observable<ApiResponse<TaskResponse>> {
    return this.http.patch<ApiResponse<TaskResponse>>(`${this.baseUrl}/${id}/start`, {});
  }

  complete(id: number): Observable<ApiResponse<TaskResponse>> {
    return this.http.patch<ApiResponse<TaskResponse>>(`${this.baseUrl}/${id}/complete`, {});
  }

  cancel(id: number): Observable<ApiResponse<TaskResponse>> {
    return this.http.patch<ApiResponse<TaskResponse>>(`${this.baseUrl}/${id}/cancel`, {});
  }
}
