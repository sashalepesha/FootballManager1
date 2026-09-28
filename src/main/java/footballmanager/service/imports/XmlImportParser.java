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
    public List<Map<String, Object>> parse(InputStream inputStream, String entityType) throws IOException {
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
            Element root = document.getDocumentElement();

            List<Element> rootChildren = childElements(root);
            boolean rootIsListOfRecords = !root.getNodeName().equalsIgnoreCase(entityType) && !rootChildren.isEmpty();

            List<Map<String, Object>> records = new ArrayList<>();
            if (rootIsListOfRecords) {
                for (Element child : rootChildren) {
                    records.add(elementToMap(child));
                }
            } else {
                records.add(elementToMap(root));
            }
            return records;
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Не удалось распарсить/провалидировать XML-файл: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> elementToMap(Element element) {
        Map<String, Object> result = new LinkedHashMap<>();
        Map<String, List<Element>> childrenByTag = new LinkedHashMap<>();

        for (Element child : childElements(element)) {
            childrenByTag.computeIfAbsent(child.getNodeName(), k -> new ArrayList<>()).add(child);
        }

        for (Map.Entry<String, List<Element>> entry : childrenByTag.entrySet()) {
            String tag = entry.getKey();
            List<Element> siblings = entry.getValue();

            if (siblings.size() > 1) {
                List<Object> list = new ArrayList<>();
                for (Element sibling : siblings) {
                    list.add(childElements(sibling).isEmpty() ? sibling.getTextContent().trim() : elementToMap(sibling));
                }
                result.put(tag, list);
            } else {
                Element only = siblings.get(0);
                if (childElements(only).isEmpty()) {
                    result.put(tag, only.getTextContent().trim());
                } else if (hasRepeatedGrandchildTag(only)) {
                    Map<String, Object> wrapped = elementToMap(only);

                    if (wrapped.size() == 1) {
                        result.put(tag, wrapped.values().iterator().next());
                    } else {
                        result.put(tag, wrapped);
                    }
                } else {
                    result.put(tag, elementToMap(only));
                }
            }
        }
        return result;
    }

    private boolean hasRepeatedGrandchildTag(Element element) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Element child : childElements(element)) {
            counts.merge(child.getNodeName(), 1, Integer::sum);
        }
        return counts
            .values()
            .stream()
            .anyMatch(c -> c > 1);
    }

    private boolean sameTagName(List<Element> elements) {
        String first = elements.get(0).getNodeName();
        return elements.stream().allMatch(e -> e.getNodeName().equals(first));
    }

    private List<Element> childElements(Element element) {
        List<Element> children = new ArrayList<>();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                children.add((Element) node);
            }
        }
        return children;
    }
}
