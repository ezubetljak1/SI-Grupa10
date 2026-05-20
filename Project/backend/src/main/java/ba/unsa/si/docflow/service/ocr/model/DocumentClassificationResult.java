package ba.unsa.si.docflow.service.ocr.model;

import ba.unsa.si.docflow.entity.enums.DocumentType;

import java.math.BigDecimal;

public record DocumentClassificationResult(DocumentType documentType, BigDecimal confidence) {}
