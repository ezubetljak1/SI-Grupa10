package ba.unsa.si.docflow.service.ocr;

import ba.unsa.si.docflow.entity.enums.DocumentType;
import ba.unsa.si.docflow.service.ocr.model.DocumentClassificationResult;
import ba.unsa.si.docflow.service.ocr.model.OcrExtractedField;
import ba.unsa.si.docflow.service.ocr.model.OcrResult;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DocumentClassificationService {

    private final OcrProvider ocrProvider;
    private final DocumentAiProcessorRouter processorRouter;

    public DocumentClassificationResult classify(byte[] fileContent, String mimeType) {
        String classifierProcessorId = processorRouter.resolveClassifierProcessorId();

        OcrResult classifierResult =
                ocrProvider.process(fileContent, mimeType, classifierProcessorId);

        return classifierResult.getFields().stream()
                .map(this::toClassificationResult)
                .flatMap(Optional::stream)
                .max(Comparator.comparing(DocumentClassificationResult::confidence))
                .orElse(new DocumentClassificationResult(DocumentType.OTHER, BigDecimal.ZERO));
    }

    private Optional<DocumentClassificationResult> toClassificationResult(OcrExtractedField field) {
        Optional<DocumentType> type = resolveDocumentType(field);

        if (type.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal confidence =
                field.getConfidence() != null ? field.getConfidence() : BigDecimal.ZERO;

        return Optional.of(new DocumentClassificationResult(type.get(), confidence));
    }

    private Optional<DocumentType> resolveDocumentType(OcrExtractedField field) {
        return firstMatchingDocumentType(
                field.getType(), field.getValue(), field.getNormalizedValue());
    }

    private Optional<DocumentType> firstMatchingDocumentType(String... candidates) {
        for (String candidate : candidates) {
            Optional<DocumentType> type = parseDocumentType(candidate);

            if (type.isPresent()) {
                return type;
            }
        }
        return Optional.empty();
    }

    private Optional<DocumentType> parseDocumentType(String rawValue) {
        if (!StringUtils.hasText(rawValue)) {
            return Optional.empty();
        }

        String normalized =
                rawValue.trim().toUpperCase(Locale.ROOT).replace("-", "_").replace(" ", "_");

        try {
            return Optional.of(DocumentType.valueOf(normalized));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
