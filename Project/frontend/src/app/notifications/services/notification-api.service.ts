import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ApiResponse } from '../../models/api.models';
import { Notification, UnreadCountResponse } from '../models/notification.models';

@Injectable({
  providedIn: 'root',
})
export class NotificationApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/notifications';

  getMyNotifications(): Observable<Notification[]> {
    return this.http
      .get<ApiResponse<Notification[]>>(`${this.baseUrl}/my`)
      .pipe(map((response) => response.payload || []));
  }

  getUnreadCount(): Observable<number> {
    return this.http
      .get<ApiResponse<UnreadCountResponse>>(`${this.baseUrl}/my/unread-count`)
      .pipe(map((response) => response.payload.unreadCount));
  }

  markOneRead(id: number): Observable<Notification> {
    return this.http
      .patch<ApiResponse<Notification>>(`${this.baseUrl}/${id}/read`, {})
      .pipe(map((response) => response.payload));
  }

  markAllRead(): Observable<void> {
    return this.http
      .patch<ApiResponse<void>>(`${this.baseUrl}/read-all`, {})
      .pipe(map((response) => response.payload));
  }
}
