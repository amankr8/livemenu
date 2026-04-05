-- V11__add_latitude_longitude_to_kitchens.sql
-- Add latitude and longitude columns to kitchens table for storing location coordinates

ALTER TABLE kitchens
ADD COLUMN k_lat VARCHAR(255),
ADD COLUMN k_lng VARCHAR(255);

-- Create an index on latitude and longitude for efficient queries
CREATE INDEX idx_kitchens_coordinates ON kitchens(k_lat, k_lng);

