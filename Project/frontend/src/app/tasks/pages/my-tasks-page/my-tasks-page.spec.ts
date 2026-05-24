import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { of } from 'rxjs';

import { TaskResponse } from '../../models/task.models';
import { TaskApiService } from '../../services/task-api.service';
import { MyTasksPageComponent } from './my-tasks-page';

describe('MyTasksPageComponent', () => {
  let fixture: ComponentFixture<MyTasksPageComponent>;
  let taskApiMock: {
    getMyTasks: ReturnType<typeof vi.fn>;
    start: ReturnType<typeof vi.fn>;
  };

  const openTask: TaskResponse = {
    id: 1,
    documentId: 7,
    documentName: 'Invoice 7',
    documentStatus: 'EXTRACTED',
    assignedUserId: 3,
    assignedUserName: 'Task Operator',
    assignedByUserId: 1,
    assignedByUserName: 'Task Admin',
    taskType: 'CORRECTION',
    status: 'OPEN',
    createdAt: '2026-05-24T09:00:00',
  };

  beforeEach(async () => {
    taskApiMock = {
      getMyTasks: vi.fn().mockReturnValue(of({ code: 'SUCCESS', payload: [openTask] })),
      start: vi.fn().mockReturnValue(of({ code: 'SUCCESS', payload: { ...openTask, status: 'IN_PROGRESS' } })),
    };

    await TestBed.configureTestingModule({
      imports: [MyTasksPageComponent],
      providers: [
        provideRouter([]),
        { provide: TaskApiService, useValue: taskApiMock },
        {
          provide: ToastrService,
          useValue: {
            success: vi.fn(),
            error: vi.fn(),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(MyTasksPageComponent);
    fixture.detectChanges();
  });

  it('renders open tasks and exposes task actions', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Invoice 7');
    expect(compiled.textContent).toContain('correction');
    expect(compiled.textContent).toContain('Start');
    expect(compiled.textContent).toContain('Complete in document');
  });

  it('starts an open task', () => {
    fixture.componentInstance.startTask(openTask);

    expect(taskApiMock.start).toHaveBeenCalledWith(openTask.id);
    expect(taskApiMock.getMyTasks).toHaveBeenCalledTimes(2);
  });
});
