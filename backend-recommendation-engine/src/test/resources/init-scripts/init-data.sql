-- Insert Users
INSERT INTO `USER` (id, email, password, username, privilege, created_at, updated_at)
VALUES
(1, 'user1@gmail.com', 'password', 'user1 user1', 'USER', NOW(), NOW()),
(2, 'user2@gmail.com', 'password', 'user2 user2', 'USER', NOW(), NOW()),
(3, 'user3@gmail.com', 'password', 'user3 user3', 'USER', NOW(), NOW()),
(4, 'user4@gmail.com', 'password', 'user4 user4', 'USER', NOW(), NOW()),
(5, 'user5@gmail.com', 'password', 'user5 user5', 'USER', NOW(), NOW());

-- Insert Tags
INSERT INTO `TAG` (id, name, created_at, updated_at)
VALUES
(1, 'ROCK', NOW(), NOW()),
(2, 'POP', NOW(), NOW()),
(3, 'INDIE', NOW(), NOW());

-- Insert Songs
INSERT INTO `SONG` (id, image, name, name_hashed, listen_count, protection_type, user_id, created_at, updated_at)
VALUES
(1, 'hashed_name', 'song1', 'hashed_name', 0, 2, 1, NOW(), NOW()),
(2, 'hashed_name', 'song2', 'hashed_name', 0, 2, 1, NOW(), NOW()),
(3, 'hashed_name', 'song3', 'hashed_name', 0, 2, 1, NOW(), NOW()),
(4, 'hashed_name', 'song4', 'hashed_name', 0, 2, 1, NOW(), NOW()),
(5, 'hashed_name', 'song5', 'hashed_name', 0, 2, 1, NOW(), NOW()),
(6, 'hashed_name', 'song6', 'hashed_name', 0, 2, 1, NOW(), NOW()),
(7, 'hashed_name', 'song7', 'hashed_name', 0, 2, 1, NOW(), NOW()),
(8, 'hashed_name', 'song8', 'hashed_name', 0, 2, 1, NOW(), NOW()),
(9, 'hashed_name', 'song9', 'hashed_name', 0, 2, 1, NOW(), NOW()),
(10, 'hashed_name', 'song10', 'hashed_name', 0, 2, 1, NOW(), NOW());

-- Map Tags to Songs (all songs use the first tag: 'ROCK')
INSERT INTO `tag_song` (song_id, tag_id)
VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(6, 1),
(7, 1),
(8, 1),
(9, 1),
(10, 1);