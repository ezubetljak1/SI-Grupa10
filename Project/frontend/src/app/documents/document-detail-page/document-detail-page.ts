import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { AuditLog } from '../models/audit.models';
import { DocumentApiService} from '../../services/document-api.service';
import {
  FileTypeIconComponent,
  PageHeaderComponent,
  StatusBadgeComponent,
  UiCardComponent,
} from '../../shared/components';
import {
  DOCUMENT_TYPE_OPTIONS,
  DocflowDocument,
  MANUAL_CLASSIFICATION_DOCUMENT_TYPES,
  ManualClassificationDocumentType,
} from '../models/document.models';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ToastrService } from 'ngx-toastr';
import { Extraction, ExtractionField } from '../models/extraction.models';
import { DocumentComment, StatusHistoryEntry } from '../models/workflow.models';
import { AuthService } from '../../auth/services/auth.service';
import { UserApiService } from '../../users/services/user-api.service';
import { UserResponse } from '../../users/models/user.models';
import { TaskApiService } from '../../tasks/services/task-api.service';
import { AssignTaskRequest, TaskResponse, TaskType } from '../../tasks/models/task.models';

interface EditState {
  fieldId: number;
  editValue: string;
  saving: boolean;
  validationError: string | null;
}

interface BackendValidationError {
  code?: string;
  message?: string;
  payload?: string;
}

@Component({
  selector: 'app-document-detail-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    PageHeaderComponent,
    UiCardComponent,
    FileTypeIconComponent,
    StatusBadgeComponent,
  ],
  templateUrl: './document-detail-page.html',
  styleUrl: './document-detail-page.scss',
})
export class DocumentDetailPageComponent implements OnInit {
  private readonly documentApiService = inject(DocumentApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly toastr = inject(ToastrService);
  private readonly authService = inject(AuthService);
  private readonly userApiService = inject(UserApiService);
  private readonly taskApiService = inject(TaskApiService);

  document: DocflowDocument | null = null;
  loading = false;
  downloading = false;

  private readonly sanitizer = inject(DomSanitizer);

  fileUrl: SafeResourceUrl | null = null;
  private previewObjectUrl: string | undefined;

  isPdf = false;
  isImage = false;

  extractionFields: ExtractionField[] = [];
  extractionLoading = false;
  extractionRunning = false;
  extractionError: string | null = null;

  extractionId: number | null = null;
  editState: EditState | null = null;
  confirmingExtraction = false;

  readonly documentTypeOptions = DOCUMENT_TYPE_OPTIONS;
  readonly manualClassificationTypeOptions = MANUAL_CLASSIFICATION_DOCUMENT_TYPES;

  selectedManualDocumentType: ManualClassificationDocumentType = 'FORM';
  confirmingDocumentType = false;

  statusHistory: StatusHistoryEntry[] = [];
  statusHistoryLoading = false;
  statusHistoryError: string | null = null;

  documentComments: DocumentComment[] = [];
  commentsLoading = false;
  commentsError: string | null = null;
  newCommentContent = '';
  submittingComment = false;
  approvalComment = '';
  approvalSubmitting = false;
  auditLogs: AuditLog[] = [];
  auditLoading = false;
  auditError: string | null = null;
  auditExpanded = false;
  tasks: TaskResponse[] = [];
  tasksLoading = false;
  taskError: string | null = null;
  duplicateTaskAssignmentAttempted = false;
  assignableUsers: UserResponse[] = [];
  assignTaskUserId: number | null = null;
  assignTaskType: TaskType = 'CORRECTION';
  assignTaskDueDate = '';
  assigningTask = false;
  readonly minDueDate = this.toDatetimeLocalValue(new Date());

  readonly allTaskTypeOptions: { value: TaskType; label: string }[] = [
    { value: 'EXTRACTION', label: 'Extraction' },
    { value: 'CORRECTION', label: 'Correction' },
    { value: 'APPROVAL', label: 'Approval' },
  ];

  get canManageExtraction(): boolean {
    return this.authService.hasRole(['ADMIN', 'OPERATOR']);
  }

  get canApproveDocuments(): boolean {
    return this.authService.hasRole(['ADMIN', 'MANAGER', 'APPROVER']);
  }

  get canViewAudit(): boolean {
    return this.authService.hasRole(['ADMIN', 'MANAGER']);
  }

  get canAssignTasks(): boolean {
    return this.authService.hasRole(['ADMIN', 'MANAGER']);
  }

  Tasks(): TaskResponse[] {
    return this.tasks.filter((task) => task.status === 'OPEN' || task.status === 'IN_PROGRESS');
  }

  get activeTasks(): TaskResponse[] {
    return this.tasks.filter(
      (task) => task.status === 'OPEN' || task.status === 'IN_PROGRESS'
    );
  }

  get activeTask(): TaskResponse | null {
    return this.activeTasks[0] ?? null;
  }

  get currentUserId(): number | null {
    return this.authService.profile?.id ?? null;
  }

 get workflowRelevantTaskType(): TaskType | null {
    switch (this.document?.documentStatus) {
      case 'UPLOADED':
      case 'PROCESSING_FAILED':
      case 'NEEDS_CLASSIFICATION_REVIEW':
        return 'EXTRACTION';

      case 'EXTRACTED':
      case 'NEEDS_CORRECTION':
        return 'CORRECTION';

      case 'READY_FOR_APPROVAL':
        return 'APPROVAL';

      default:
        return null;
    }
  }

  get activeTaskOfSelectedType(): TaskResponse | null {
    return (
      this.activeTasks.find((task) => task.taskType === this.assignTaskType) ??
      null
    );
  }

  get blockingTaskForCurrentStatus(): TaskResponse | null {
    const taskType = this.workflowRelevantTaskType;

    if (!taskType) {
      return null;
    }

    return (
      this.activeTasks.find((task) => task.taskType === taskType) ??
      null
    );
  }

  get workflowBannerTask(): TaskResponse | null {
    return this.blockingTaskForCurrentStatus ?? this.activeTask;
  }

  get activeTaskAssignedToAnotherUser(): boolean {
    const blockingTask = this.blockingTaskForCurrentStatus;

    return (
      !!blockingTask &&
      !!this.currentUserId &&
      !this.canAssignTasks &&
      blockingTask.assignedUserId !== this.currentUserId
    );
  }

  get showAssignedToAnotherUserWarning(): boolean {
    return this.activeTaskAssignedToAnotherUser;
  }

  get canUseExtractionActions(): boolean {
    return this.canManageExtraction && !this.activeTaskAssignedToAnotherUser;
  }

  get canUseApprovalActions(): boolean {
    return this.canApproveDocuments && !this.activeTaskAssignedToAnotherUser;
  }

  get eligibleAssignees(): UserResponse[] {
    const allowedRoles =
      this.assignTaskType === 'APPROVAL'
        ? ['APPROVER', 'MANAGER']
        : ['OPERATOR'];

    return this.assignableUsers.filter(
      (user) => allowedRoles.includes(user.role) && user.accountStatus !== 'INACTIVE'
    );
  }

 get taskTypeOptions(): { value: TaskType; label: string }[] {
    switch (this.document?.documentStatus) {
      case 'UPLOADED':
      case 'PROCESSING_FAILED':
      case 'NEEDS_CLASSIFICATION_REVIEW':
        return this.allTaskTypeOptions.filter((type) =>
          ['EXTRACTION', 'CORRECTION', 'APPROVAL'].includes(type.value)
        );

      case 'EXTRACTED':
      case 'NEEDS_CORRECTION':
        return this.allTaskTypeOptions.filter((type) =>
          ['CORRECTION', 'APPROVAL'].includes(type.value)
        );

      case 'READY_FOR_APPROVAL':
        return this.allTaskTypeOptions.filter((type) => type.value === 'APPROVAL');

      default:
        return [];
    }
  }

  get canAssignSelectedTaskType(): boolean {
    return this.taskTypeOptions.some((type) => type.value === this.assignTaskType);
  }

  get canAssignTaskForCurrentStatus(): boolean {
    return this.canAssignTasks && this.taskTypeOptions.length > 0;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const id = Number(params.get('id'));
      if (!Number.isFinite(id) || id <= 0) {
        this.toastr.error('Invalid document id.', 'Error');
        return;
      }
      this.resetDocumentState();
      this.loadDocument(id);
    });
  }

  loadDocument(id: number): void {
    this.loading = true;
    this.documentApiService.getById(id).subscribe({
      next: (response) => {
        this.loading = false;
        this.document = response.payload;
        this.syncAssignmentDefaultsForCurrentStatus();
        this.selectedManualDocumentType = this.resolveDefaultManualDocumentType(this.document);
        // Fetch preview as a blob so Authorization header is included by interceptor
        // then create an object URL for the iframe/img src. This avoids 401 on iframe
        // navigations and X-Frame-Options issues.
        this.isPdf = this.document.fileType === 'application/pdf';
        this.isImage = this.document.fileType?.startsWith('image/');

        // revoke previously created object URL if present
        if (this.previewObjectUrl) {
          try {
            window.URL.revokeObjectURL(this.previewObjectUrl);
          } catch {}
          this.previewObjectUrl = undefined;
        }

        this.documentApiService.getPreview(this.document.id).subscribe({
          next: (blob) => {
            this.previewObjectUrl = window.URL.createObjectURL(blob);
            this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.previewObjectUrl);
          },
          error: () => {
            this.fileUrl = null;
            this.toastr.error('Failed to load document preview.', 'Error');
          },
        });
        this.extractionError = null;
        this.extractionFields = [];
        if (this.shouldLoadExtractionForStatus(this.document.documentStatus)) {
          this.loadExtraction();
        }

        this.loadWorkflowData(this.document.id);
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        const message = this.extractErrorMessage(err.error) ?? 'Failed to load document.';
        this.document = null;
        this.fileUrl = null;
        this.isPdf = false;
        this.isImage = false;
        this.toastr.error(message, 'Error');
      },
    });
  }

  confirmDocumentType(): void {
    if (!this.document) {
      return;
    }

    const documentId = this.document.id;
    this.confirmingDocumentType = true;

    this.documentApiService
      .confirmDocumentType(documentId, this.selectedManualDocumentType)
      .subscribe({
        next: (response) => {
          this.confirmingDocumentType = false;
          this.document = response.payload;
          this.extractionError = null;
          this.toastr.success(
            'Document type confirmed. You can run extraction again.',
            'Classification confirmed'
          );
          this.loadDocument(documentId);
        },
        error: (err: HttpErrorResponse) => {
          this.confirmingDocumentType = false;
          const message =
            this.extractErrorMessage(err.error) ??
            'Document type confirmation failed.';
          this.toastr.error(message, 'Error');
        },
      });
  }

  loadExtraction(): void {
    if (!this.document) return;
    this.extractionLoading = true;
    this.extractionError = null;
    this.documentApiService.getExtraction(this.document.id).subscribe({
      next: (response) => {
        const extraction: Extraction = response.payload;
        this.extractionId = extraction?.id ?? null;
        this.loadExtractionFields();
      },
      error: () => {
        this.loadExtractionFields();
      },
    });
  }

  loadExtractionFields(): void {
    if (!this.document) return;
    this.extractionLoading = true;
    this.extractionError = null;
    this.documentApiService.getExtractionFields(this.document.id).subscribe({
      next: (response) => {
        this.extractionLoading = false;
        this.extractionFields = response.payload ?? [];
      },
      error: (err: HttpErrorResponse) => {
        this.extractionLoading = false;
        this.extractionFields = [];
        this.extractionError =
          this.extractErrorMessage(err.error) ?? 'Failed to load extracted fields.';
      },
    });
  }

  runExtraction(): void {
    if (!this.document) return;
    const documentId = this.document.id;
    this.extractionRunning = true;
    this.extractionError = null;
    this.documentApiService.processExtraction(this.document.id).subscribe({
      next: (response) => {
        this.extractionRunning = false;
        this.extractionId = response.payload?.id ?? null;
        this.extractionFields = response.payload?.fields ?? [];
        this.toastr.success('Extraction completed.', 'Success');
        this.loadDocument(documentId);
      },
      error: (err: HttpErrorResponse) => {
        this.extractionRunning = false;
        const message = this.extractExtractionErrorMessage(err.error, 'Extraction failed.');
        this.extractionError = message;
        this.extractionFields = [];
        this.toastr.error(message, 'Error');
        this.loadDocument(documentId);
      },
    });
  }

  retryExtraction(): void {
    if (!this.document) return;
    const documentId = this.document.id;
    this.extractionRunning = true;
    this.extractionError = null;
    this.documentApiService.retryExtraction(this.document.id).subscribe({
      next: (response) => {
        this.extractionRunning = false;
        this.extractionId = response.payload?.id ?? null;
        this.extractionFields = response.payload?.fields ?? [];
        this.toastr.success('Extraction retried.', 'Success');
        this.loadDocument(documentId);
      },
      error: (err: HttpErrorResponse) => {
        this.extractionRunning = false;
        const message = this.extractExtractionErrorMessage(err.error, 'Retry extraction failed.');
        this.extractionError = message;
        this.extractionFields = [];
        this.toastr.error(message, 'Error');
        this.loadDocument(documentId);
      },
    });
  }

  startEdit(field: ExtractionField): void {
    if (this.editState && this.editState.fieldId !== field.id) {
      this.cancelEdit();
    }
    this.editState = {
      fieldId: field.id,
      editValue: field.value ?? '',
      saving: false,
      validationError: null,
    };
  }

  cancelEdit(): void {
    this.editState = null;
  }

  isEditing(field: ExtractionField): boolean {
    return this.editState?.fieldId === field.id;
  }

  validateEditValue(field: ExtractionField, value: string): string | null {
    const trimmed = value.trim();

    if (trimmed === '') {
      return 'Field value cannot be empty.';
    }

    const name = field.fieldName.toLowerCase();

    if (name.includes('date') || name.includes('datum')) {
      if (!this.isValidDateValue(trimmed)) {
        return 'Invalid date format. Supported formats are ISO YYYY-MM-DD or European DD.MM.YYYY / DD/MM/YYYY. US MM/DD/YYYY format is not supported.';
      }
    }

    if (
      name.includes('amount') ||
      name.includes('balance') ||
      name.includes('deposit') ||
      name.includes('withdrawal') ||
      name.includes('iznos') ||
      name.includes('cijena') ||
      name.endsWith('_price') ||
      name.endsWith('_quantity') ||
      [
        'net_amount',
        'vat_amount',
        'total_amount',
        'total_tax_amount',
        'tax_amount',
        'subtotal_amount',
        'amount',
        'price',
        'unit_price',
        'quantity',
        'qty',
        'starting_balance',
        'ending_balance',
        'opening_balance',
        'closing_balance',
        'current_balance',
        'transaction_deposit',
        'transaction_withdrawal',
        'transaction_amount',
        'table_item/transaction_deposit',
        'table_item/transaction_withdrawal',
        'table_item/transaction_amount',
      ].includes(name)
    ) {
      if (!this.isValidNumericValue(trimmed)) {
        return 'Field must be a numeric value only, without currency symbols or additional text. Use for example 1500, 1500.50 or 1500,50.';
      }
    }
    return null;
  }

  isClassificationReviewRequired(): boolean {
    return this.document?.documentStatus === 'NEEDS_CLASSIFICATION_REVIEW';
  }

  formatDocumentTypeLabel(documentType: string | null | undefined): string {
    if (!documentType) {
      return '—';
    }

    return (
      this.documentTypeOptions.find((type) => type.value === documentType)?.label ??
      documentType.replaceAll('_', ' ')
    );
  }

  formatClassificationConfidence(confidence: number | null | undefined): string {
    if (confidence === null || confidence === undefined) {
      return '—';
    }

    const normalized = confidence <= 1 ? confidence * 100 : confidence;
    return `${Math.round(normalized)}%`;
  }

  formatProcessorLabel(document: DocflowDocument | null | undefined): string {
    if (!document?.processorIdUsed) {
      return '—';
    }

    if (document.documentStatus === 'NEEDS_CLASSIFICATION_REVIEW') {
      return 'AI classifier';
    }

    const typeForProcessor = document.documentType ?? document.detectedDocumentType;

    switch (typeForProcessor) {
      case 'INVOICE':
        return 'Invoice parser';
      case 'RECEIPT':
        return 'Expense parser';
      case 'BANK_STATEMENT':
        return 'Bank statement parser';
      case 'FORM':
        return 'Form parser';
      case 'OTHER':
        return 'AI classifier';
      default:
        return 'Document AI processor';
    }
  }

  private resolveDefaultManualDocumentType(
    document: DocflowDocument
  ): ManualClassificationDocumentType {
    const detectedType = document.detectedDocumentType;

    if (
      detectedType === 'INVOICE' ||
      detectedType === 'RECEIPT' ||
      detectedType === 'BANK_STATEMENT' ||
      detectedType === 'FORM'
    ) {
      return detectedType;
    }

    return 'FORM';
  }

  private isValidDateValue(value: string): boolean {
    const matchers = [
      {
        pattern: /^(\d{4})-(\d{2})-(\d{2})$/,
        parts: (match: RegExpMatchArray) => [Number(match[1]), Number(match[2]), Number(match[3])],
      },
      {
        pattern: /^(\d{2})\.(\d{2})\.(\d{4})$/,
        parts: (match: RegExpMatchArray) => [Number(match[3]), Number(match[2]), Number(match[1])],
      },
      {
        pattern: /^(\d{2})\/(\d{2})\/(\d{4})$/,
        parts: (match: RegExpMatchArray) => [Number(match[3]), Number(match[2]), Number(match[1])],
      },
    ];

    for (const matcher of matchers) {
      const match = value.match(matcher.pattern);
      if (!match) {
        continue;
      }

      const [year, month, day] = matcher.parts(match);
      const parsed = new Date(Date.UTC(year, month - 1, day));
      return (
        parsed.getUTCFullYear() === year &&
        parsed.getUTCMonth() === month - 1 &&
        parsed.getUTCDate() === day
      );
    }

    return false;
  }

  private isValidNumericValue(value: string): boolean {
    if (value.includes(' ') || value.includes('\t') || (value.includes(',') && value.includes('.'))) {
      return false;
    }

    return /^\d+([.,]\d{1,2})?$/.test(value);
  }

  confirmEdit(field: ExtractionField): void {
    if (!this.editState || this.editState.fieldId !== field.id) return;

    const value = this.editState.editValue;
    const validationError = this.validateEditValue(field, value);

    if (validationError) {
      this.editState.validationError = validationError;
      return;
    }

    if (!this.extractionId) {
      this.toastr.error('Extraction ID not available. Please refresh the page.', 'Error');
      return;
    }

    this.editState.saving = true;
    this.editState.validationError = null;

    this.documentApiService.updateExtractionField(this.extractionId, field.id, value).subscribe({
      next: (response) => {
        const updated = response.payload;
        const idx = this.extractionFields.findIndex((f) => f.id === field.id);
        if (idx !== -1) {
          this.extractionFields[idx] = {
            ...this.extractionFields[idx],
            value: updated.value,
            corrected: updated.corrected,
            placeholder: updated.placeholder,
          };
        }
        this.editState = null;
        this.toastr.success('Changes saved successfully.', 'Success');
      },
      error: (err: HttpErrorResponse) => {
        if (this.editState) {
          this.editState.saving = false;
          this.editState.validationError =
            this.extractErrorMessage(err.error) ?? 'Failed to save. Please try again.';
        }
      },
    });
  }

  formatConfidence(confidence: number | null | undefined): string {
    if (confidence === null || confidence === undefined) return '—';
    if (!Number.isFinite(confidence)) return '—';
    const normalized = confidence <= 1 ? confidence * 100 : confidence;
    return `${Math.round(normalized)}%`;
  }

  downloadDocument(): void {
    if (!this.document) return;
    this.downloading = true;
    this.documentApiService.downloadFile(this.document.id).subscribe({
      next: (blob) => {
        this.downloading = false;
        const objectUrl = window.URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = objectUrl;
        anchor.download = this.resolveDownloadFileName(this.document!);
        anchor.click();
        window.URL.revokeObjectURL(objectUrl);
      },
      error: (err: HttpErrorResponse) => {
        this.downloading = false;
        const message = this.extractErrorMessage(err.error) ?? 'Failed to download file.';
        this.toastr.error(message, 'Error');
      },
    });
  }

  confirmExtraction(): void {
    if (!this.document) return;

    if (this.editState) {
      this.toastr.warning(
        'Save or cancel the current field edit before confirming extraction.',
        'Review required'
      );
      return;
    }

    const documentId = this.document.id;
    this.confirmingExtraction = true;
    this.extractionError = null;

    this.documentApiService.confirmExtraction(documentId).subscribe({
      next: (response) => {
        this.confirmingExtraction = false;
        this.extractionId = response.payload?.id ?? this.extractionId;
        this.extractionFields = response.payload?.fields ?? this.extractionFields;

        this.toastr.success(
          'Extraction confirmed. Document is ready for approval.',
          'Success'
        );

        this.loadDocument(documentId);
      },
      error: (err: HttpErrorResponse) => {
        this.confirmingExtraction = false;

        if (this.hasValidationErrors(err.error)) {
          const message = this.buildExtractionValidationMessage(
            err.error,
            'Review highlighted fields before confirming.'
          );

          this.extractionError = null;
          this.toastr.warning(message, 'Review required');
          return;
        }

        const message = this.extractExtractionErrorMessage(
          err.error,
          'Extraction confirmation failed.'
        );

        this.extractionError = message;
        this.toastr.error(message, 'Error');
      },
    });
  }

  formatFileSize(sizeInBytes: number): string {
    if (sizeInBytes < 1024) return `${sizeInBytes} B`;
    const kb = sizeInBytes / 1024;
    if (kb < 1024) return `${kb.toFixed(2)} KB`;
    return `${(kb / 1024).toFixed(2)} MB`;
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  loadWorkflowData(documentId: number): void {
    this.loadStatusHistory(documentId);
    this.loadComments(documentId);
    this.loadAuditLogs(documentId);
    this.loadTasks(documentId);
    this.loadAssignableUsers();
  }

  loadStatusHistory(documentId: number): void {
    this.statusHistoryLoading = true;
    this.statusHistoryError = null;

    this.documentApiService.getStatusHistory(documentId).subscribe({
      next: (response) => {
        this.statusHistoryLoading = false;
        this.statusHistory = response.payload ?? [];
      },
      error: (err: HttpErrorResponse) => {
        this.statusHistoryLoading = false;
        this.statusHistory = [];
        this.statusHistoryError =
          this.extractErrorMessage(err.error) ?? 'Failed to load status history.';
      },
    });
  }

  loadComments(documentId: number): void {
    this.commentsLoading = true;
    this.commentsError = null;

    this.documentApiService.getComments(documentId).subscribe({
      next: (response) => {
        this.commentsLoading = false;
        this.documentComments = response.payload ?? [];
      },
      error: (err: HttpErrorResponse) => {
        this.commentsLoading = false;
        this.documentComments = [];
        this.commentsError =
          this.extractErrorMessage(err.error) ?? 'Failed to load comments.';
      },
    });
  }

  loadAuditLogs(documentId: number): void {
    if (!this.canViewAudit) {
      return;
    }

    this.auditLoading = true;

    this.documentApiService.getAuditLog(documentId).subscribe({
      next: (response) => {
        this.auditLoading = false;
        this.auditLogs = response.payload ?? [];
      },
      error: (err) => {
        this.auditLoading = false;
        this.auditError =
          this.extractErrorMessage(err.error) ?? 'Failed to load audit log.';
      }
    });
  }

  loadTasks(documentId: number): void {
    this.tasksLoading = true;
    this.taskError = null;

    this.taskApiService.getByDocument(documentId).subscribe({
      next: (response) => {
        this.tasksLoading = false;
        this.tasks = response.payload ?? [];
        this.duplicateTaskAssignmentAttempted = false;
      },
      error: (err: HttpErrorResponse) => {
        this.tasksLoading = false;
        this.tasks = [];
        this.taskError = this.extractErrorMessage(err.error) ?? 'Failed to load document tasks.';
      },
    });
  }

  loadAssignableUsers(): void {
    if (!this.canAssignTasks || this.assignableUsers.length > 0) {
      return;
    }

    this.userApiService.list({ page: 0, size: 100 }).subscribe({
      next: (response) => {
        this.assignableUsers = response.payload ?? [];
        this.syncDefaultAssignee();
      },
      error: () => {
        this.assignableUsers = [];
      },
    });
  }

  assignTask(): void {
    if (!this.document || !this.assignTaskUserId) {
      this.toastr.warning('Choose a user before assigning the task.', 'Assignee required');
      return;
    }

    const existingActiveTaskOfSameType = this.activeTaskOfSelectedType;

    if (existingActiveTaskOfSameType) {
      this.duplicateTaskAssignmentAttempted = true;
      this.toastr.warning(
        `An active ${this.formatTaskType(this.assignTaskType)} task already exists for this document. Cancel it before assigning another one.`,
        'Duplicate task type'
      );
      return;
    }

    const payload: AssignTaskRequest = {
      assignedUserId: this.assignTaskUserId,
      taskType: this.assignTaskType,
      dueDate: this.assignTaskDueDate || null,
    };

    this.assigningTask = true;

    this.taskApiService.assign(this.document.id, payload).subscribe({
      next: () => {
        this.assigningTask = false;
        this.toastr.success('Task assigned.', 'Success');
        this.loadTasks(this.document!.id);
        this.loadAuditLogs(this.document!.id);
      },
      error: (err: HttpErrorResponse) => {
        this.assigningTask = false;
        if (this.extractErrorCode(err.error) === 'TASK_DUPLICATE_ACTIVE') {
          this.duplicateTaskAssignmentAttempted = true;
        }
        this.toastr.error(this.extractErrorMessage(err.error) ?? 'Failed to assign task.', 'Error');
      },
    });
  }

  cancelTask(task: TaskResponse): void {
    if (!this.document) {
      return;
    }

    this.taskApiService.cancel(task.id).subscribe({
      next: () => {
        this.toastr.success('Task cancelled.', 'Success');
        this.loadTasks(this.document!.id);
        this.loadAuditLogs(this.document!.id);
      },
      error: (err: HttpErrorResponse) => {
        this.toastr.error(this.extractErrorMessage(err.error) ?? 'Failed to cancel task.', 'Error');
      },
    });
  }

  onTaskTypeChange(): void {
    this.duplicateTaskAssignmentAttempted = false;
    this.syncDefaultAssignee();
  }

  formatTaskType(taskType: string): string {
    return taskType.replaceAll('_', ' ').toLowerCase();
  }

  isTaskOverdue(task: TaskResponse): boolean {
    return !!task.dueDate
      && (task.status === 'OPEN' || task.status === 'IN_PROGRESS')
      && new Date(task.dueDate).getTime() < Date.now();
  }

  approveDocument(): void {
    this.submitApprovalDecision('approve');
  }

  rejectDocument(): void {
    this.submitApprovalDecision('reject');
  }

  returnDocumentForCorrection(): void {
    this.submitApprovalDecision('correction');
  }

  submitComment(): void {
    if (!this.document || !this.newCommentContent.trim()) {
      this.toastr.warning('Enter a comment before submitting.', 'Comment required');
      return;
    }

    const documentId = this.document.id;
    this.submittingComment = true;

    this.documentApiService
      .createComment(documentId, { content: this.newCommentContent.trim() })
      .subscribe({
        next: () => {
          this.submittingComment = false;
          this.newCommentContent = '';
          this.toastr.success('Comment added.', 'Success');
          this.loadComments(documentId);
        },
        error: (err: HttpErrorResponse) => {
          this.submittingComment = false;
          const message =
            this.extractErrorMessage(err.error) ?? 'Failed to add comment.';
          this.toastr.error(message, 'Error');
        },
      });
  }

  private submitApprovalDecision(decision: 'approve' | 'reject' | 'correction'): void {
    if (!this.document) {
      return;
    }

    const content = this.approvalComment.trim();
    if (!content) {
      this.toastr.warning('Enter an approval comment before submitting.', 'Comment required');
      return;
    }

    const documentId = this.document.id;
    const payload = { content };
    const request =
      decision === 'approve'
        ? this.documentApiService.approveDocument(documentId, payload)
        : decision === 'reject'
          ? this.documentApiService.rejectDocument(documentId, payload)
          : this.documentApiService.returnDocumentForCorrection(documentId, payload);

    this.approvalSubmitting = true;

    request.subscribe({
      next: (response) => {
        this.approvalSubmitting = false;
        this.document = response.payload;
        this.approvalComment = '';
        this.toastr.success('Approval decision saved.', 'Success');
        this.loadTasks(documentId);
        this.loadStatusHistory(documentId);
        this.loadComments(documentId);
        this.loadAuditLogs(documentId);
      },
      error: (err: HttpErrorResponse) => {
        this.approvalSubmitting = false;
        this.toastr.error(
          this.extractErrorMessage(err.error) ?? 'Failed to save approval decision.',
          'Error'
        );
      },
    });
  }

  formatHistoryAction(action: string): string {
    const labels: Record<string, string> = {
      DOCUMENT_UPLOADED: 'Document uploaded',
      DOCUMENT_TYPE_CONFIRMED: 'Document type confirmed',
      EXTRACTION_COMPLETED: 'Extraction completed',
      EXTRACTION_FAILED: 'Extraction failed',
      EXTRACTION_CONFIRMED: 'Extraction confirmed',
      EXTRACTION_RECONFIRMED: 'Extraction reconfirmed',
      DOCUMENT_APPROVED: 'Document approved',
      DOCUMENT_REJECTED: 'Document rejected',
      DOCUMENT_RETURNED_FOR_CORRECTION: 'Returned for correction',
      SYSTEM_STATUS_CHANGE: 'Status updated',
    };

    return labels[action] ?? action.replaceAll('_', ' ').toLowerCase();
  }

  formatCommentType(type: string): string {
    const labels: Record<string, string> = {
      GENERAL: 'General',
      APPROVAL: 'Approval',
      REJECTION: 'Rejection',
      CORRECTION_REQUEST: 'Correction request',
      SYSTEM: 'System',
    };

    return labels[type] ?? type;
  }

  isPlaceholderField(field: ExtractionField): boolean {
    return field.placeholder === true;
  }

  isLowConfidenceField(field: ExtractionField): boolean {
    if (field.placeholder) return false;
    if (field.confidence === null || field.confidence === undefined) return false;

    const confidencePercent = field.confidence <= 1 ? field.confidence * 100 : field.confidence;
    return confidencePercent < 70;
  }

  needsManualReview(field: ExtractionField): boolean {
    return (
      this.isPlaceholderField(field) ||
      this.isDateFieldRequiringReview(field) ||
      (this.isLowConfidenceField(field) &&
        !field.corrected &&
        this.shouldLowConfidenceBlockConfirmation(field))
    );
  }

  get missingRequiredFields(): ExtractionField[] {
    return this.extractionFields.filter((field) => this.isPlaceholderField(field));
  }

  get lowConfidenceUnreviewedFields(): ExtractionField[] {
    return this.extractionFields.filter(
      (field) =>
        this.isLowConfidenceField(field) &&
        !field.corrected &&
        this.shouldLowConfidenceBlockConfirmation(field)
    );
  }

  private shouldLowConfidenceBlockConfirmation(field: ExtractionField): boolean {
    const documentType = this.document?.documentType;
    const fieldName = field.fieldName?.trim().toLowerCase() ?? '';

    if (documentType === 'INVOICE') {
      return true;
    }

    if (documentType === 'RECEIPT') {
      return (
        ['supplier_name', 'total_amount', 'currency'].includes(fieldName) ||
        ['receipt_date', 'expense_date', 'transaction_date', 'purchase_date'].includes(fieldName)
      );
    }

    if (documentType === 'BANK_STATEMENT') {
      return (
        ['account_number'].includes(fieldName) ||
        [
          'bank_name',
          'client_name',
          'account_holder_name',
          'customer_name',
          'statement_date',
          'statement_start_date',
          'statement_end_date',
          'starting_balance',
          'ending_balance',
          'opening_balance',
          'closing_balance',
          'current_balance',
          'table_item',
          'table_item/transaction_date',
          'table_item/transaction_deposit',
          'table_item/transaction_withdrawal',
          'table_item/transaction_amount',
          'transaction_date',
          'transaction_deposit',
          'transaction_withdrawal',
          'transaction_amount',
        ].includes(fieldName)
      );
    }

    return false;
  }

  get missingRequiredFieldNames(): string {
    return this.missingRequiredFields
      .map((field) => field.fieldName)
      .join(', ');
  }

  get lowConfidenceUnreviewedFieldNames(): string {
    return this.lowConfidenceUnreviewedFields
      .map((field) => field.fieldName)
      .join(', ');
  }

  hasPendingFieldIssues(): boolean {
    return this.missingRequiredFields.length > 0
      || this.lowConfidenceUnreviewedFields.length > 0;
  }

  getFieldReviewLabel(field: ExtractionField): string {
    if (this.isPlaceholderField(field)) {
      return 'Missing required';
    }

    if (this.isLowConfidenceField(field) && !field.corrected) {
      return 'Review needed';
    }

    if (field.corrected) {
      return 'Reviewed';
    }

    return 'OK';
  }

  getFieldReviewClass(field: ExtractionField): string {
    if (this.isPlaceholderField(field)) {
      return 'review-badge--missing';
    }

    if (
      (this.isLowConfidenceField(field) || this.isDateFieldRequiringReview(field)) &&
      !field.corrected
    ) {
      return 'review-badge--warning';
    }

    if (field.corrected) {
      return 'review-badge--reviewed';
    }

    return 'review-badge--ok';
  }

  displayFieldValue(field: ExtractionField): string {
    if (this.isPlaceholderField(field)) {
      return 'Missing value';
    }

    return field.value?.trim() || '—';
  }

  hasAiClassificationMetadata(): boolean {
    if (!this.document) {
      return false;
    }

    return (
      this.document.documentStatus === 'NEEDS_CLASSIFICATION_REVIEW' ||
      !!this.document.detectedDocumentType ||
      this.document.classificationConfidence !== null &&
        this.document.classificationConfidence !== undefined
    );
  }

  isDateField(field: ExtractionField): boolean {
    const name = field.fieldName.toLowerCase();
    return name.includes('date') || name.includes('datum');
  }

  getDateFormatHint(): string {
    return 'Supported format: DD.MM.YYYY or DD/MM/YYYY. Ambiguous US format MM/DD/YYYY is not supported.';
  }

  isDateFieldRequiringReview(field: ExtractionField): boolean {
    return (
      this.isDateField(field) &&
      this.isLowConfidenceField(field) &&
      !field.corrected &&
      this.shouldLowConfidenceBlockConfirmation(field)
    );
  }

  hasDateFields(): boolean {
    return this.extractionFields.some((field) => this.isDateField(field));
  }

  hasUnreviewedDateFields(): boolean {
    return this.extractionFields.some(
      (field) => this.isDateField(field) && !field.corrected
    );
  }

  formatAuditAction(action: string): string {
    const labels: Record<string, string> = {
      DOCUMENT_ASSIGNED: 'Document assigned',
      TASK_STARTED: 'Task started',
      TASK_COMPLETED: 'Task completed',
      TASK_CANCELLED: 'Task cancelled',

      FIELD_ADDED: 'Field added',
      FIELD_UPDATED: 'Field updated',

      DOCUMENT_APPROVED: 'Document approved',
      DOCUMENT_REJECTED: 'Document rejected',
      DOCUMENT_RETURNED_FOR_CORRECTION: 'Returned for correction',

      NOTIFICATION_CREATED: 'Notification created',
      NOTIFICATION_READ: 'Notification read',
      NOTIFICATIONS_READ_ALL: 'All notifications read',

      EMAIL_REMINDER_SENT: 'Email reminder sent',
      PERMISSION_DENIED: 'Permission denied',
      SYSTEM_ACTION: 'System action',
    };

    return labels[action] ?? this.toReadableText(action);
  }

  get latestAuditLog(): AuditLog | null {
  return this.auditLogs.length > 0 ? this.auditLogs[0] : null;
}

get visibleAuditLogs(): AuditLog[] {
  return this.auditExpanded ? this.auditLogs : this.auditLogs.slice(0, 3);
}

toggleAuditExpanded(): void {
  this.auditExpanded = !this.auditExpanded;
}

formatStatusTransition(entry: StatusHistoryEntry): string {
  if (!entry.oldStatus) {
    return entry.newStatus;
  }

  return `${entry.oldStatus} → ${entry.newStatus}`;
}

getStatusHistoryIcon(action: string): string {
  if (action.includes('UPLOADED')) return '↑';
  if (action.includes('EXTRACTION')) return '✎';
  if (action.includes('APPROVED')) return '✓';
  if (action.includes('REJECTED')) return '!';
  if (action.includes('RETURNED')) return '↩';
  if (action.includes('CONFIRMED')) return '✓';

  return '•';
}

getStatusHistoryClass(action: string): string {
  if (action.includes('APPROVED')) return 'workflow-event--success';
  if (action.includes('REJECTED')) return 'workflow-event--danger';
  if (action.includes('RETURNED')) return 'workflow-event--warning';
  if (action.includes('EXTRACTION')) return 'workflow-event--info';

  return 'workflow-event--neutral';
}

formatAuditDetails(entry: AuditLog): string {
  const details = entry.details;

  if (!details) {
    return 'No additional details.';
  }

  try {
    const parsed = JSON.parse(details);
    const taskType = parsed.taskType ? this.toTitleCase(this.formatTaskType(parsed.taskType)) : 'workflow';

    switch (entry.action) {
      case 'DOCUMENT_ASSIGNED': {
  const assignedUserName =
          parsed.assignedUserName || this.resolveAuditAssignedUserName(parsed.assignedUserId);

        return `Assigned ${taskType} task to ${assignedUserName}.`;
      }

      case 'TASK_STARTED':
        return `${taskType} task was started.`;

      case 'TASK_COMPLETED':
        return parsed.completedAutomatically
          ? `${taskType} task was completed automatically after the workflow action.`
          : `${taskType} task was completed.`;

      case 'TASK_CANCELLED':
        return `${taskType} task was cancelled.`;

      case 'FIELD_UPDATED':
        return parsed.fieldName
          ? `Updated extracted field: ${this.formatFieldName(parsed.fieldName)}.`
          : 'An extracted field was updated.';

      case 'DOCUMENT_APPROVED':
        return 'Document was moved to Approved.';

      case 'DOCUMENT_REJECTED':
        return 'Document was moved to Rejected.';

      case 'DOCUMENT_RETURNED_FOR_CORRECTION':
        return 'Document was returned to the operator for correction.';

      default:
        return Object.entries(parsed)
          .map(([key, value]) => `${this.formatFieldName(key)}: ${value}`)
          .join(' · ');
    }
  } catch {
    return details
      .replaceAll('taskId', 'Task ID')
      .replaceAll('taskType', 'Task type')
      .replaceAll('completedAutomatically', 'Completed automatically')
      .replaceAll('{', '')
      .replaceAll('}', '')
      .replaceAll('"', '');
  }
}

private toTitleCase(value: string): string {
  return value
    .split(' ')
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ');
}

  formatFieldName(value: string): string {
    return value
      .replace(/^custom\./, '')
      .split('_')
      .filter(Boolean)
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }

  getAuditIcon(action: string): string {
    if (action.includes('FIELD')) return '✎';
    if (action.includes('APPROVED')) return '✓';
    if (action.includes('REJECTED')) return '!';
    if (action.includes('RETURNED')) return '↩';
    if (action.includes('ASSIGNED') || action.includes('TASK')) return '→';
    if (action.includes('NOTIFICATION')) return '🔔';
    return '•';
  }

  getAuditActionClass(action: string): string {
    if (action.includes('APPROVED')) return 'audit-success';
    if (action.includes('REJECTED') || action.includes('DENIED')) return 'audit-danger';
    if (action.includes('RETURNED')) return 'audit-warning';
    if (action.includes('FIELD')) return 'audit-info';
    return 'audit-neutral';
  }

  private toReadableText(value: string): string {
    return value
      .toLowerCase()
      .split('_')
      .filter(Boolean)
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }

  private resolveDownloadFileName(doc: DocflowDocument): string {
    if (doc.name.includes('.')) return doc.name;
    const ext = this.resolveExtension(doc);
    return ext ? `${doc.name}.${ext}` : doc.name;
  }

  private resolveExtension(doc: DocflowDocument): string {
    if (doc.storagePath?.includes('.')) return doc.storagePath.split('.').pop() ?? '';
    if (doc.fileType === 'application/pdf') return 'pdf';
    if (doc.fileType === 'image/png') return 'png';
    if (doc.fileType === 'image/jpeg') return 'jpg';
    return '';
  }

  private extractErrorMessage(errorBody: unknown): string | null {
    if (Array.isArray(errorBody)) {
      const firstError = errorBody[0];
      if (firstError?.message) return firstError.message;
      if (firstError?.payload) return firstError.payload;
      if (firstError?.code) return firstError.code;
      return null;
    }
    if (typeof errorBody === 'string') return errorBody;
    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as {message?: string; payload?: string; code?: string};
      return body.message ?? body.payload ?? body.code ?? null;
    }
    return null;
  }

  private extractErrorCode(errorBody: unknown): string | null {
    if (Array.isArray(errorBody)) {
      const firstError = errorBody[0];
      return typeof firstError?.code === 'string' ? firstError.code : null;
    }

    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as {code?: string};
      if (typeof body.code === 'string') return body.code;
    }
    return null;
  }

  private extractExtractionErrorMessage(errorBody: unknown, fallback: string): string {
    const code = this.extractErrorCode(errorBody);
    if (code === 'DOCUMENT_CLASSIFICATION_REVIEW_REQUIRED') {
      return 'AI classification needs manual review. Confirm the correct document type before running extraction again.';
    }

    if (code === 'EXTRACTION_FAILED') {
      return 'Document extraction could not be completed. Please check OCR/AI setup or try again later.';
    }
    return this.extractErrorMessage(errorBody) ?? fallback;
  }

  private resetDocumentState(): void {
    this.document = null;
    this.fileUrl = null;
    this.isPdf = false;
    this.isImage = false;
    this.extractionFields = [];
    this.extractionId = null;
    this.extractionError = null;
    this.extractionLoading = false;
    this.extractionRunning = false;
    this.editState = null;
    this.confirmingExtraction = false;
    this.statusHistory = [];
    this.statusHistoryLoading = false;
    this.statusHistoryError = null;
    this.documentComments = [];
    this.commentsLoading = false;
    this.commentsError = null;
    this.newCommentContent = '';
    this.submittingComment = false;
    this.approvalComment = '';
    this.approvalSubmitting = false;
    this.tasks = [];
    this.tasksLoading = false;
    this.taskError = null;
    this.duplicateTaskAssignmentAttempted = false;
    this.assignTaskUserId = null;
    this.assignTaskType = 'CORRECTION';
    this.assignTaskDueDate = '';
    this.assigningTask = false;
  }

  private syncDefaultAssignee(): void {
    if (this.eligibleAssignees.some((user) => user.id === this.assignTaskUserId)) {
      return;
    }

    this.assignTaskUserId = this.eligibleAssignees[0]?.id ?? null;
  }

  private toDatetimeLocalValue(date: Date): string {
    const pad = (value: number) => value.toString().padStart(2, '0');

    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
  }

  private shouldLoadExtractionForStatus(status: string | null | undefined): boolean {
    return [
      'EXTRACTED',
      'READY_FOR_APPROVAL',
      'NEEDS_CORRECTION',
      'APPROVED',
      'REJECTED',
    ].includes(status ?? '');
  }


  shouldShowExtractionFields(): boolean {
    return [
      'EXTRACTED',
      'READY_FOR_APPROVAL',
      'NEEDS_CORRECTION',
      'APPROVED',
      'REJECTED',
    ].includes(this.document?.documentStatus ?? '');
  }

  canEditExtractionField(): boolean {
    return (
      this.canUseExtractionActions &&
      (this.document?.documentStatus === 'EXTRACTED' ||
        this.document?.documentStatus === 'NEEDS_CORRECTION')
    );
  }

  canConfirmCurrentExtraction(): boolean {
    return (
      this.canUseExtractionActions &&
      (this.document?.documentStatus === 'EXTRACTED' ||
        this.document?.documentStatus === 'NEEDS_CORRECTION')
    );
  }

  canRetryCurrentExtraction(): boolean {
    return (
      this.canUseExtractionActions &&
      (this.document?.documentStatus === 'EXTRACTED' ||
        this.document?.documentStatus === 'NEEDS_CORRECTION')
    );
  }

  private hasValidationErrors(errorBody: unknown): boolean {
    return this.extractValidationErrors(errorBody).length > 0;
  }

  private extractValidationErrors(errorBody: unknown): BackendValidationError[] {
    if (Array.isArray(errorBody)) {
      return errorBody
        .filter((item) => item && typeof item === 'object')
        .map((item) => item as BackendValidationError);
    }

    if (errorBody && typeof errorBody === 'object') {
      const body = errorBody as BackendValidationError;

      if (body.code || body.message || body.payload) {
        return [body];
      }
    }

    return [];
  }

  private buildExtractionValidationMessage(errorBody: unknown, fallback: string): string {
    const errors = this.extractValidationErrors(errorBody);

    if (errors.length === 0) {
      return this.extractExtractionErrorMessage(errorBody, fallback);
    }

    const codes = new Set(errors.map((error) => error.code).filter(Boolean));
    const parts: string[] = [];

    if (codes.has('EXTRACTION_REQUIRED_FIELD_MISSING')) {
      const fields = this.extractFieldNamesFromErrors(errors, 'EXTRACTION_REQUIRED_FIELD_MISSING');

      parts.push(
        fields.length > 0
          ? `Missing: ${fields.join(', ')}.`
          : 'Some required fields are missing.'
      );
    }

    if (codes.has('EXTRACTION_FIELD_EMPTY')) {
      const fields = this.extractFieldNamesFromErrors(errors, 'EXTRACTION_FIELD_EMPTY');

      parts.push(
        fields.length > 0
          ? `Empty: ${fields.join(', ')}.`
          : 'Some required fields are empty.'
      );
    }

    if (codes.has('EXTRACTION_FIELD_LOW_CONFIDENCE')) {
      const count = errors.filter(
        (error) => error.code === 'EXTRACTION_FIELD_LOW_CONFIDENCE'
      ).length;

      parts.push(
        count > 1
          ? `${count} low-confidence fields need review.`
          : 'One low-confidence field needs review.'
      );
    }

    if (codes.has('EXTRACTION_FIELD_REQUIRES_REVIEW')) {
      const count = errors.filter(
        (error) => error.code === 'EXTRACTION_FIELD_REQUIRES_REVIEW'
      ).length;

      parts.push(
        count > 1
          ? `${count} date fields need review confirmation.`
          : 'One date field needs review confirmation.'
      );
    }

    if (
      codes.has('EXTRACTION_FIELD_DATE_FORMAT_INVALID')
      || codes.has('EXTRACTION_FIELD_NUMERIC_FORMAT_INVALID')
      || codes.has('EXTRACTION_FIELD_AMOUNT_INVALID')
    ) {
      parts.push('Fix invalid date or amount formats.');
    }

    if (codes.has('EXTRACTION_FIELD_AMOUNT_INCONSISTENT')) {
      parts.push('Check amount consistency.');
    }

    if (codes.has('EXTRACTION_FIELDS_MISSING')) {
      parts.push('Run extraction again before confirming.');
    }

    if (parts.length === 0) {
      return 'Review highlighted fields before confirming.';
    }

    return parts.join(' ');
  }

  private extractFieldNamesFromErrors(
    errors: BackendValidationError[],
    code: string
  ): string[] {
    const names = errors
      .filter((error) => error.code === code)
      .map((error) => error.message ?? error.payload ?? '')
      .map((message) => {
        const match = message.match(/'([^']+)'/);
        return match?.[1] ?? null;
      })
      .filter((value): value is string => Boolean(value));

    return Array.from(new Set(names));
  }

  private syncAssignmentDefaultsForCurrentStatus(): void {
    const availableTypes = this.taskTypeOptions;

    if (availableTypes.length === 0) {
      this.assignTaskUserId = null;
      return;
    }

    if (!availableTypes.some((type) => type.value === this.assignTaskType)) {
      this.assignTaskType = availableTypes[0].value;
    }

    this.syncDefaultAssignee();
  }

  private resolveAuditAssignedUserName(userId: unknown): string {
    const numericUserId = Number(userId);

    if (!Number.isFinite(numericUserId)) {
      return 'selected user';
    }

    const user = this.assignableUsers.find((candidate) => candidate.id === numericUserId);

    if (!user) {
      return `user #${numericUserId}`;
    }

    const fullName = `${user.firstName ?? ''} ${user.lastName ?? ''}`.trim();

    return fullName || `user #${numericUserId}`;
  }

}
