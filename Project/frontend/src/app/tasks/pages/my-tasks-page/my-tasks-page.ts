import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

import { EmptyStateComponent, PageHeaderComponent, UiCardComponent } from '../../../shared/components';
import { TaskApiService } from '../../services/task-api.service';
import { TaskResponse, TaskStatus } from '../../models/task.models';

type TaskTab = 'ACTIVE' | 'COMPLETED' | 'CANCELLED';

@Component({
  selector: 'app-my-tasks-page',
  standalone: true,
  imports: [CommonModule, RouterLink, EmptyStateComponent, PageHeaderComponent, UiCardComponent],
  templateUrl: './my-tasks-page.html',
  styleUrl: './my-tasks-page.scss',
})
export class MyTasksPageComponent implements OnInit {
  private readonly taskApi = inject(TaskApiService);
  private readonly toastr = inject(ToastrService);

  tasks: TaskResponse[] = [];
  loading = false;
  actionTaskId: number | null = null;
  selectedTab: TaskTab = 'ACTIVE';
  error: string | null = null;

  readonly tabs: { value: TaskTab; label: string }[] = [
    { value: 'ACTIVE', label: 'Open' },
    { value: 'COMPLETED', label: 'Completed' },
    { value: 'CANCELLED', label: 'Cancelled' },
  ];

  ngOnInit(): void {
    this.loadTasks();
  }

  get visibleTasks(): TaskResponse[] {
    if (this.selectedTab === 'ACTIVE') {
      return this.tasks.filter((task) => task.status === 'OPEN' || task.status === 'IN_PROGRESS');
    }

    return this.tasks.filter((task) => task.status === this.selectedTab);
  }

  loadTasks(): void {
    this.loading = true;
    this.error = null;

    this.taskApi.getMyTasks().subscribe({
      next: (response) => {
        this.loading = false;
        this.tasks = response.payload ?? [];
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.error = this.extractErrorMessage(err.error) ?? 'Failed to load tasks.';
      },
    });
  }

  startTask(task: TaskResponse): void {
    this.runTaskAction(task.id, () => this.taskApi.start(task.id), 'Task started.');
  }

  formatDate(value?: string | null): string {
    if (!value) {
      return '-';
    }

    return new Date(value).toLocaleString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  formatTaskType(value: string): string {
    return value.replaceAll('_', ' ').toLowerCase();
  }

  statusClass(status: TaskStatus): string {
    return `task-status task-status--${status.toLowerCase()}`;
  }

  isOverdue(task: TaskResponse): boolean {
    return (
      !!task.dueDate &&
      (task.status === 'OPEN' || task.status === 'IN_PROGRESS') &&
      new Date(task.dueDate).getTime() < Date.now()
    );
  }

  private runTaskAction(
    taskId: number,
    action: () => ReturnType<TaskApiService['start']>,
    successMessage: string
  ): void {
    this.actionTaskId = taskId;

    action().subscribe({
      next: () => {
        this.actionTaskId = null;
        this.toastr.success(successMessage, 'Success');
        this.loadTasks();
      },
      error: (err: HttpErrorResponse) => {
        this.actionTaskId = null;
        this.toastr.error(this.extractErrorMessage(err.error) ?? 'Task action failed.', 'Error');
      },
    });
  }

  private extractErrorMessage(errorBody: unknown): string | null {
    if (Array.isArray(errorBody)) {
      const first = errorBody[0] as { message?: string; code?: string } | undefined;
      return first?.message ?? first?.code ?? null;
    }

    if (typeof errorBody === 'string') {
      return errorBody;
    }

    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as { message?: string; payload?: string; code?: string };
      return body.message ?? body.payload ?? body.code ?? null;
    }

    return null;
  }
}
