-- V10__refactor_menu_items_category_to_foreign_key.sql
-- Refactor menu_items table to use c_id foreign key instead of mi_category string column

-- Step 1: Add the new c_id foreign key column (nullable initially to handle existing data)
ALTER TABLE menu_items ADD COLUMN c_id BIGINT;

-- Step 2: Add the foreign key constraint
ALTER TABLE menu_items
ADD CONSTRAINT fk_menu_items_category FOREIGN KEY (c_id) REFERENCES categories(c_id);

-- Step 3: Create an index on the c_id column for better query performance
CREATE INDEX idx_menu_items_category ON menu_items(c_id);

-- Step 4: Drop the old mi_category column (this assumes data migration is not needed or has been handled separately)
ALTER TABLE menu_items DROP COLUMN mi_category;

