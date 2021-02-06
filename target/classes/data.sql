
-- 会議室
INSERT INTO meeting_room (room_name) VALUES ('新木場');
INSERT INTO meeting_room (room_name) VALUES ('辰巳');
INSERT INTO meeting_room (room_name) VALUES ('豊洲');
INSERT INTO meeting_room (room_name) VALUES ('月島');
INSERT INTO meeting_room (room_name) VALUES ('新富町');
INSERT INTO meeting_room (room_name) VALUES ('銀座一丁目');
INSERT INTO meeting_room (room_name) VALUES ('有楽町');

-- 会議室の予約可能日
-- room_id = 1(新木場)の予約可能日
INSERT INTO reservable_room (reserved_date, room_id) VALUES (CURRENT_DATE, 1),(CURRENT_DATE+1, 1),(CURRENT_DATE+2, 1)
,(CURRENT_DATE+3, 1),(CURRENT_DATE+4, 1),(CURRENT_DATE+5, 1),(CURRENT_DATE+6, 1),(CURRENT_DATE+7, 1),(CURRENT_DATE+8, 1)
,(CURRENT_DATE+9, 1), (CURRENT_DATE+10, 1);

-- room_id = 2(辰巳)の予約可能日
INSERT INTO reservable_room (reserved_date, room_id) VALUES (CURRENT_DATE, 2),(CURRENT_DATE+1, 2),(CURRENT_DATE+2, 2)
,(CURRENT_DATE+3, 2),(CURRENT_DATE+4, 2),(CURRENT_DATE+5, 2),(CURRENT_DATE+6, 2),(CURRENT_DATE+7, 2),(CURRENT_DATE+8, 2)
,(CURRENT_DATE+9, 2), (CURRENT_DATE+10, 2);

-- room_id = 3(豊洲)の予約可能日
INSERT INTO reservable_room (reserved_date, room_id) VALUES (CURRENT_DATE, 3),(CURRENT_DATE+1, 3),(CURRENT_DATE+2, 3)
,(CURRENT_DATE+3, 3),(CURRENT_DATE+4, 3),(CURRENT_DATE+5, 3),(CURRENT_DATE+6, 3),(CURRENT_DATE+7, 3),(CURRENT_DATE+8, 3)
,(CURRENT_DATE+9, 3), (CURRENT_DATE+10, 3);

-- room_id = 4(月島)の予約可能日
INSERT INTO reservable_room (reserved_date, room_id) VALUES (CURRENT_DATE, 4),(CURRENT_DATE+1, 4),(CURRENT_DATE+2, 4)
,(CURRENT_DATE+3, 4),(CURRENT_DATE+4, 4),(CURRENT_DATE+5, 4),(CURRENT_DATE+6, 4),(CURRENT_DATE+7, 4),(CURRENT_DATE+8, 4)
,(CURRENT_DATE+9, 4), (CURRENT_DATE+10, 4);

-- room_id = 5(新富町)の予約可能日
INSERT INTO reservable_room (reserved_date, room_id) VALUES (CURRENT_DATE, 5),(CURRENT_DATE+1, 5),(CURRENT_DATE+2, 5)
,(CURRENT_DATE+3, 5),(CURRENT_DATE+4, 5),(CURRENT_DATE+5, 5),(CURRENT_DATE+6, 5),(CURRENT_DATE+7, 5),(CURRENT_DATE+8, 5)
,(CURRENT_DATE+9, 5), (CURRENT_DATE+10, 5);

-- room_id = 6(銀座一丁目)の予約可能日
INSERT INTO reservable_room (reserved_date, room_id) VALUES (CURRENT_DATE, 6),(CURRENT_DATE+1, 6),(CURRENT_DATE+2, 6)
,(CURRENT_DATE+3, 6),(CURRENT_DATE+4, 6),(CURRENT_DATE+5, 6),(CURRENT_DATE+6, 6),(CURRENT_DATE+7, 6),(CURRENT_DATE+8, 6)
,(CURRENT_DATE+9, 6), (CURRENT_DATE+10, 6);

-- room_id = 7(有楽町)の予約可能日
INSERT INTO reservable_room (reserved_date, room_id) VALUES (CURRENT_DATE, 7),(CURRENT_DATE+1, 7),(CURRENT_DATE+2, 7)
,(CURRENT_DATE+3, 7),(CURRENT_DATE+4, 7),(CURRENT_DATE+5, 7),(CURRENT_DATE+6, 7),(CURRENT_DATE+7, 7),(CURRENT_DATE+8, 7)
,(CURRENT_DATE+9, 7), (CURRENT_DATE+10, 7);

-- 認証確認用のテストユーザー
INSERT INTO usr (user_id, first_name, last_name, password, phone_number, zip_code, address, role_name) VALUES('hoge@gmail.com', 'Aaa', 'Aaa', '$2a$10$oxSJl.keBwxmsMLkcT9lPeAIxfNTPNQxpeywMrF7A3kVszwUTqfTK', '08048262740', '5918023','大阪府堺市北区中百舌鳥町6丁目962岡田マンション406号室','USER');
INSERT INTO usr (user_id, first_name, last_name, password, phone_number, zip_code, address, role_name) VALUES('bbbb_munemoto@gmail.com', 'Bbb', 'Bbb', '$2a$10$oxSJl.keBwxmsMLkcT9lPeAIxfNTPNQxpeywMrF7A3kVszwUTqfTK', '08048262740', '5918023','大阪府堺市北区中百舌鳥町6丁目962岡田マンション406号室','USER');
INSERT INTO usr (user_id, first_name, last_name, password, phone_number, zip_code, address, role_name) VALUES('cccc_munemoto@gmail.com', 'Ccc', 'Ccc', '$2a$10$oxSJl.keBwxmsMLkcT9lPeAIxfNTPNQxpeywMrF7A3kVszwUTqfTK', '08048262740', '5918023','大阪府堺市北区中百舌鳥町6丁目962岡田マンション406号室','ADMIN');



