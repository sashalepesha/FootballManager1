package footballmanager.service.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

@Component
public class XmlImportParser implements ImportFormatParser {

    @Override
    public boolean supports(String filename) {
        return filename != null && filename.toLowerCase().endsWith(".xml");
    }

    @Override
    public List<Map<String, String>> parse(InputStream inputStream, String entityType) throws IOException {
        byte[] fileBytes = inputStream.readAllBytes();
        List<String> validationErrors = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);

            DocumentBuilder builder = factory.newDocumentBuilder();

            builder.setEntityResolver((_, _) -> {
                String dtdPath = "/dtd/" + entityType + ".dtd";
                InputStream dtdStream = getClass().getResourceAsStream(dtdPath);
                if (dtdStream == null) {
                    throw new java.io.IOException("DTD файл не найден в ресурсах по пути: " + dtdPath);
                }

                return new org.xml.sax.InputSource(dtdStream);
            });

            builder.setErrorHandler(
                new ErrorHandler() {
                    @Override
                    public void warning(SAXParseException exception) {}

                    @Override
                    public void error(SAXParseException exception) {
                        validationErrors.add("Строка " + exception.getLineNumber() + ": " + exception.getMessage());
                    }

                    @Override
                    public void fatalError(SAXParseException exception) throws SAXParseException {
                        throw exception;
                    }
                }
            );

            Document document = builder.parse(new ByteArrayInputStream(fileBytes));

            if (!validationErrors.isEmpty()) {
                throw new IOException("Файл не прошёл DTD-валидацию:\n" + String.join("\n", validationErrors));
            }

            document.getDocumentElement().normalize();

            List<Map<String, String>> records = new ArrayList<>();
            NodeList rootChildren = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < rootChildren.getLength(); i++) {
                Node recordNode = rootChildren.item(i);
                if (recordNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                records.add(elementToMap((Element) recordNode));
            }
            return records;
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Не удалось распарсить/провалидировать XML-файл: " + e.getMessage(), e);
        }
    }

    private Map<String, String> elementToMap(Element recordElement) {
        Map<String, String> record = new LinkedHashMap<>();
        NodeList fields = recordElement.getChildNodes();
        for (int i = 0; i < fields.getLength(); i++) {
            Node fieldNode = fields.item(i);
            if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {
                record.put(fieldNode.getNodeName(), fieldNode.getTextContent().trim());
            }
        }
        return record;
    }
}
