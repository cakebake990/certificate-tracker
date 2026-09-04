CREATE TABLE customer (
    id BIGINT IDENTITY(1,1) NOT NULL,
    customer_code VARCHAR(50) NOT NULL,
    customer_name NVARCHAR(200) NOT NULL,
    active BIT NOT NULL CONSTRAINT df_customer_active DEFAULT 1,
    created_at DATETIME2(3) NOT NULL CONSTRAINT df_customer_created DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2(3) NOT NULL CONSTRAINT df_customer_updated DEFAULT SYSUTCDATETIME(),
    version BIGINT NOT NULL CONSTRAINT df_customer_version DEFAULT 0,
    CONSTRAINT pk_customer PRIMARY KEY (id),
    CONSTRAINT uq_customer_code UNIQUE (customer_code)
);

CREATE TABLE site (
    id BIGINT IDENTITY(1,1) NOT NULL,
    customer_id BIGINT NOT NULL,
    site_code VARCHAR(50) NOT NULL,
    display_name NVARCHAR(200) NOT NULL,
    active BIT NOT NULL CONSTRAINT df_site_active DEFAULT 1,
    created_at DATETIME2(3) NOT NULL CONSTRAINT df_site_created DEFAULT SYSUTCDATETIME(),
    updated_at DATETIME2(3) NOT NULL CONSTRAINT df_site_updated DEFAULT SYSUTCDATETIME(),
    version BIGINT NOT NULL CONSTRAINT df_site_version DEFAULT 0,
    CONSTRAINT pk_site PRIMARY KEY (id),
    CONSTRAINT fk_site_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT uq_site_customer_code UNIQUE (customer_id, site_code)
);

CREATE INDEX ix_site_customer_active ON site(customer_id, active);
