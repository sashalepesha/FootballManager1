package footballmanager.service.imports;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ImportFormatParser {
    boolean supports(String fileName);

    List<Map<String, String>> parse(InputStream inputStream, String entityType) throws IOException;
}
