CREATE TABLE log_request (
    id BIGSERIAL PRIMARY KEY,
    context_method VARCHAR(16) NOT NULL,
    context_headers TEXT,
    context_body TEXT,
    request_remote_address VARCHAR(255),
    request_cookie_map TEXT,
    info_path VARCHAR(2000),
    info_path_parameters TEXT,
    info_query_parameters TEXT,
    duration INTEGER,
    request_origin TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_log_request_created_at
    ON log_request (created_at DESC);

CREATE INDEX idx_log_request_info_path
    ON log_request (info_path);

CREATE INDEX idx_log_request_remote_address
    ON log_request (request_remote_address);
