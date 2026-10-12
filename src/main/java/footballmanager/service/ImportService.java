package footballmanager.service;

import footballmanager.service.BatchImportService;
import footballmanager.service.imports.EntityImporter;
import footballmanager.service.imports.ImportFormatParser;
import footballmanager.service.imports.ImportRecordException;
import footballmanager.service.imports.ImportRegistry;
import footballmanager.service.imports.ImportResult;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportService {

    private final List<ImportFormatParser> formatParsers;
    private final ImportRegistry importRegistry;
    private final BatchImportService batchImportService;

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    public ImportService(List<ImportFormatParser> formatParsers, ImportRegistry importRegistry, BatchImportService batchImportService) {
        this.formatParsers = formatParsers;
        this.importRegistry = importRegistry;
        this.batchImportService = batchImportService;
    }

    @Transactional
    public ImportResult importFile(String entityType, MultipartFile file, boolean bulk) throws IOException {
        long start = System.nanoTime();

        EntityImporter<?> importer = importRegistry
            .find(entityType)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "Импорт не поддерживается для сущности '" + entityType + "'. Доступно: " + importRegistry.supportedEntityTypes()
                )
            );

        ImportFormatParser parser = formatParsers
            .stream()
            .filter(p -> p.supports(file.getOriginalFilename()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Неподдерживаемый формат файла: " + file.getOriginalFilename()));

        List<Map<String, Object>> records;

        try (InputStream inputStream = file.getInputStream()) {
            records = parser.parse(inputStream, entityType);
        }

        List<String> errors = new ArrayList<>();

        List<Object> entities = new ArrayList<>();

        mapRecords(importer, records, entities, errors);

        int importedCount;

        long saveStart = System.nanoTime();

        if (bulk) {
            importedCount = batchSave(importer, entities);
        } else {
            importedCount = saveAll(importer, entities);
        }

        long elapsedMillis = (System.nanoTime() - saveStart) / 1_000_000;

        String mode = bulk ? "bulk" : "saveAll";

        log.info(
            "Импорт {} завершён: режим={}, записей={}, ошибок={}, время={} мс",
            entityType,
            mode,
            importedCount,
            errors.size(),
            elapsedMillis
        );

        return new ImportResult(records.size(), importedCount, errors.size(), errors, elapsedMillis, mode);
    }

    private <T> void mapRecords(EntityImporter<T> importer, List<Map<String, Object>> records, List<Object> entities, List<String> errors) {
        for (Map<String, Object> record : records) {
            try {
                T entity = importer.mapRecord(record);
                entities.add(entity);
            } catch (ImportRecordException e) {
                errors.add(e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> int saveAll(EntityImporter<T> importer, List<Object> entities) {
        if (entities.isEmpty()) {
            return 0;
        }
        List<T> typedEntities = (List<T>) entities;

        importer.saveAll(typedEntities);
        return typedEntities.size();
    }

    @SuppressWarnings("unchecked")
    private <T> int batchSave(EntityImporter<T> importer, List<Object> entities) {
        if (entities.isEmpty()) {
            return 0;
        }

        List<T> typedEntities = (List<T>) entities;
        importer.batchSave(typedEntities, batchImportService);
        return typedEntities.size();
    }
}
