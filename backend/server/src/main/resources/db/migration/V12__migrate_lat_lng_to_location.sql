-- V12__migrate_lat_lng_to_location.sql
-- Migrate from separate lat/lng columns to a combined location column (format: lat,lng)

-- Step 1: Add the new k_location column
ALTER TABLE kitchens
ADD COLUMN k_location VARCHAR(255);

-- Step 2: Migrate existing data from lat,lng to location format
UPDATE kitchens
SET k_location = k_lat || ',' || k_lng
WHERE k_lat IS NOT NULL AND k_lng IS NOT NULL;

-- Step 3: Drop the old index on lat and lng
DROP INDEX IF EXISTS idx_kitchens_coordinates;

-- Step 4: Drop the old lat and lng columns
ALTER TABLE kitchens
DROP COLUMN k_lat,
DROP COLUMN k_lng;

-- Step 5: Create an index on the new location column for efficient queries
CREATE INDEX idx_kitchens_location ON kitchens(k_location);

