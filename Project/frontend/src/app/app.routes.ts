import { Routes } from '@angular/router';
import { DocumentsPageComponent } from './documents/documents-page/documents-page';
import { UiPreviewPageComponent } from './shared/layout/ui-preview-page/ui-preview-page';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'documents',
        pathMatch: 'full'
    },
    {
        path: 'documents',
        component: DocumentsPageComponent
    },
    {
        path: 'ui-preview',
        component: UiPreviewPageComponent
    }
];
