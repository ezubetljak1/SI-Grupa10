package ba.unsa.si.docflow.dto.company;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class CompanyUpdateRequest implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonIgnore private Long id;

    private String name;
    private String address;
    private String email;
    private String status;
}
