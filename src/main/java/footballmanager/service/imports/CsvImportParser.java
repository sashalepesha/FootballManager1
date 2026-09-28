package footballmanager.service.imports;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
public class CsvImportParser implements ImportFormatParser {

    @Override
    public boolean supports(String filename) {
        return filename != null && filename.endsWith(".csv");
    }

    @Override
    public List<Map<String, Object>> parse(InputStream inputStream, String entityType) throws IOException {
        List<Map<String, Object>> records = new ArrayList<>();
        CSVFormat format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).build();

        try (
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            CSVParser parser = new CSVParser(reader, format)
        ) {
            for (CSVRecord csvRecord : parser) {
                records.add(new java.util.LinkedHashMap<>(csvRecord.toMap()));
            }
        }
        return records;
    }
}
