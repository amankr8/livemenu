-- V13__add_delivery_radius_to_kitchens.sql
-- Add delivery radius column to kitchens table for storing delivery distance in km

ALTER TABLE kitchens
ADD COLUMN k_delivery_radius INT;

-- Create an index on delivery_radius for efficient queries
CREATE INDEX idx_kitchens_delivery_radius ON kitchens(k_delivery_radius);

