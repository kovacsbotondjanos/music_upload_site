-- Insert Users
INSERT INTO `USER` (id, email, password, username, privilege, created_at, updated_at) VALUES
(1, 'user1@gmail.com', 'password', 'user1 user1', 'USER', NOW(), NOW()),
(2, 'user2@gmail.com', 'password', 'user2 user2', 'USER', NOW(), NOW()),
(3, 'user3@gmail.com', 'password', 'user3 user3', 'USER', NOW(), NOW()),
(4, 'user4@gmail.com', 'password', 'user4 user4', 'USER', NOW(), NOW()),
(5, 'user5@gmail.com', 'password', 'user5 user5', 'USER', NOW(), NOW()),
(6, 'user6@gmail.com', 'password', 'user6 user6', 'USER', NOW(), NOW()),
(7, 'user7@gmail.com', 'password', 'user7 user7', 'USER', NOW(), NOW()),
(8, 'user8@gmail.com', 'password', 'user8 user8', 'USER', NOW(), NOW()),
(9, 'user9@gmail.com', 'password', 'user9 user9', 'USER', NOW(), NOW()),
(10, 'user10@gmail.com', 'password', 'user10 user10', 'USER', NOW(), NOW());

-- Insert Tags
INSERT INTO `TAG` (id, name, created_at, updated_at) VALUES
(1, 'ROCK', NOW(), NOW()), (2, 'POP', NOW(), NOW()), (3, 'INDIE', NOW(), NOW());

-- Insert Songs
INSERT INTO `SONG` (id, image, name, name_hashed, listen_count, protection_type, user_id, created_at, updated_at) VALUES
(1, 'hashed_name', 'song1', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(2, 'hashed_name', 'song2', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(3, 'hashed_name', 'song3', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(4, 'hashed_name', 'song4', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(5, 'hashed_name', 'song5', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(6, 'hashed_name', 'song6', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(7, 'hashed_name', 'song7', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(8, 'hashed_name', 'song8', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(9, 'hashed_name', 'song9', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(10, 'hashed_name', 'song10', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(11, 'hashed_name', 'song11', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(12, 'hashed_name', 'song12', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(13, 'hashed_name', 'song13', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(14, 'hashed_name', 'song14', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(15, 'hashed_name', 'song15', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(16, 'hashed_name', 'song16', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(17, 'hashed_name', 'song17', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(18, 'hashed_name', 'song18', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(19, 'hashed_name', 'song19', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(20, 'hashed_name', 'song20', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(21, 'hashed_name', 'song21', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(22, 'hashed_name', 'song22', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(23, 'hashed_name', 'song23', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(24, 'hashed_name', 'song24', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(25, 'hashed_name', 'song25', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(26, 'hashed_name', 'song26', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(27, 'hashed_name', 'song27', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(28, 'hashed_name', 'song28', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(29, 'hashed_name', 'song29', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW()),
(30, 'hashed_name', 'song30', 'hashed_name', 0, 'PUBLIC', 1, NOW(), NOW());

-- Map Tags to Songs (all songs use the first tag: 'ROCK')
INSERT INTO `tag_song` (song_id, tag_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1),
(6, 1), (7, 1), (8, 1), (9, 1), (10, 1),
(1, 2), (2, 2), (3, 2), (4, 2), (5, 2),
(6, 2), (7, 2), (8, 2), (9, 2), (10, 2),
(1, 3), (2, 3), (3, 3), (4, 3), (5, 3),
(6, 3), (7, 3), (8, 3), (9, 3), (10, 3),
(11, 1), (12, 2), (13, 1), (14, 3), (15, 2),
(16, 1), (17, 3), (18, 2), (19, 3), (20, 2),
(11, 3), (12, 3), (13, 2), (14, 2), (15, 1),
(16, 3), (17, 2), (18, 1), (19, 2), (20, 1),
(21, 3), (22, 3), (23, 2), (24, 2), (25, 1),
(26, 3), (27, 2), (28, 1), (29, 2), (30, 2);
