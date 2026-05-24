import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuditLog } from '../documents/models/audit.models';
import { ApiResponse, PagedResponse } from '../models/api.models';
import {
  ConfirmDocumentTypeRequest,
  DocflowDocument,
  DocumentCreateRequest,
  DocumentFilterRequest,
  DocumentUpdateRequest,
  DocumentUploadRequest,
  ManualClassificationDocumentType,
} from '../documents/models/document.models';
import {
  CreateExtractionFieldRequest,
  Extraction,
  ExtractionField,
} from '../documents/models/extraction.models';
import {
  CreateCommentRequest,
  DocumentComment,
  StatusHistoryEntry,
} from '../documents/models/workflow.models';

@Injectable({
  providedIn: 'root',
})
export class DocumentApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/documents';
  private readonly extractionsBaseUrl = '/api/extractions';

  getAll(params: DocumentFilterRequest = {}): Observable<PagedResponse<DocflowDocument>> {
    return this.http.get<PagedResponse<DocflowDocument>>(this.baseUrl, {
      params: this.toHttpParams(params),
    });
  }

  getById(id: number): Observable<ApiResponse<DocflowDocument>> {
    return this.http.get<ApiResponse<DocflowDocument>>(`${this.baseUrl}/${id}`);
  }

  create(payload: DocumentCreateRequest): Observable<ApiResponse<DocflowDocument>> {
    return this.http.post<ApiResponse<DocflowDocument>>(this.baseUrl, payload);
  }

  upload(request: DocumentUploadRequest): Observable<ApiResponse<DocflowDocument>> {
    const formData = new FormData();

    formData.append('file', request.file);
    formData.append('documentType', request.documentType);

    if (request.name?.trim()) {
      formData.append('name', request.name.trim());
    }

    return this.http.post<ApiResponse<DocflowDocument>>(`${this.baseUrl}/upload`, formData);
  }

  downloadFile(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/file`, {
      responseType: 'blob',
    });
  }

  getPreview(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/preview`, {
      responseType: 'blob',
    });
  }

  update(id: number, payload: DocumentUpdateRequest): Observable<ApiResponse<DocflowDocument>> {
    return this.http.put<ApiResponse<DocflowDocument>>(`${this.baseUrl}/${id}`, payload);
  }

  confirmDocumentType(
    id: number,
    documentType: ManualClassificationDocumentType
  ): Observable<ApiResponse<DocflowDocument>> {
    const payload: ConfirmDocumentTypeRequest = { documentType };

    return this.http.patch<ApiResponse<DocflowDocument>>(
      `${this.baseUrl}/${id}/classification`,
      payload
    );
  }

  delete(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.baseUrl}/${id}`);
  }

  processExtraction(documentId: number): Observable<ApiResponse<Extraction>> {
    return this.http.post<ApiResponse<Extraction>>(`${this.baseUrl}/${documentId}/extraction`, {});
  }

  retryExtraction(documentId: number): Observable<ApiResponse<Extraction>> {
    return this.http.post<ApiResponse<Extraction>>(
      `${this.baseUrl}/${documentId}/extraction/retry`,
      {}
    );
  }

  getExtraction(documentId: number): Observable<ApiResponse<Extraction>> {
    return this.http.get<ApiResponse<Extraction>>(`${this.baseUrl}/${documentId}/extraction`);
  }

  getExtractionFields(documentId: number): Observable<ApiResponse<ExtractionField[]>> {
    return this.http.get<ApiResponse<ExtractionField[]>>(
      `${this.baseUrl}/${documentId}/extraction/fields`
    );
  }

  updateExtractionField(
    extractionId: number,
    fieldId: number,
    value: string
  ): Observable<ApiResponse<ExtractionField>> {
    return this.http.patch<ApiResponse<ExtractionField>>(
      `${this.extractionsBaseUrl}/${extractionId}/fields/${fieldId}`,
      { value }
    );
  }

  addExtractionField(
    extractionId: number,
    payload: CreateExtractionFieldRequest
  ): Observable<ApiResponse<ExtractionField>> {
    return this.http.post<ApiResponse<ExtractionField>>(
      `${this.extractionsBaseUrl}/${extractionId}/fields`,
      payload
    );
  }

  confirmExtraction(documentId: number): Observable<ApiResponse<Extraction>> {
    return this.http.post<ApiResponse<Extraction>>(
      `${this.baseUrl}/${documentId}/extraction/confirm`,
      {}
    );
  }

  getStatusHistory(documentId: number): Observable<ApiResponse<StatusHistoryEntry[]>> {
    return this.http.get<ApiResponse<StatusHistoryEntry[]>>(
      `${this.baseUrl}/${documentId}/status-history`
    );
  }

  getComments(documentId: number): Observable<ApiResponse<DocumentComment[]>> {
    return this.http.get<ApiResponse<DocumentComment[]>>(
      `${this.baseUrl}/${documentId}/comments`
    );
  }

  createComment(
    documentId: number,
    payload: CreateCommentRequest
  ): Observable<ApiResponse<DocumentComment>> {
    return this.http.post<ApiResponse<DocumentComment>>(
      `${this.baseUrl}/${documentId}/comments`,
      payload
    );
  }

  filter(params: DocumentFilterRequest): Observable<PagedResponse<DocflowDocument>> {
    return this.getAll(params);
  }

  private toHttpParams(params: DocumentFilterRequest): HttpParams {
    let httpParams = new HttpParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        httpParams = httpParams.set(key, String(value));
      }
    });

    return httpParams;
  }

  getAuditLog(documentId: number) {
    return this.http.get<{ code: string; payload: AuditLog[] }>(
      `/api/documents/${documentId}/audit-log`
    );
  }
}
