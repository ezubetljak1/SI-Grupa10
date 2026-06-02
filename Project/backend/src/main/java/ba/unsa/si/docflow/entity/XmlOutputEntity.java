package ba.unsa.si.docflow.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "xml_output")
public class XmlOutputEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, unique = true)
    private DocumentEntity document;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "generated_by", nullable = false)
    private Long generatedBy;
}
