-- Ensure extensions are enabled
CREATE EXTENSION IF NOT EXISTS "postgis";

CREATE TABLE IF NOT EXISTS business (
    -- UUID Primary Key
                          id UUID PRIMARY KEY ,
                          name VARCHAR(255) NOT NULL,
                          address TEXT NOT NULL,
                          type VARCHAR(255) NOT NULL,
                          country VARCHAR(255) NOT NULL,
    -- Raw Coordinates
                          longitude DOUBLE PRECISION NOT NULL,
                          latitude DOUBLE PRECISION NOT NULL

);

CREATE TABLE IF NOT EXISTS business_geo_hash_mapping (
                                        business_id UUID PRIMARY KEY, -- Links to the sharded table
                                        geom GEOMETRY(Point, 4326) NOT NULL,
                                        geohash TEXT GENERATED ALWAYS AS (ST_GeoHash(geom, 6)) STORED,
                                        business_type VARCHAR(50) -- Optional: for filtering by type during search
);

-- Spatial Index for "Find Nearby" queries
CREATE INDEX IF NOT EXISTS idx_spatial_geom ON business_geo_hash_mapping USING GIST (geom);
-- Standard Index for Geohash lookups
CREATE INDEX IF NOT EXISTS idx_spatial_geohash ON business_geo_hash_mapping (geohash);

