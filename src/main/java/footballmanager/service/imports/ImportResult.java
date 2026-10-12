package footballmanager.service.imports;

import java.util.List;

public record ImportResult(int totalRecords, int importedCount, int failedCount, List<String> errors, long elapsedMillis, String mode) {}
