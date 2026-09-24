package footballmanager.service.imports;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ImportRegistry {

    private final Map<String, EntityImporter<?>> importersByType;

    public ImportRegistry(List<EntityImporter<?>> importers) {
        this.importersByType = importers.stream().collect(Collectors.toMap(EntityImporter::entityType, importer -> importer));
    }

    public Optional<EntityImporter<?>> find(String entityType) {
        return Optional.ofNullable(importersByType.get(entityType));
    }

    public List<String> supportedEntityTypes() {
        return importersByType.keySet().stream().sorted().collect(Collectors.toList());
    }
}
