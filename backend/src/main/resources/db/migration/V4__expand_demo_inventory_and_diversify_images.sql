-- Give every existing hotel a varied, stable photo. This also repairs databases
-- that were created before the image migration was introduced.
UPDATE hotels
SET image_url = CASE id % 12
    WHEN 0 THEN 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1400&q=85'
    WHEN 1 THEN 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=1400&q=85'
    WHEN 2 THEN 'https://images.unsplash.com/photo-1564501049412-61c2a3083791?auto=format&fit=crop&w=1400&q=85'
    WHEN 3 THEN 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1400&q=85'
    WHEN 4 THEN 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=1400&q=85'
    WHEN 5 THEN 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1400&q=85'
    WHEN 6 THEN 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=1400&q=85'
    WHEN 7 THEN 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=1400&q=85'
    WHEN 8 THEN 'https://images.unsplash.com/photo-1445019980597-93fa8acb246c?auto=format&fit=crop&w=1400&q=85'
    WHEN 9 THEN 'https://images.unsplash.com/photo-1455587734955-081b22074882?auto=format&fit=crop&w=1400&q=85'
    WHEN 10 THEN 'https://images.unsplash.com/photo-1551632436-cbf8dd35adfa?auto=format&fit=crop&w=1400&q=85'
    ELSE 'https://images.unsplash.com/photo-1601918774946-25832a4be0d6?auto=format&fit=crop&w=1400&q=85'
END
WHERE image_url IS NULL OR image_url = '';

-- Broaden the development catalogue beyond the initial India-only seed data.
-- These are clearly demo listings; production inventory requires a licensed
-- hotel-supply provider and its API credentials.
WITH destinations(name, city, country, rating, description, image_url) AS (
    VALUES
        ('Hudson Corner Hotel', 'New York', 'United States', 4.6, 'A thoughtful city base near the waterfront.', 'https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=1400&q=85'),
        ('Kensington Garden House', 'London', 'United Kingdom', 4.7, 'A calm stay for museum mornings and park walks.', 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=1400&q=85'),
        ('Montmartre Maison', 'Paris', 'France', 4.5, 'Light-filled rooms in a classic neighbourhood.', 'https://images.unsplash.com/photo-1445019980597-93fa8acb246c?auto=format&fit=crop&w=1400&q=85'),
        ('Shibuya Lantern Stay', 'Tokyo', 'Japan', 4.8, 'An easy base for late dinners and early trains.', 'https://images.unsplash.com/photo-1455587734955-081b22074882?auto=format&fit=crop&w=1400&q=85'),
        ('Harbour View Sydney', 'Sydney', 'Australia', 4.6, 'A relaxed harbour stay with clear, simple comforts.', 'https://images.unsplash.com/photo-1551632436-cbf8dd35adfa?auto=format&fit=crop&w=1400&q=85'),
        ('Marina Courtyard', 'Dubai', 'United Arab Emirates', 4.7, 'A quiet retreat close to the city energy.', 'https://images.unsplash.com/photo-1601918774946-25832a4be0d6?auto=format&fit=crop&w=1400&q=85')
), inserted AS (
    INSERT INTO hotels (name, city, country, rating, description, image_url)
    SELECT name, city, country, rating, description, image_url
    FROM destinations d
    WHERE NOT EXISTS (
        SELECT 1 FROM hotels h WHERE h.name = d.name AND h.city = d.city
    )
    RETURNING id
)
INSERT INTO rooms (hotel_id, room_type, price_per_night, capacity)
SELECT id, 'Deluxe', 6500.00, 2 FROM inserted;
