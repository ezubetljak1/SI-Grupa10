package ba.unsa.si.docflow.exception;

public class ApiNotFoundException extends RuntimeException {
    public ApiNotFoundException(String message){
        super(message);
    }
}
