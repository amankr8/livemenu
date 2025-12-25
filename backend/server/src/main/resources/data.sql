-- Insert Kitchens
INSERT INTO kitchens (k_id, k_name, k_tagline, k_address, k_subdomain, k_whatsapp, created_at, updated_at)
VALUES (1, 'Biryani Hub', 'Authentic Hyderabadi', 'Indiranagar, Bangalore', 'biryanihub', '919876543210', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO kitchens (k_id, k_name, k_tagline, k_address, k_subdomain, k_whatsapp, created_at, updated_at)
VALUES (2, 'Pizza Party', 'Cheesy Delights', 'Koramangala, Bangalore', 'pizzaparty', '919876543211', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Menu Items for Biryani Hub (k_id = 1)
-- Categories: STARTERS, MAIN_COURSE, DESSERT (Assuming these match your Category Enum)
INSERT INTO menu_items (mi_id, k_id, mi_name, mi_desc, mi_category, mi_in_stock, mi_is_veg, mi_price, created_at, updated_at)
VALUES (101, 1, 'Chicken Dum Biryani', 'Slow cooked with basmati rice', 'MAIN_COURSE', true, false, 350.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO menu_items (mi_id, k_id, mi_name, mi_desc, mi_category, mi_in_stock, mi_is_veg, mi_price, created_at, updated_at)
VALUES (102, 1, 'Gobi 65', 'Spicy fried cauliflower', 'STARTERS', true, true, 180.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert Menu Items for Pizza Party (k_id = 2)
INSERT INTO menu_items (mi_id, k_id, mi_name, mi_desc, mi_category, mi_in_stock, mi_is_veg, mi_price, created_at, updated_at)
VALUES (201, 2, 'Margherita Pizza', 'Classic cheese and basil', 'MAIN_COURSE', true, true, 299.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);