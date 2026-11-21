WITH cities AS (
    SELECT
        hotel_number,
        (ARRAY['Goa', 'Jaipur', 'Mumbai', 'Delhi', 'Bengaluru', 'Kochi', 'Udaipur', 'Manali', 'Pondicherry', 'Hyderabad'])
            [((hotel_number - 1) % 10) + 1] AS city
    FROM generate_series(1, 200) AS series(hotel_number)
),
seeded_hotels AS (
    INSERT INTO hotels (name, city, country, rating, description)
    SELECT
        city || ' Stay ' || hotel_number,
        city,
        'India',
        ROUND((3.0 + random() * 2.0)::numeric, 1),
        'Seeded hotel for local development and load testing.'
    FROM cities
    RETURNING id
)
INSERT INTO rooms (hotel_id, room_type, price_per_night, capacity)
SELECT
    hotel.id,
    room_template.room_type,
    room_template.base_price + FLOOR(random() * 1000)::numeric,
    room_template.capacity
FROM seeded_hotels AS hotel
CROSS JOIN (
    VALUES
        ('Standard', 2500.00::numeric, 2),
        ('Deluxe', 4000.00::numeric, 3),
        ('Suite', 6500.00::numeric, 4)
) AS room_template(room_type, base_price, capacity);
