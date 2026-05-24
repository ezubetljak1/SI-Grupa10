-- Sprint 9 workflow foundation migration
-- Hibernate ddl-auto=update usually creates new tables/columns,
-- but existing PostgreSQL CHECK constraints must be updated manually.

-- Pokretanje:
-- nakon sto je dignut docker kontejner lokalni za db
-- docker exec -it docflow-local-db psql -U postgres -d docflow
-- pasteati sql
-- izlaz iz psql sa \q

ALTER TABLE document DROP CONSTRAINT IF EXISTS document_document_type_check;
ALTER TABLE document DROP CONSTRAINT IF EXISTS document_document_status_check;

ALTER TABLE document
    ADD CONSTRAINT document_document_type_check
        CHECK (
            document_type IN (
                              'INVOICE',
                              'RECEIPT',
                              'BANK_STATEMENT',
                              'FORM',
                              'OTHER',
                              'UNKNOWN'
                )
            );

ALTER TABLE document
    ADD CONSTRAINT document_document_status_check
        CHECK (
            document_status IN (
                                'UPLOADED',
                                'PROCESSING_FAILED',
                                'EXTRACTED',
                                'UNDER_REVIEW',
                                'NEEDS_CLASSIFICATION_REVIEW',
                                'READY_FOR_APPROVAL',
                                'NEEDS_CORRECTION',
                                'APPROVED',
                                'REJECTED',
                                'COMPLETED'
                )
            );