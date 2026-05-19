import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { of } from 'rxjs';

import { AuthService } from '../../auth/services/auth.service';
import { PagedResponse } from '../../models/api.models';
import { DocumentApiService } from '../../services/document-api.service';
import { DocumentsPageComponent } from './documents-page';

describe('DocumentsPageComponent', () => {
  let fixture: ComponentFixture<DocumentsPageComponent>;
  let authServiceMock: { hasRole: ReturnType<typeof vi.fn> };

  const emptyDocumentsResponse: PagedResponse<never> = {
    code: 'OK',
    payload: [],
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
  };

  beforeEach(async () => {
    authServiceMock = {
      hasRole: vi.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [DocumentsPageComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
        {
          provide: ToastrService,
          useValue: {
            success: vi.fn(),
            error: vi.fn(),
          },
        },
        {
          provide: DocumentApiService,
          useValue: {
            getAll: vi.fn().mockReturnValue(of(emptyDocumentsResponse)),
          },
        },
      ],
    }).compileComponents();
  });

  function renderWithRole(canUpload: boolean): HTMLElement {
    authServiceMock.hasRole.mockReturnValue(canUpload);
    fixture = TestBed.createComponent(DocumentsPageComponent);
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('shows upload actions for admin or operator roles', () => {
    const compiled = renderWithRole(true);

    expect(compiled.textContent).toContain('Upload document');
  });

  it('hides upload actions for manager role', () => {
    const compiled = renderWithRole(false);

    expect(compiled.textContent).not.toContain('Upload document');
  });
});
