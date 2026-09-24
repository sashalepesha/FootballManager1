package footballmanager.service.imports;

public class ImportRecordException extends Exception {

    public ImportRecordException(String message) {
        super(message);
    }

    public ImportRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
