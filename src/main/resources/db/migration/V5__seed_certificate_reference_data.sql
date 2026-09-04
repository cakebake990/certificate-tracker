INSERT INTO certificate_type (type_code, display_name) VALUES
('TLS_SSL', 'TLS/SSL'), ('CLIENT_AUTH', 'Client Authentication'), ('SIGNING', 'Signing'), ('SAML', 'SAML'), ('OTHER', 'Other');

INSERT INTO function_usage (usage_code, display_name) VALUES
('APPLICATION', 'Application'), ('INTERFACE_INTEGRATION', 'Interface / Integration'),
('API', 'API'), ('SSO_AUTHENTICATION', 'SSO / Authentication'), ('WEB_TLS', 'Web / TLS'),
('CLIENT_AUTHENTICATION', 'Client Authentication'), ('SIGNING', 'Signing'),
('DATABASE', 'Database'), ('OTHER', 'Other');

INSERT INTO deployment_environment (environment_code, display_name) VALUES
('PRODUCTION', 'Production'), ('STAGING', 'Staging'), ('TEST', 'Test'), ('DR', 'DR'), ('OTHER', 'Other');
