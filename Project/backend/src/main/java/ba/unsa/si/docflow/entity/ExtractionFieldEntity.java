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
     * Many extracted fields belong to one extraction result.
     * Example fields: supplier_name, invoice_id, invoice_date, total_amount, currency.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extraction_id", nullable = false)
    private ExtractionEntity extraction;

    @Column(name = "field_name", nullable = false)
    private String fieldName;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "confidence", precision = 10, scale = 6)
    private BigDecimal confidence;

    @Column(name = "is_corrected", nullable = false)
    private Boolean corrected = false;
}