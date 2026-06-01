package ba.unsa.si.docflow.service.xml;

import ba.unsa.si.docflow.entity.DocumentEntity;
import ba.unsa.si.docflow.entity.ExtractionEntity;
import ba.unsa.si.docflow.entity.ExtractionFieldEntity;
import ba.unsa.si.docflow.exception.XmlOutputException;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@Service
public class XmlOutputGenerator {

    public byte[] generate(
            DocumentEntity document, ExtractionEntity extraction, LocalDateTime generatedAt) {

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            XMLStreamWriter writer =
                    XMLOutputFactory.newFactory()
                            .createXMLStreamWriter(outputStream, StandardCharsets.UTF_8.name());

            writer.writeStartDocument(StandardCharsets.UTF_8.name(), "1.0");

            writer.writeStartElement("docflowDocument");

            writeMetadata(writer, document, generatedAt);
            writeFields(writer, extraction.getFields());

            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();

            return outputStream.toByteArray();

        } catch (XMLStreamException exception) {
            throw new XmlOutputException("Could not generate XML output.", exception);
        }
    }

    private void writeMetadata(
            XMLStreamWriter writer, DocumentEntity document, LocalDateTime generatedAt)
            throws XMLStreamException {

        writer.writeStartElement("metadata");

        writeTextElement(writer, "documentId", String.valueOf(document.getId()));

        writeTextElement(writer, "documentName", document.getName());

        writeTextElement(writer, "documentType", document.getDocumentType().name());

        writeTextElement(writer, "generatedAt", generatedAt.toString());

        writer.writeEndElement();
    }

    private void writeFields(XMLStreamWriter writer, List<ExtractionFieldEntity> fields)
            throws XMLStreamException {

        writer.writeStartElement("extractedFields");

        fields.stream()
                .filter(this::shouldIncludeField)
                .sorted(Comparator.comparing(field -> normalizeFieldName(field.getFieldName())))
                .forEach(field -> writeFieldUnchecked(writer, field));

        writer.writeEndElement();
    }

    private boolean shouldIncludeField(ExtractionFieldEntity field) {

        return field != null
                && !Boolean.TRUE.equals(field.getPlaceholder())
                && StringUtils.hasText(field.getFieldName())
                && StringUtils.hasText(field.getValue());
    }

    private void writeFieldUnchecked(XMLStreamWriter writer, ExtractionFieldEntity field) {

        try {
            writer.writeStartElement("field");

            writer.writeAttribute("name", normalizeFieldName(field.getFieldName()));

            writer.writeAttribute("manual", String.valueOf(Boolean.TRUE.equals(field.getManual())));

            writer.writeAttribute(
                    "corrected", String.valueOf(Boolean.TRUE.equals(field.getCorrected())));

            if (StringUtils.hasText(field.getDisplayName())) {
                writer.writeAttribute("label", field.getDisplayName().trim());
            }

            if (field.getConfidence() != null) {
                writer.writeAttribute("confidence", field.getConfidence().toPlainString());
            }

            writer.writeCharacters(field.getValue().trim());

            writer.writeEndElement();

        } catch (XMLStreamException exception) {
            throw new XmlOutputException("Could not map extracted field to XML.", exception);
        }
    }

    private void writeTextElement(XMLStreamWriter writer, String name, String value)
            throws XMLStreamException {

        writer.writeStartElement(name);
        writer.writeCharacters(value == null ? "" : value);
        writer.writeEndElement();
    }

    private String normalizeFieldName(String fieldName) {
        return fieldName.trim().toLowerCase();
    }
}
