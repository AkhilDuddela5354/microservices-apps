CREATE TABLE assets
(
    asset_id       VARCHAR(255) NOT NULL,
    asset_name     VARCHAR(255),
    type           VARCHAR(255),
    installed_date DATE,
    location       VARCHAR(255),
    status         VARCHAR(255),
    CONSTRAINT pk_asset PRIMARY KEY (asset_id)
);