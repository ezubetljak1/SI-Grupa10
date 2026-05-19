import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';

import { DashboardApiService } from '../../services/dashboard-api.service';
import { DashboardResponse } from '../../models/dashboard.models';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-page.html',
  styleUrl: './dashboard-page.scss'
})
export class DashboardPage implements OnInit {

  private readonly dashboardApiService = inject(DashboardApiService);

  dashboard: DashboardResponse | null = null;

  loading = false;

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {

    this.loading = true;

    this.dashboardApiService
      .getCompanyDashboard()
      .subscribe({
        next: (response) => {
          this.loading = false;
          this.dashboard = response.payload;
        },
        error: () => {
          this.loading = false;
        }
      });
  }
}
