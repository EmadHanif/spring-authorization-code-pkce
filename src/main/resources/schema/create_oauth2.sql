/*
    For PostgreSQL, replaced 'timestamp' with 'timstamptz' and 'blob' with 'text'
 */

CREATE TABLE IF NOT EXISTS oauth2_registered_client
(
    id                            varchar(100)                            NOT NULL,
    client_id                     varchar(100)                            NOT NULL,
    client_id_issued_at           timestamptz   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 varchar(200)  DEFAULT NULL,
    client_secret_expires_at      timestamptz   DEFAULT NULL,
    client_name                   varchar(200)                            NOT NULL,
    client_authentication_methods varchar(1000)                           NOT NULL,
    authorization_grant_types     varchar(1000)                           NOT NULL,
    redirect_uris                 varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris     varchar(1000) DEFAULT NULL,
    scopes                        varchar(1000)                           NOT NULL,
    client_settings               varchar(2000)                           NOT NULL,
    token_settings                varchar(2000)                           NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS oauth2_authorization
(
    id                            varchar(100) NOT NULL,
    registered_client_id          varchar(100) NOT NULL,
    principal_name                varchar(200) NOT NULL,
    authorization_grant_type      varchar(100) NOT NULL,
    authorized_scopes             varchar(1000) DEFAULT NULL,
    attributes                    text          DEFAULT NULL,
    state                         varchar(500)  DEFAULT NULL,
    authorization_code_value      text          DEFAULT NULL,
    authorization_code_issued_at  timestamptz   DEFAULT NULL,
    authorization_code_expires_at timestamptz   DEFAULT NULL,
    authorization_code_metadata   text          DEFAULT NULL,
    access_token_value            text          DEFAULT NULL,
    access_token_issued_at        timestamptz   DEFAULT NULL,
    access_token_expires_at       timestamptz   DEFAULT NULL,
    access_token_metadata         text          DEFAULT NULL,
    access_token_type             varchar(100)  DEFAULT NULL,
    access_token_scopes           varchar(1000) DEFAULT NULL,
    oidc_id_token_value           text          DEFAULT NULL,
    oidc_id_token_issued_at       timestamptz   DEFAULT NULL,
    oidc_id_token_expires_at      timestamptz   DEFAULT NULL,
    oidc_id_token_metadata        text          DEFAULT NULL,
    refresh_token_value           text          DEFAULT NULL,
    refresh_token_issued_at       timestamptz   DEFAULT NULL,
    refresh_token_expires_at      timestamptz   DEFAULT NULL,
    refresh_token_metadata        text          DEFAULT NULL,
    user_code_value               text          DEFAULT NULL,
    user_code_issued_at           timestamptz   DEFAULT NULL,
    user_code_expires_at          timestamptz   DEFAULT NULL,
    user_code_metadata            text          DEFAULT NULL,
    device_code_value             text          DEFAULT NULL,
    device_code_issued_at         timestamptz   DEFAULT NULL,
    device_code_expires_at        timestamptz   DEFAULT NULL,
    device_code_metadata          text          DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX idx_oauth2_access_token
    ON oauth2_authorization(access_token_value)
    WHERE access_token_value IS NOT NULL;

CREATE UNIQUE INDEX idx_oauth2_refresh_token
    ON oauth2_authorization(refresh_token_value)
    WHERE refresh_token_value IS NOT NULL;

CREATE UNIQUE INDEX idx_oauth2_authorization_code
    ON oauth2_authorization(authorization_code_value)
    WHERE authorization_code_value IS NOT NULL;

CREATE INDEX idx_oauth2_client_id ON oauth2_authorization(registered_client_id);
CREATE INDEX idx_oauth2_principal_name ON oauth2_authorization(principal_name);