package ba.unsa.si.docflow.dto.document;

import ba.unsa.si.docflow.dto.BaseFilterRequest;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Schema(description = "Filter parameters for document search")
public class DocumentFilterRequest extends BaseFilterRequest {

    @Schema(description = "Free-text search across document name and identifier")
    private String search;

    @Schema(description = "Filter by document name")
    private String name;

    @Schema(description = "Filter by document type")
    private String documentType;

    @Schema(description = "Filter by document status")
    private String documentStatus;

    @Schema(description = "Filter by document creation date from")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdFrom;

    @Schema(description = "Filter by document creation date to")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdTo;

    @Schema(description = "Filter by assigned user id")
    private Long assignedUserId;

    @Schema(description = "Filter by company id")
    private Long companyId;
}
