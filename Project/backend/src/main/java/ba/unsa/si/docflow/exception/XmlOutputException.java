package ba.unsa.si.docflow.exception;

public class XmlOutputException extends RuntimeException {

    public XmlOutputException(String message) {
        super(message);
    }

    public XmlOutputException(String message, Throwable cause) {
        super(message, cause);
    }
}
