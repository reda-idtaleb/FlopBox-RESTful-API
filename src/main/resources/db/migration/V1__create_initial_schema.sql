CREATE TABLE app_users (
    id UUID PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE UNIQUE INDEX ux_app_users_username ON app_users (username);

CREATE TABLE ftp_servers (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL,
    alias VARCHAR(64) NOT NULL,
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_ftp_servers_owner FOREIGN KEY (owner_id) REFERENCES app_users (id) ON DELETE CASCADE,
    CONSTRAINT ck_ftp_servers_port CHECK (port BETWEEN 1 AND 65535),
    CONSTRAINT ux_ftp_servers_owner_alias UNIQUE (owner_id, alias)
);

CREATE INDEX ix_ftp_servers_owner_id ON ftp_servers (owner_id);
