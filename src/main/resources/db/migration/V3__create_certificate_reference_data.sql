CREATE TABLE certificate_type (
    id BIGINT IDENTITY(1,1) NOT NULL,
    type_code VARCHAR(50) NOT NULL,
    display_name NVARCHAR(100) NOT NULL,
    active BIT NOT NULL CONSTRAINT df_certificate_type_active DEFAULT 1,
    CONSTRAINT pk_certificate_type PRIMARY KEY (id),
    CONSTRAINT uq_certificate_type_code UNIQUE (type_code)
);

CREATE TABLE function_usage (
    id BIGINT IDENTITY(1,1) NOT NULL,
    usage_code VARCHAR(50) NOT NULL,
    display_name NVARCHAR(100) NOT NULL,
    active BIT NOT NULL CONSTRAINT df_function_usage_active DEFAULT 1,
    CONSTRAINT pk_function_usage PRIMARY KEY (id),
    CONSTRAINT uq_function_usage_code UNIQUE (usage_code)
);

CREATE TABLE deployment_environment (
    id BIGINT IDENTITY(1,1) NOT NULL,
    environment_code VARCHAR(50) NOT NULL,
    display_name NVARCHAR(100) NOT NULL,
    active BIT NOT NULL CONSTRAINT df_environment_active DEFAULT 1,
    CONSTRAINT pk_deployment_environment PRIMARY KEY (id),
    CONSTRAINT uq_environment_code UNIQUE (environment_code)
);

CREATE TABLE system_integration (
    id BIGINT IDENTITY(1,1) NOT NULL,
    system_code VARCHAR(100) NOT NULL,
    display_name NVARCHAR(200) NOT NULL,
    active BIT NOT NULL CONSTRAINT df_system_active DEFAULT 1,
    created_at DATETIME2(3) NOT NULL CONSTRAINT df_system_created DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2(3) NOT NULL CONSTRAINT df_system_updated DEFAULT SYSUTCDATETIME(),
    version BIGINT NOT NULL CONSTRAINT df_system_version DEFAULT 0,
    CONSTRAINT pk_system_integration PRIMARY KEY (id),
    CONSTRAINT uq_system_integration_code UNIQUE (system_code)
);

CREATE INDEX ix_system_integration_active_name ON system_integration(active, display_name);
