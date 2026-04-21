package ba.unsa.si.docflow.response;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrors {
    private final List<ValidationError> errors = new ArrayList<>();

    public void add(String code, String message){
        errors.add(new ValidationError(code, message));
    }

    public boolean hasErrors(){
        return !errors.isEmpty();
    }

    public List<ValidationError> getErrors(){
        return errors;
    }
}
