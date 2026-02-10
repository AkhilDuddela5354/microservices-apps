CREATE TABLE asset
(
    asset_id       VARCHAR(255) NOT NULL,
    asset_name     VARCHAR(255),
    type           VARCHAR(255),
    installed_date date,
    location       VARCHAR(255),
    status         VARCHAR(255),
    CONSTRAINT pk_asset PRIMARY KEY (asset_id)
);