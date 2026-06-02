package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dto.extraction.ExtractionFieldResponse;
import ba.unsa.si.docflow.dto.extraction.ExtractionResponse;
import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;

import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ExtractionMapper {

    public ExtractionResponse entityToDto(ExtractionEntity entity) {
        if (entity == null) {
            return null;
        }

        ExtractionResponse response = new ExtractionResponse();

        response.setId(entity.getId());
        response.setDocumentId(entity.getDocument() != null ? entity.getDocument().getId() : null);
        response.setRawJson(entity.getRawJson());
        response.setExtractionTime(entity.getExtractionTime());

        response.setFields(fieldsToDto(entity.getFields()));

        return response;
    }

    public List<ExtractionFieldResponse> fieldsToDto(List<ExtractionFieldEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .sorted(Comparator.comparing(ExtractionFieldEntity::getFieldName))
                .map(this::fieldToDto)
                .toList();
    }

    public ExtractionFieldResponse fieldToDto(ExtractionFieldEntity entity) {
        if (entity == null) {
            return null;
        }

        ExtractionFieldResponse response = new ExtractionFieldResponse();

        response.setId(entity.getId());
        response.setFieldName(entity.getFieldName());
        response.setValue(entity.getValue());
        response.setConfidence(entity.getConfidence());
        response.setCorrected(entity.getCorrected());
        response.setPlaceholder(entity.getPlaceholder());
        response.setDisplayName(entity.getDisplayName());
        response.setManual(entity.getManual());

        return response;
    }
}
