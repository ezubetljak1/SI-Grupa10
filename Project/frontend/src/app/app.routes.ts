import { Routes } from '@angular/router';
import { DocumentsPageComponent } from './documents/documents-page/documents-page';
import { DocumentUploadPageComponent } from './documents/document-upload-page/document-upload-page';
import { UiPreviewPageComponent } from './shared/layout/ui-preview-page/ui-preview-page';
import { DocumentDetailPageComponent } from './documents/document-detail-page/document-detail-page';
import { DashboardPage } from './dashboard/pages/dashboard-page/dashboard-page';
import { RegisterCompanyPageComponent } from './auth/pages/register-company-page/register-company-page';
import { AuthCallbackPageComponent } from './auth/pages/auth-callback-page/auth-callback-page';
import { UsersPageComponent } from './users/pages/users-page/users-page';
import { authGuard } from './auth/services/auth.guard';
import { roleGuard } from './auth/services/role.guard';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'register-company',
        pathMatch: 'full'
    },
    {
        path: 'register-company',
        component: RegisterCompanyPageComponent
    },
    {
        path: 'auth/callback',
        component: AuthCallbackPageComponent
    },
    {
        path: 'dashboard',
        component: DashboardPage,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'MANAGER'] }
    },
    {
        path: 'documents/upload',
        component: DocumentUploadPageComponent,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN', 'OPERATOR'] }
    },
    {
        path: 'documents',
        component: DocumentsPageComponent,
        canActivate: [authGuard]
    },
    {
        path: 'ui-preview',
        component: UiPreviewPageComponent
    },
    {
        path: 'documents/:id',
        component: DocumentDetailPageComponent,
        canActivate: [authGuard]
    },
    {
        path: 'company/users',
        component: UsersPageComponent,
        canActivate: [authGuard, roleGuard],
        data: { roles: ['ADMIN'] }
    }
];
