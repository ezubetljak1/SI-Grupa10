import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { AuthService } from '../../auth/services/auth.service';
import { DocumentApiService } from '../../services/document-api.service';
import { UserApiService } from '../../users/services/user-api.service';
import { TaskApiService } from '../../tasks/services/task-api.service';
import { ToastrService } from 'ngx-toastr';
import { DocumentDetailPageComponent } from './document-detail-page';

describe('DocumentDetailPageComponent', () => {
  let fixture: ComponentFixture<DocumentDetailPageComponent>;

  const completedDocument = {
    id: 1,
    companyId: 1,
    createdBy: 1,
    name: 'Completed invoice',
    fileType: 'application/pdf',
    documentType: 'INVOICE',
    detectedDocumentType: null,
    classificationConfidence: null,
    processorIdUsed: null,
    storagePath: 'company-1/completed-invoice.pdf',
    uploadDate: '2026-05-20T10:00:00',
    fileSize: 2048,
    documentStatus: 'COMPLETED',
  };

  const authServiceMock = {
    hasRole: vi.fn().mockReturnValue(false),
    profile: null,
  };

  const documentApiServiceMock = {
    getById: vi.fn().mockReturnValue(of({ code: 'OK', payload: completedDocument })),
    getStatusHistory: vi.fn().mockReturnValue(of({ code: 'OK', payload: [] })),
    getComments: vi.fn().mockReturnValue(of({ code: 'OK', payload: [] })),
    getAuditLog: vi.fn(),
    getPreview: vi.fn(),
  };

  const userApiServiceMock = {
    list: vi.fn(),
  };

  const taskApiServiceMock = {
    getByDocument: vi.fn(),
  };

  beforeEach(async () => {
    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [DocumentDetailPageComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({ id: '1' })),
          },
        },
        { provide: AuthService, useValue: authServiceMock },
        { provide: DocumentApiService, useValue: documentApiServiceMock },
        { provide: UserApiService, useValue: userApiServiceMock },
        { provide: TaskApiService, useValue: taskApiServiceMock },
        {
          provide: ToastrService,
          useValue: {
            success: vi.fn(),
            error: vi.fn(),
            warning: vi.fn(),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DocumentDetailPageComponent);
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('hides workflow-specific sections for completed documents', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Completed invoice');
    expect(compiled.textContent).not.toContain('Task assignment');
    expect(compiled.textContent).not.toContain('Document preview');
    expect(compiled.textContent).not.toContain('Extracted fields');
  });
});
