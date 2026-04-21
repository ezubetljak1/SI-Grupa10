import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApiResponse, PagedResponse } from '../models/api.models';
import {
  Document,
  DocumentCreateRequest,
  DocumentUpdateRequest,
  DocumentFilterRequest
} from '../documents/models/document.models';

@Injectable({
  providedIn: 'root'
})
export class DocumentApiService {
  private http = inject(HttpClient);
  private readonly baseUrl = '/api/documents';

  getAll(): Observable<PagedResponse<Document>> {
    return this.http.get<PagedResponse<Document>>(this.baseUrl);
  }

  getById(id: number): Observable<ApiResponse<Document>> {
    return this.http.get<ApiResponse<Document>>(`${this.baseUrl}/${id}`);
  }

  create(payload: DocumentCreateRequest): Observable<ApiResponse<Document>> {
    return this.http.post<ApiResponse<Document>>(this.baseUrl, payload);
  }

  update(id: number, payload: DocumentUpdateRequest): Observable<ApiResponse<Document>> {
    return this.http.put<ApiResponse<Document>>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.baseUrl}/${id}`);
  }

  filter(params: DocumentFilterRequest): Observable<PagedResponse<Document>> {
    return this.http.get<PagedResponse<Document>>(this.baseUrl, {
      params: params as any
    });
  }
}