CREATE SCHEMA IF NOT EXISTS hostel;

INSERT INTO hostel.hostels (
    hostel_code,
    hostel_name,
    hostel_type,
    address,
    created_by,
    created_at,
    active_flag
) VALUES
    ('BH-01', 'Boys Hostel A', 'Boys', 'North Block, Main Campus', 'system', CURRENT_TIMESTAMP, 'Y'),
    ('GH-01', 'Girls Hostel A', 'Girls', 'East Block, Main Campus', 'system', CURRENT_TIMESTAMP, 'Y'),
    ('STF-01', 'Staff Quarters A', 'Staff', 'South Block, Main Campus', 'system', CURRENT_TIMESTAMP, 'Y'),
    ('GST-01', 'Guest House A', 'Guest', 'Visitor Block, Main Campus', 'system', CURRENT_TIMESTAMP, 'Y')
ON CONFLICT (hostel_code) DO NOTHING;

INSERT INTO hostel.hostel_rooms (
    hostel_id,
    room_number,
    floor_number,
    room_type,
    capacity,
    occupied_count,
    created_by,
    created_at,
    active_flag
)
SELECT hostel_id, room_number, floor_number, room_type, capacity, 0, 'system', CURRENT_TIMESTAMP, 'Y'
FROM (
    VALUES
        ('BH-01', 'B101', 1, 'Double Sharing', 2),
        ('BH-01', 'B102', 1, 'Double Sharing', 2),
        ('BH-01', 'B201', 2, 'Triple Sharing', 3),
        ('BH-01', 'B202', 2, 'Triple Sharing', 3),
        ('GH-01', 'G101', 1, 'Double Sharing', 2),
        ('GH-01', 'G102', 1, 'Double Sharing', 2),
        ('GH-01', 'G201', 2, 'Triple Sharing', 3),
        ('GH-01', 'G202', 2, 'Triple Sharing', 3),
        ('STF-01', 'S101', 1, 'Single', 1),
        ('STF-01', 'S102', 1, 'Single', 1),
        ('GST-01', 'V101', 1, 'Guest Room', 2),
        ('GST-01', 'V102', 1, 'Guest Room', 2)
) AS seed(hostel_code, room_number, floor_number, room_type, capacity)
JOIN hostel.hostels h ON h.hostel_code = seed.hostel_code
ON CONFLICT (hostel_id, room_number) DO NOTHING;
