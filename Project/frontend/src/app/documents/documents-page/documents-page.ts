import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DocumentApiService } from '../../services/document-api.service';

@Component({
  selector: 'app-documents-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './documents-page.html',
  styleUrl: './documents-page.scss'
})
export class DocumentsPageComponent {
  private documentApiService = inject(DocumentApiService);

  result: any = null;
  error: any = null;

  loadAll(): void {
    this.error = null;
    this.documentApiService.getAll().subscribe({
      next: (response) => {
        this.result = response;
      },
      error: (err) => {
        this.error = err;
      }
    });
  }

  loadById(): void {
    this.error = null;
    this.documentApiService.getById(1).subscribe({
      next: (response) => {
        this.result = response;
      },
      error: (err) => {
        this.error = err;
      }
    });
  }

  createDocument(): void {
    this.error = null;
    const payload = {
      companyId: 1,
      createdByUserId: 101,
      name: 'Frontend Test Document',
      fileType: 'pdf',
      documentType: 'OTHER',
      storagePath: 'docs/frontend-test.pdf',
      fileSize: 123456
    };

    this.documentApiService.create(payload).subscribe({
      next: (response) => {
        this.result = response;
      },
      error: (err) => {
        this.error = err;
      }
    });
  }

  updateDocument(): void {
    this.error = null;
    const payload = {
      name: 'Frontend Updated Document',
      documentStatus: 'APPROVED',
      documentType: 'OTHER'
    };

    this.documentApiService.update(1, payload).subscribe({
      next: (response) => {
        this.result = response;
      },
      error: (err) => {
        this.error = err;
      }
    });
  }

  deleteDocument(): void {
    this.error = null;
    this.documentApiService.delete(1).subscribe({
      next: (response) => {
        this.result = response;
      },
      error: (err) => {
        this.error = err;
      }
    });
  }

  filterDocuments(): void {
    this.error = null;
    this.documentApiService.filter({
      name: 'invoice',
      page: 0,
      size: 5,
      sortBy: 'id',
      sortDirection: 'desc'
    }).subscribe({
      next: (response) => {
        this.result = response;
      },
      error: (err) => {
        this.error = err;
      }
    });
  }
}