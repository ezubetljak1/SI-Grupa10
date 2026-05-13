export interface ExtractionField {
  id: number;
  fieldName: string;
  value: string | null;
  confidence: number | null;
  corrected: boolean;
}

export interface Extraction {
  id: number;
  documentId: number;
  rawJson: string;
  extractionTime: string;
  fields: ExtractionField[];
}