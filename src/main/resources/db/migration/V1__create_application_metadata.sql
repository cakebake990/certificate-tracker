CREATE TABLE application_metadata (
    metadata_key VARCHAR(100) NOT NULL PRIMARY KEY,
    metadata_value VARCHAR(500) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
);

INSERT INTO application_metadata (metadata_key, metadata_value)
VALUES ('schema_purpose', 'Certificate Tracker infrastructure foundation');
