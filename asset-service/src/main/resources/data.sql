-- public class Asset {
--
--     @Id
--     private String assetId;
--     private String assetName;
--     private String type;
--     private LocalDate installedDate;
--     private String location;
--     private String status;
--
-- }

-- generate random data for asset table, assetId is UUID, assetName is random string, type is random string, installedDate is random date, location is random string, status is random string

INSERT INTO asset(asset_id, asset_name, type, installed_date, location, status) VALUES
(UUID(), 'Wind Turbine 101', 'Turbine', '2022-01-01', 'Hyderabad', 'Active'),
(UUID(), 'Solar Panel 202', 'Solar Panel', '2021-06-15', 'Bangalore', 'Active'),
(UUID(), 'Hydro Generator 303', 'Generator', '2020-03-20', 'Chennai', 'Inactive'),
(UUID(), 'Battery Storage 404', 'Battery', '2023-02-10', 'Mumbai', 'Active'),
(UUID(), 'Wind Turbine 102', 'Turbine', '2022-05-12', 'Hyderabad', 'Active'),
(UUID(), 'Solar Panel 203', 'Solar Panel', '2021-08-15', 'Bangalore', 'Inactive'),
(UUID(), 'Hydro Generator 304', 'Generator', '2020-07-20', 'Chennai', 'Active'),
(UUID(), 'Battery Storage 405', 'Battery', '2023-03-10', 'Mumbai', 'Inactive'),
(UUID(), 'Wind Turbine 103', 'Turbine', '2022-09-01', 'Hyderabad', 'Active'),
(UUID(), 'Solar Panel 204', 'Solar Panel', '2021-11-15', 'Bangalore', 'Active'),
(UUID(), 'Hydro Generator 305', 'Generator', '2020-10-20', 'Chennai', 'Inactive'),
(UUID(), 'Battery Storage 406', 'Battery', '2023-04-10', 'Mumbai', 'Active');