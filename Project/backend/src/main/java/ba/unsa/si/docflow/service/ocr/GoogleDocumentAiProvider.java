package ba.unsa.si.docflow.service.ocr;

import ba.unsa.si.docflow.config.OcrProperties;
import ba.unsa.si.docflow.service.ocr.model.OcrExtractedField;
import ba.unsa.si.docflow.service.ocr.model.OcrResult;

import com.google.cloud.documentai.v1.Document;
import com.google.cloud.documentai.v1.DocumentProcessorServiceClient;
import com.google.cloud.documentai.v1.DocumentProcessorServiceSettings;
import com.google.cloud.documentai.v1.ProcessRequest;
import com.google.cloud.documentai.v1.ProcessResponse;
import com.google.cloud.documentai.v1.ProcessorName;
import com.google.cloud.documentai.v1.RawDocument;
import com.google.protobuf.ByteString;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class GoogleDocumentAiProvider implements OcrProvider {

    private final OcrProperties ocrProperties;

    @Override
    public OcrResult process(byte[] fileContent, String mimeType, String processorId) {
        validateConfiguration(processorId);

        try {
            DocumentProcessorServiceSettings settings =
                    DocumentProcessorServiceSettings.newBuilder()
                            .setEndpoint(ocrProperties.getEndpoint())
                            .build();

            try (DocumentProcessorServiceClient client =
                    DocumentProcessorServiceClient.create(settings)) {

                String processorName =
                        ProcessorName.of(
                                        ocrProperties.getProjectId(),
                                        ocrProperties.getLocation(),
                                        processorId)
                                .toString();

                RawDocument rawDocument =
                        RawDocument.newBuilder()
                                .setContent(ByteString.copyFrom(fileContent))
                                .setMimeType(mimeType)
                                .build();

                ProcessRequest request =
                        ProcessRequest.newBuilder()
                                .setName(processorName)
                                .setRawDocument(rawDocument)
                                .build();

                ProcessResponse response = client.processDocument(request);
                Document document = response.getDocument();

                return new OcrResult(document.getText(), mapExtractedFields(document, processorId));
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not create Google Document AI client.", exception);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Google Document AI processing failed.", exception);
        }
    }

    private List<OcrExtractedField> mapExtractedFields(Document document, String processorId) {
        List<OcrExtractedField> fields = new ArrayList<>(mapEntities(document));

        if (shouldMapFormFields(processorId)) {
            fields.addAll(mapFormFields(document));
        }

        return fields;
    }

    private List<OcrExtractedField> mapEntities(Document document) {
        return document.getEntitiesList().stream()
                .map(
                        entity ->
                                new OcrExtractedField(
                                        entity.getType(),
                                        entity.getMentionText(),
                                        entity.hasNormalizedValue()
                                                ? entity.getNormalizedValue().getText()
                                                : null,
                                        BigDecimal.valueOf(entity.getConfidence())))
                .toList();
    }

    private List<OcrExtractedField> mapFormFields(Document document) {
        List<OcrExtractedField> fields = new ArrayList<>();
        String documentText = document.getText();

        for (Document.Page page : document.getPagesList()) {
            for (Document.Page.FormField formField : page.getFormFieldsList()) {
                String fieldName =
                        cleanExtractedText(
                                getTextFromTextAnchor(
                                        formField.getFieldName().getTextAnchor(), documentText));

                String fieldValue =
                        cleanExtractedText(
                                getTextFromTextAnchor(
                                        formField.getFieldValue().getTextAnchor(), documentText));

                if (!StringUtils.hasText(fieldName)) {
                    continue;
                }

                fields.add(
                        new OcrExtractedField(
                                normalizeFormFieldName(fieldName),
                                fieldValue,
                                null,
                                resolveFormFieldConfidence(formField)));
            }
        }

        return fields;
    }

    private boolean shouldMapFormFields(String processorId) {
        return StringUtils.hasText(ocrProperties.getFormProcessorId())
                && ocrProperties.getFormProcessorId().equals(processorId);
    }

    private String getTextFromTextAnchor(Document.TextAnchor textAnchor, String documentText) {
        if (textAnchor == null
                || textAnchor.getTextSegmentsList().isEmpty()
                || !StringUtils.hasText(documentText)) {
            return "";
        }

        StringBuilder text = new StringBuilder();

        for (Document.TextAnchor.TextSegment segment : textAnchor.getTextSegmentsList()) {
            int startIndex = Math.toIntExact(segment.getStartIndex());
            int endIndex = Math.toIntExact(segment.getEndIndex());

            startIndex = Math.max(0, Math.min(startIndex, documentText.length()));
            endIndex = Math.max(startIndex, Math.min(endIndex, documentText.length()));

            if (startIndex < endIndex) {
                text.append(documentText, startIndex, endIndex);
            }
        }

        return text.toString();
    }

    private String cleanExtractedText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.replace("\n", " ").replaceAll("\\s+", " ").trim();
    }

    private String normalizeFormFieldName(String fieldName) {
        String normalized =
                fieldName
                        .trim()
                        .replaceAll("[:\\s]+$", "")
                        .toLowerCase(Locale.ROOT)
                        .replaceAll("[^\\p{L}\\p{Nd}]+", "_")
                        .replaceAll("^_+", "")
                        .replaceAll("_+$", "");

        if (!StringUtils.hasText(normalized)) {
            return "form_field";
        }

        return normalized;
    }

    private BigDecimal resolveFormFieldConfidence(Document.Page.FormField formField) {
        float nameConfidence = formField.getFieldName().getConfidence();
        float valueConfidence = formField.getFieldValue().getConfidence();

        float confidence = Math.max(nameConfidence, valueConfidence);

        if (confidence <= 0) {
            return null;
        }

        return BigDecimal.valueOf(confidence);
    }

    private void validateConfiguration(String processorId) {
        if (!StringUtils.hasText(ocrProperties.getProjectId())
                || !StringUtils.hasText(ocrProperties.getLocation())
                || !StringUtils.hasText(ocrProperties.getEndpoint())
                || !StringUtils.hasText(processorId)) {
            throw new IllegalStateException(
                    "Google Document AI configuration is missing. "
                            + "Check GOOGLE_CLOUD_PROJECT_ID, GOOGLE_DOCUMENT_AI_LOCATION, "
                            + "GOOGLE_DOCUMENT_AI_ENDPOINT and the selected processor ID.");
        }
    }
}
