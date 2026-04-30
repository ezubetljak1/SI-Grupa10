import { Component, Input } from '@angular/core';

type FileIconVariant = 'pdf' | 'image' | 'default';

@Component({
  selector: 'app-file-type-icon',
  standalone: true,
  templateUrl: './file-type-icon.html',
  styleUrl: './file-type-icon.scss'
})
export class FileTypeIconComponent {
  @Input() mimeType = '';
  @Input() fileName = '';

  get label(): string {
    const extension = this.extension;

    if (extension === 'pdf' || this.mimeType === 'application/pdf') {
      return 'PDF';
    }

    if (['jpg', 'jpeg'].includes(extension) || this.mimeType === 'image/jpeg') {
      return 'JPG';
    }

    if (extension === 'png' || this.mimeType === 'image/png') {
      return 'PNG';
    }

    return 'FILE';
  }

  get variant(): FileIconVariant {
    if (this.label === 'PDF') {
      return 'pdf';
    }

    if (['JPG', 'PNG'].includes(this.label)) {
      return 'image';
    }

    return 'default';
  }

  private get extension(): string {
    const fileNameParts = this.fileName?.split('.') ?? [];

    if (fileNameParts.length < 2) {
      return '';
    }

    return fileNameParts[fileNameParts.length - 1].toLowerCase();
  }
}