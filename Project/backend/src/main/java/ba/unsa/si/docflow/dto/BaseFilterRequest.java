package ba.unsa.si.docflow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BaseFilterRequest {

    @Schema(description = "Page number")
    private int page = 0;

    @Schema(description = "Page size")
    private int size = 10;

    @Schema(description = "Field to sort by")
    private String sortBy = "id";

    @Schema(description = "Sort direction (asc or desc)")
    private String sortDirection = "asc";
}
