package ba.unsa.si.docflow.dto.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Filter parameters for document search")
public class DocumentFilterRequest {

    @Schema(description = "Filter by document name")
    private String name;

    @Schema(description = "Filter by document type")
    private String documentType;

    @Schema(description = "Filter by document status")
    private String documentStatus;

    @Schema(description = "Filter by company id")
    private Long companyId;

    @Schema(description = "Page number")
    private int page = 0;

    @Schema(description = "Page size")
    private int size = 10;

    @Schema(description = "Field to sort by")
    private String sortBy = "id";

    @Schema(description = "Sort direction (asc or desc)")
    private String sortDirection = "asc";
}