ALTER TABLE hotels ADD COLUMN image_url VARCHAR(1000);

UPDATE hotels
SET image_url = CASE id % 6
    WHEN 0 THEN 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1400&q=85'
    WHEN 1 THEN 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=1400&q=85'
    WHEN 2 THEN 'https://images.unsplash.com/photo-1564501049412-61c2a3083791?auto=format&fit=crop&w=1400&q=85'
    WHEN 3 THEN 'https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1400&q=85'
    WHEN 4 THEN 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=1400&q=85'
    ELSE 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1400&q=85'
END;
