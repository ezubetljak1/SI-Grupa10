import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

import { CompanyRegisterRequest, CompanyRegisterResponse } from '../../../companies/models/company.models';
import { CompanyApiService } from '../../../companies/services/company-api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register-company-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register-company-page.html',
  styleUrl: './register-company-page.scss',
})
export class RegisterCompanyPageComponent {
  private readonly companyApi = inject(CompanyApiService);
  private readonly toastr = inject(ToastrService);
  readonly authService = inject(AuthService);

  readonly form: CompanyRegisterRequest = {
    companyName: '',
    companyAddress: '',
    companyEmail: '',
    adminFirstName: '',
    adminLastName: '',
    adminEmail: '',
  };

  submitting = false;
  result: CompanyRegisterResponse | null = null;

  submit(): void {
    if (!this.isFormValid()) {
      this.toastr.warning('Fill all required company and admin fields.', 'Validation');
      return;
    }

    this.submitting = true;
    this.result = null;

    this.companyApi.registerCompany(this.trimmedPayload()).subscribe({
      next: (response) => {
        this.submitting = false;
        this.result = response.payload;
        this.toastr.success('Company registered successfully.', 'Success');
      },
      error: (error: HttpErrorResponse) => {
        this.submitting = false;
        this.toastr.error(this.extractErrorMessage(error.error), 'Registration failed');
      },
    });
  }

  login(): void {
    void this.authService.login('/documents');
  }

  private isFormValid(): boolean {
    return Object.values(this.form).every((value) => value.trim().length > 0);
  }

  private trimmedPayload(): CompanyRegisterRequest {
    return {
      companyName: this.form.companyName.trim(),
      companyAddress: this.form.companyAddress.trim(),
      companyEmail: this.form.companyEmail.trim(),
      adminFirstName: this.form.adminFirstName.trim(),
      adminLastName: this.form.adminLastName.trim(),
      adminEmail: this.form.adminEmail.trim(),
    };
  }

  private extractErrorMessage(errorBody: unknown): string {
    if (typeof errorBody === 'string') {
      return errorBody;
    }

    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as { message?: string; payload?: string; code?: string };
      return body.message ?? body.payload ?? body.code ?? 'Company registration failed.';
    }

    return 'Company registration failed.';
  }
}
