import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DocumentApiService {
  private http = inject(HttpClient);
  private readonly baseUrl = '/api/documents';

  getAll(): Observable<any> {
    return this.http.get(this.baseUrl);
  }

  getById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  create(payload: any): Observable<any> {
    return this.http.post(this.baseUrl, payload);
  }

  update(id: number, payload: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  filter(params: {
    name?: string;
    documentType?: string;
    documentStatus?: string;
    companyId?: number;
    page?: number;
    size?: number;
    sortBy?: string;
    sortDirection?: string;
  }): Observable<any> {
    return this.http.get(this.baseUrl, { params: params as any });
  }
}