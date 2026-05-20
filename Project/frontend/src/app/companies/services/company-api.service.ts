import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiResponse } from '../../models/api.models';
import { CompanyRegisterRequest, CompanyRegisterResponse } from '../models/company.models';

@Injectable({
  providedIn: 'root',
})
export class CompanyApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/public/companies';

  registerCompany(
    payload: CompanyRegisterRequest
  ): Observable<ApiResponse<CompanyRegisterResponse>> {
    return this.http.post<ApiResponse<CompanyRegisterResponse>>(
      `${this.baseUrl}/register`,
      payload
    );
  }
}
