package ba.unsa.si.docflow.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PagedResponse<T> {
    private String code;
    private List<T> payload;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
