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
import java.util.List;

@Service
@AllArgsConstructor
public class GoogleDocumentAiProvider implements OcrProvider {

    private final OcrProperties ocrProperties;

    @Override
    public OcrResult process(byte[] fileContent, String mimeType) {
        validateConfiguration();

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
                                        ocrProperties.getProcessorId())
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

                return new OcrResult(document.getText(), mapEntities(document));
            }
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Could not create Google Document AI client.", exception);
        } catch (RuntimeException exception) {
            throw new IllegalStateException("Google Document AI processing failed.", exception);
        }
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

    private void validateConfiguration() {
        if (!StringUtils.hasText(ocrProperties.getProjectId())
                || !StringUtils.hasText(ocrProperties.getLocation())
                || !StringUtils.hasText(ocrProperties.getProcessorId())
                || !StringUtils.hasText(ocrProperties.getEndpoint())) {
            throw new IllegalStateException(
                    "Google Document AI configuration is missing. Check GOOGLE_CLOUD_PROJECT_ID, GOOGLE_DOCUMENT_AI_LOCATION, GOOGLE_DOCUMENT_AI_PROCESSOR_ID and GOOGLE_DOCUMENT_AI_ENDPOINT.");
        }
    }
}
