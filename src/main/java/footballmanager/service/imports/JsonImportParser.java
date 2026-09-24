package footballmanager.service.imports;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JsonImportParser implements ImportFormatParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(String fileName) {
        return fileName != null && fileName.endsWith(".json");
    }

    public List<Map<String, String>> parse(InputStream inputStream, String entityType) throws IOException {
        return objectMapper.readValue(inputStream, new TypeReference<List<Map<String, String>>>() {});
    }
}
