package footballmanager.service;

import footballmanager.service.imports.EntityImporter;
import footballmanager.service.imports.ImportFormatParser;
import footballmanager.service.imports.ImportRecordException;
import footballmanager.service.imports.ImportRegistry;
import footballmanager.service.imports.ImportResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImportService {

    private final List<ImportFormatParser> formatParsers;
    private final ImportRegistry importRegistry;

    public ImportService(List<ImportFormatParser> formatParsers, ImportRegistry importRegistry) {
        this.formatParsers = formatParsers;
        this.importRegistry = importRegistry;
    }

    public ImportResult importFile(String entityType, MultipartFile file) throws IOException {
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

        List<Map<String, String>> records;
        try (InputStream inputStream = file.getInputStream()) {
            records = parser.parse(inputStream, entityType);
        }

        return mapAndSave(importer, records);
    }

    @SuppressWarnings("unchecked")
    private <T> ImportResult mapAndSave(EntityImporter<T> importer, List<Map<String, String>> records) {
        List<T> toSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Map<String, String> record : records) {
            try {
                toSave.add(importer.mapRecord(record));
            } catch (ImportRecordException e) {
                errors.add(e.getMessage());
            }
        }

        if (!toSave.isEmpty()) {
            importer.saveAll(toSave);
        }

        return new ImportResult(records.size(), toSave.size(), errors.size(), errors);
    }
}
