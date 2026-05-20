import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';

import { CompanyRegisterRequest, CompanyRegisterResponse } from '../../../companies/models/company.models';
import { CompanyApiService } from '../../../companies/services/company-api.service';
import { AuthService } from '../../services/auth.service';
import { extractApiErrorMessage } from '../../../models/api.models';

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
    const validationErrors = this.validateForm();

    if (validationErrors.length > 0) {
      this.toastr.warning(validationErrors.join('\n'), 'Validation');
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
        this.toastr.error(
          extractApiErrorMessage(error.error, 'Company registration failed.'),
          'Registration failed'
        );
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

  private validateForm(): string[] {
    const errors: string[] = [];

    if (!this.form.companyName.trim()) {
      errors.push('Company name is required.');
    }

    if (!this.form.companyAddress.trim()) {
      errors.push('Company address is required.');
    }

    if (!this.form.companyEmail.trim()) {
      errors.push('Company email is required.');
    }

    if (!this.form.adminFirstName.trim()) {
      errors.push('Admin first name is required.');
    }

    if (!this.form.adminLastName.trim()) {
      errors.push('Admin last name is required.');
    }

    if (!this.form.adminEmail.trim()) {
      errors.push('Admin email is required.');
    }

    if (
      this.form.companyEmail.trim() &&
      !this.isValidEmail(this.form.companyEmail.trim())
    ) {
      errors.push('Company email must be a valid email address.');
    }

    if (
      this.form.adminEmail.trim() &&
      !this.isValidEmail(this.form.adminEmail.trim())
    ) {
      errors.push('Admin email must be a valid email address.');
    }

    return errors;
  }

  private isValidEmail(value: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);
  }
}
