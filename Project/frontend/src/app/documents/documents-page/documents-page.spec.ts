import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { of } from 'rxjs';

import { AuthService } from '../../auth/services/auth.service';
import { PagedResponse } from '../../models/api.models';
import { DocumentApiService } from '../../services/document-api.service';
import { UserApiService } from '../../users/services/user-api.service';
import { DocumentsPageComponent } from './documents-page';

describe('DocumentsPageComponent', () => {
  let fixture: ComponentFixture<DocumentsPageComponent>;
  let component: DocumentsPageComponent;

  const emptyDocumentsResponse: PagedResponse<never> = {
    code: 'OK',
    payload: [],
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0,
  };

  const emptyUsersResponse = {
    code: 'OK',
    payload: [],
    page: 0,
    size: 1000,
    totalElements: 0,
    totalPages: 0,
  };

  const authServiceMock = {
    hasRole: vi.fn(),
  };

  const documentApiServiceMock = {
    getAll: vi.fn().mockReturnValue(of(emptyDocumentsResponse)),
    getById: vi.fn(),
    downloadFile: vi.fn(),
    delete: vi.fn(),
  };

  const userApiServiceMock = {
    list: vi.fn().mockReturnValue(of(emptyUsersResponse)),
  };

  beforeEach(async () => {
    vi.clearAllMocks();
    currentRole = 'MANAGER';

    authServiceMock.hasRole.mockImplementation((roles: string | string[]) => {
      const allowedRoles = Array.isArray(roles) ? roles : [roles];
      return allowedRoles.includes(currentRole);
    });

    await TestBed.configureTestingModule({
      imports: [DocumentsPageComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
        { provide: ToastrService, useValue: { success: vi.fn(), error: vi.fn() } },
        { provide: DocumentApiService, useValue: documentApiServiceMock },
        { provide: UserApiService, useValue: userApiServiceMock },
      ],
    }).compileComponents();
  });

  let currentRole: 'ADMIN' | 'MANAGER' | 'OPERATOR' | 'APPROVER' = 'MANAGER';

  function renderWithRole(role: typeof currentRole): HTMLElement {
    currentRole = role;
    fixture = TestBed.createComponent(DocumentsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('shows upload actions for admin or operator roles', () => {
    const compiled = renderWithRole('ADMIN');

    expect(compiled.textContent).toContain('Upload document');
  });

  it('shows search and filter controls for managers', () => {
    const compiled = renderWithRole('MANAGER');

    expect(compiled.textContent).toContain('Search and filters');
    expect(compiled.textContent).toContain(
      'Find documents by title, ID, status, date, assignee, or type.'
    );
    expect(compiled.textContent).toContain('Reset filters');
  });

  it('loads documents with combined search and filter criteria', () => {
    renderWithRole('MANAGER');

    component.search = 'invoice';
    component.statusFilter = 'READY_FOR_APPROVAL';
    component.createdFromFilter = '2026-05-01';
    component.createdToFilter = '2026-05-31';
    component.assignedUserIdFilter = 42;
    component.documentTypeFilter = 'INVOICE';
    component.loadDocuments();

    expect(documentApiServiceMock.getAll).toHaveBeenLastCalledWith(
      expect.objectContaining({
        search: 'invoice',
        documentStatus: 'READY_FOR_APPROVAL',
        createdFrom: '2026-05-01',
        createdTo: '2026-05-31',
        assignedUserId: 42,
        documentType: 'INVOICE',
        page: 0,
        size: 20,
        sortBy: 'id',
        sortDirection: 'desc',
      })
    );
  });

  it('resets all filters and reloads the default list', () => {
    renderWithRole('MANAGER');

    component.search = 'invoice';
    component.statusFilter = 'COMPLETED';
    component.createdFromFilter = '2026-05-01';
    component.createdToFilter = '2026-05-31';
    component.assignedUserIdFilter = 42;
    component.documentTypeFilter = 'RECEIPT';
    component.resetFilters();

    expect(component.search).toBe('');
    expect(component.statusFilter).toBe('');
    expect(component.createdFromFilter).toBe('');
    expect(component.createdToFilter).toBe('');
    expect(component.assignedUserIdFilter).toBeNull();
    expect(component.documentTypeFilter).toBe('');
    expect(documentApiServiceMock.getAll).toHaveBeenLastCalledWith(
      expect.objectContaining({
        search: undefined,
        documentStatus: undefined,
        createdFrom: undefined,
        createdTo: undefined,
        assignedUserId: undefined,
        documentType: undefined,
      })
    );
  });
});
