export interface ExtractionField {
  id: number;
  fieldName: string;
  value: string | null;
  confidence: number | null;
  corrected: boolean;
  placeholder: boolean;
  displayName?: string | null;
  manual?: boolean;
}

export interface CreateExtractionFieldRequest {
  fieldName: string;
  displayName?: string;
  value: string;
}

export interface Extraction {
  id: number;
  documentId: number;
  rawJson: string;
  extractionTime: string;
  fields: ExtractionField[];
}