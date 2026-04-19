package ba.unsa.si.docflow.dto.document;

import ba.unsa.si.docflow.dto.BaseFilterRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Filter parameters for document search")
public class DocumentFilterRequest extends BaseFilterRequest {

    @Schema(description = "Filter by document name")
    private String name;

    @Schema(description = "Filter by document type")
    private String documentType;

    @Schema(description = "Filter by document status")
    private String documentStatus;

    @Schema(description = "Filter by company id")
    private Long companyId;
}