CREATE OR REPLACE VIEW active_assets AS
SELECT asset_id, asset_name, type, location
FROM assets
WHERE status = 'Active';