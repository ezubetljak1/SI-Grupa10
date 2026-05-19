import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { DashboardResponse } from '../models/dashboard.models';

@Injectable({
  providedIn: 'root'
})
export class DashboardApiService {

  private readonly http = inject(HttpClient);

  getCompanyDashboard() {
    return this.http.get<{
      code: string;
      payload: DashboardResponse;
    }>('/api/dashboard/company');
  }
}
