CREATE TABLE certificate (
    id BIGINT IDENTITY(1,1) NOT NULL,
    customer_id BIGINT NOT NULL,
    certificate_type_id BIGINT NOT NULL,
    certificate_name NVARCHAR(200) NOT NULL,
    issuer NVARCHAR(500) NULL,
    expiration_date DATE NOT NULL,
    serial_number VARCHAR(200) NULL,
    fingerprint VARCHAR(256) NULL,
    subject_common_name NVARCHAR(500) NULL,
    notes NVARCHAR(MAX) NULL,
    active BIT NOT NULL CONSTRAINT df_certificate_active DEFAULT 1,
    created_at DATETIME2(3) NOT NULL CONSTRAINT df_certificate_created DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2(3) NOT NULL CONSTRAINT df_certificate_updated DEFAULT SYSUTCDATETIME(),
    version BIGINT NOT NULL CONSTRAINT df_certificate_version DEFAULT 0,
    CONSTRAINT pk_certificate PRIMARY KEY (id),
    CONSTRAINT fk_certificate_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_certificate_type FOREIGN KEY (certificate_type_id) REFERENCES certificate_type(id)
);

CREATE TABLE certificate_hostname (
    id BIGINT IDENTITY(1,1) NOT NULL,
    certificate_id BIGINT NOT NULL,
    hostname VARCHAR(253) NOT NULL,
    created_at DATETIME2(3) NOT NULL CONSTRAINT df_certificate_hostname_created DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_certificate_hostname PRIMARY KEY (id),
    CONSTRAINT fk_certificate_hostname_certificate FOREIGN KEY (certificate_id) REFERENCES certificate(id) ON DELETE CASCADE,
    CONSTRAINT uq_certificate_hostname UNIQUE (certificate_id, hostname)
);

CREATE TABLE certificate_function_usage (
    id BIGINT IDENTITY(1,1) NOT NULL,
    certificate_id BIGINT NOT NULL,
    function_usage_id BIGINT NOT NULL,
    created_at DATETIME2(3) NOT NULL CONSTRAINT df_certificate_usage_created DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_certificate_function_usage PRIMARY KEY (id),
    CONSTRAINT fk_certificate_usage_certificate FOREIGN KEY (certificate_id) REFERENCES certificate(id) ON DELETE CASCADE,
    CONSTRAINT fk_certificate_usage_function FOREIGN KEY (function_usage_id) REFERENCES function_usage(id),
    CONSTRAINT uq_certificate_function_usage UNIQUE (certificate_id, function_usage_id)
);

CREATE TABLE certificate_system_integration (
    id BIGINT IDENTITY(1,1) NOT NULL,
    certificate_id BIGINT NOT NULL,
    system_integration_id BIGINT NOT NULL,
    created_at DATETIME2(3) NOT NULL CONSTRAINT df_certificate_system_created DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_certificate_system PRIMARY KEY (id),
    CONSTRAINT fk_certificate_system_certificate FOREIGN KEY (certificate_id) REFERENCES certificate(id) ON DELETE CASCADE,
    CONSTRAINT fk_certificate_system_reference FOREIGN KEY (system_integration_id) REFERENCES system_integration(id),
    CONSTRAINT uq_certificate_system UNIQUE (certificate_id, system_integration_id)
);

CREATE TABLE certificate_environment (
    id BIGINT IDENTITY(1,1) NOT NULL,
    certificate_id BIGINT NOT NULL,
    environment_id BIGINT NOT NULL,
    created_at DATETIME2(3) NOT NULL CONSTRAINT df_certificate_environment_created DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_certificate_environment PRIMARY KEY (id),
    CONSTRAINT fk_certificate_environment_certificate FOREIGN KEY (certificate_id) REFERENCES certificate(id) ON DELETE CASCADE,
    CONSTRAINT fk_certificate_environment_reference FOREIGN KEY (environment_id) REFERENCES deployment_environment(id),
    CONSTRAINT uq_certificate_environment UNIQUE (certificate_id, environment_id)
);

CREATE TABLE certificate_site (
    id BIGINT IDENTITY(1,1) NOT NULL,
    certificate_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL,
    created_at DATETIME2(3) NOT NULL CONSTRAINT df_certificate_site_created DEFAULT SYSUTCDATETIME(),
    CONSTRAINT pk_certificate_site PRIMARY KEY (id),
    CONSTRAINT fk_certificate_site_certificate FOREIGN KEY (certificate_id) REFERENCES certificate(id) ON DELETE CASCADE,
    CONSTRAINT fk_certificate_site_site FOREIGN KEY (site_id) REFERENCES site(id),
    CONSTRAINT uq_certificate_site UNIQUE (certificate_id, site_id)
);

CREATE INDEX ix_certificate_customer ON certificate(customer_id);
CREATE INDEX ix_certificate_expiration_active ON certificate(expiration_date, active);
CREATE INDEX ix_certificate_name ON certificate(certificate_name);
CREATE INDEX ix_certificate_system_reference ON certificate_system_integration(system_integration_id, certificate_id);
CREATE INDEX ix_certificate_site_reference ON certificate_site(site_id, certificate_id);
