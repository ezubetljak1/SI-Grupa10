import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApiResponse } from '../../models/api.models';
import { DocflowDocument } from '../../documents/models/document.models';

@Injectable({
  providedIn: 'root',
})
export class ApprovalApiService {
  private readonly http = inject(HttpClient);

  getCompletedForReview(): Observable<ApiResponse<DocflowDocument[]>> {
    return this.http.get<ApiResponse<DocflowDocument[]>>('/api/approvals/completed');
  }
}
