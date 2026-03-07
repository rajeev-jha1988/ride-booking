-- Ensure extensions are enabled
CREATE EXTENSION IF NOT EXISTS "postgis";

CREATE TABLE IF NOT EXISTS ride (
    -- UUID Primary Key
                          id UUID PRIMARY KEY ,
                          name VARCHAR(255) NOT NULL,
                          address TEXT NOT NULL,
                          type VARCHAR(255) NOT NULL,
                          country VARCHAR(255) NOT NULL,
    -- Raw Coordinates
                          source_lat DOUBLE PRECISION NOT NULL,
                          source_long DOUBLE PRECISION NOT NULL,
                          destination_lat DOUBLE PRECISION NOT NULL,
                          destination_long DOUBLE PRECISION NOT NULL,
                          driver_id UUID,
                          status VARCHAR(255),
                          price DECIMAL(12, 2),
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          city_id VARCHAR(50) NOT NULL
);

-- CRITICAL: Index for the "Accept Ride" lookups
CREATE INDEX idx_ride_status_city ON ride (status, city_id);

CREATE TABLE IF NOT EXISTS driver (
                                      id UUID PRIMARY KEY ,
                                      name VARCHAR(255) NOT NULL,
                                      status VARCHAR(255),
                                      updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS location (
                                      id UUID PRIMARY KEY ,
                                      driver_id UUID NOT NULL,
                                      latitude DOUBLE PRECISION NOT NULL,
                                      longitude DOUBLE PRECISION NOT NULL,
                                      created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


