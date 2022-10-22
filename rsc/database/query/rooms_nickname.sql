CREATE TABLE IF NOT EXISTS rooms_nickname (
   discord_id char(18),
   room_id char(18),
   room_name varchar(255),
   PRIMARY KEY(discord_id, room_id)
);





