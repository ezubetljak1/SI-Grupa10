package ba.unsa.si.docflow.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private String code;
    private T payload;
}
