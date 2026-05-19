export interface DocumentsByResponsibleUser {
  userId: number;
  fullName: string;
  documentCount: number;
}

export interface DashboardResponse {
  totalDocuments: number;

  documentsByStatus: Record<string, number>;

  documentsByResponsibleUser: DocumentsByResponsibleUser[];
}
