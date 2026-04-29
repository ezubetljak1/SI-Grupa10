import { Routes } from '@angular/router';
import { DocumentsPageComponent } from './documents/documents-page/documents-page';
import { DocumentUploadPageComponent } from './documents/document-upload-page/document-upload-page';
import { UiPreviewPageComponent } from './shared/layout/ui-preview-page/ui-preview-page';
import { DocumentDetailPageComponent } from './documents/document-detail-page/document-detail-page';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'documents',
        pathMatch: 'full'
    },
    {
        path: 'documents/upload',
        component: DocumentUploadPageComponent
    },
    {
        path: 'documents',
        component: DocumentsPageComponent
    },
    {
        path: 'ui-preview',
        component: UiPreviewPageComponent
    },
    {
        path: 'documents/:id',
        component: DocumentDetailPageComponent
    }
];
