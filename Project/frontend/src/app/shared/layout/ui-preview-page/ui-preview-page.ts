import { Component } from '@angular/core';
import { PageHeaderComponent } from '../../components/page-header/page-header';
import { UiCardComponent } from '../../components/ui-card/ui-card';
import { StatusBadgeComponent } from '../../components/status-badge/status-badge';
import { FileTypeIconComponent } from '../../components/file-type-icon/file-type-icon';
import { EmptyStateComponent } from '../../components/empty-state/empty-state';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-ui-preview-page',
  standalone: true,
  imports: [
    PageHeaderComponent,
    UiCardComponent,
    StatusBadgeComponent,
    FileTypeIconComponent,
    EmptyStateComponent
  ],
  templateUrl: './ui-preview-page.html',
  styleUrl: './ui-preview-page.scss'
})
export class UiPreviewPageComponent {
  constructor(private toastr: ToastrService) {}

  showSuccessToast(): void {
    this.toastr.success('Document uploaded successfully.', 'Success');
  }

  showErrorToast(): void {
    this.toastr.error('Only PDF, JPG, JPEG and PNG files are supported.', 'Upload failed');
  }

  showWarningToast(): void {
    this.toastr.warning('Please select a file before uploading.', 'Missing file');
  }

  showInfoToast(): void {
    this.toastr.info('Document metadata loaded successfully.', 'Info');
  }
}