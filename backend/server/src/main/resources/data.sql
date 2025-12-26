-- 1. Insert Auth Users (Passwords are 'password' BCrypt hashed)
INSERT INTO auth_users (au_id, au_username, au_password, au_authority, created_at, updated_at)
VALUES (2, 'biryani_admin', '$2a$10$8.UnVuG9HHgffUDAlk8qnOOu3cwW6SNo97U6T7.6q96GqV3r.G7dG', 'KITCHEN_OWNER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO auth_users (au_id, au_username, au_password, au_authority, created_at, updated_at)
VALUES (3, 'pizza_admin', '$2a$10$8.UnVuG9HHgffUDAlk8qnOOu3cwW6SNo97U6T7.6q96GqV3r.G7dG', 'KITCHEN_OWNER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. Insert Kitchens
INSERT INTO kitchens (k_id, k_name, k_tagline, k_address, k_subdomain, k_whatsapp, created_at, updated_at)
VALUES (1, 'Biryani Hub', 'Authentic Hyderabadi', 'Indiranagar, Bangalore', 'biryanihub', '919876543210', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO kitchens (k_id, k_name, k_tagline, k_address, k_subdomain, k_whatsapp, created_at, updated_at)
VALUES (2, 'Pizza Party', 'Cheesy Delights', 'Koramangala, Bangalore', 'pizzaparty', '919876543211', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. Link Auth Users to Kitchens via KitchenOwners
INSERT INTO kitchen_owners (ko_id, au_id, k_id, created_at, updated_at)
VALUES (1, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO kitchen_owners (ko_id, au_id, k_id, created_at, updated_at)
VALUES (2, 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. Insert Menu Items
INSERT INTO menu_items (mi_id, k_id, mi_name, mi_desc, mi_category, mi_in_stock, mi_is_veg, mi_price, created_at, updated_at)
VALUES (101, 1, 'Chicken Dum Biryani', 'Slow cooked with basmati rice', 'MAIN_COURSE', true, false, 350.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO menu_items (mi_id, k_id, mi_name, mi_desc, mi_category, mi_in_stock, mi_is_veg, mi_price, created_at, updated_at)
VALUES (102, 1, 'Gobi 65', 'Spicy fried cauliflower', 'STARTERS', true, true, 180.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO menu_items (mi_id, k_id, mi_name, mi_desc, mi_category, mi_in_stock, mi_is_veg, mi_price, created_at, updated_at)
VALUES (201, 2, 'Margherita Pizza', 'Classic cheese and basil', 'MAIN_COURSE', true, true, 299.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);