package ba.unsa.si.docflow.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "extraction_field")
public class ExtractionFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many extracted fields belong to one extraction result. Example fields: supplier_name,
     * invoice_id, invoice_date, total_amount, currency.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extraction_id", nullable = false)
    private ExtractionEntity extraction;

    /**
     * Canonical backend/XML key. Do not use this as a free user-facing label.
     *
     * <p>Examples: - invoice_id - invoice_date - supplier_name - total_amount -
     * custom.contract_reference
     */
    @Column(name = "field_name", nullable = false)
    private String fieldName;

    /**
     * Optional user-facing label. This improves UI readability without breaking validation/XML
     * mapping.
     */
    @Column(name = "display_name")
    private String displayName;

    @Column(name = "`value`", columnDefinition = "TEXT")
    private String value;

    @Column(name = "confidence", precision = 10, scale = 6)
    private BigDecimal confidence;

    @Column(name = "is_corrected", nullable = false)
    private Boolean corrected = false;

    @Column(name = "is_placeholder", nullable = false, columnDefinition = "boolean default false")
    private Boolean placeholder = false;

    /** True when the field was manually added by a user instead of returned by OCR. */
    @Column(name = "is_manual", nullable = false, columnDefinition = "boolean default false")
    private Boolean manual = false;
}
