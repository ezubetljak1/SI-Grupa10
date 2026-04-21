import { Routes } from '@angular/router';
import { DocumentsPageComponent } from './documents/documents-page/documents-page';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'documents',
        pathMatch: 'full'
    },
    {
        path: 'documents',
        component: DocumentsPageComponent
    }
];
