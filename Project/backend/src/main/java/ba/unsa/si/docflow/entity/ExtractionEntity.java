package ba.unsa.si.docflow.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "extraction")
public class ExtractionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * One extraction result belongs to one document.
     * Retry should update/replace fields for this extraction, not create a second extraction row.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private DocumentEntity document;

    /**
     * Stores the full serialized OCR/AI response used for traceability/debugging.
     * This can include rawText + all fields returned by Google Document AI.
     */
    @Column(name = "raw_json", columnDefinition = "TEXT")
    private String rawJson;

    @Column(name = "extraction_time", nullable = false)
    private LocalDateTime extractionTime;

    @OneToMany(mappedBy = "extraction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExtractionFieldEntity> fields = new ArrayList<>();
}