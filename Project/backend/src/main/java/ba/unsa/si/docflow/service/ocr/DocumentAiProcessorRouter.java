package ba.unsa.si.docflow.service.ocr;

import ba.unsa.si.docflow.config.OcrProperties;
import ba.unsa.si.docflow.entity.enums.DocumentType;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class DocumentAiProcessorRouter {

    private final OcrProperties ocrProperties;

    public String resolveProcessorId(DocumentType documentType) {
        String processorId =
                switch (documentType) {
                    case INVOICE -> ocrProperties.getInvoiceProcessorId();
                    case RECEIPT -> ocrProperties.getReceiptProcessorId();
                    case BANK_STATEMENT -> ocrProperties.getBankStatementProcessorId();
                    case FORM, OTHER, UNKNOWN -> ocrProperties.getFormProcessorId();
                };

        if (!StringUtils.hasText(processorId)) {
            throw new IllegalStateException(
                    "Document AI processor is not configured for document type: " + documentType);
        }

        return processorId;
    }

    public String resolveClassifierProcessorId() {
        if (!StringUtils.hasText(ocrProperties.getClassifierProcessorId())) {
            throw new IllegalStateException("Document AI classifier processor is not configured.");
        }

        return ocrProperties.getClassifierProcessorId();
    }
}
