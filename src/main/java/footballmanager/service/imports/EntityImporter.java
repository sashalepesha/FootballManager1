package footballmanager.service.imports;

import java.util.List;
import java.util.Map;

public interface EntityImporter<T> {
    String entityType();

    T mapRecord(Map<String, Object> record) throws ImportRecordException;

    void saveAll(List<T> entities);
}
