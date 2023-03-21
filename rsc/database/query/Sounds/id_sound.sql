CREATE TABLE IF NOT EXISTS welcome_message (
   discord_id char(18) PRIMARY KEY,
   channel_id char(18),
   message_text varchar(1024)
);
CREATE TABLE IF NOT EXISTS welcome_roles (
   role_id char(18) unique PRIMARY KEY,
   discord_id char(18), 
   FOREIGN KEY(discord_id) REFERENCES welcome_message(discord_id)
);


CREATE TABLE IF NOT EXISTS TTS_guilds (
   discord_id char(18) UNIQUE PRIMARY KEY,
   name_tts varchar(20),
   language_tts varchar(5)
);

CREATE TABLE IF NOT EXISTS rooms_nickname (
   discord_id char(18),
   room_id char(18),
   room_name varchar(255),
   PRIMARY KEY(discord_id, room_id)
);

CREATE TABLE IF NOT EXISTS LOL_User (
   discord_id char(18) UNIQUE PRIMARY KEY,
   summoner_id varchar(255),
   account_id varchar(255),
   account_name varchar(255)
);

CREATE TABLE IF NOT EXISTS sound (
   id int UNIQUE PRIMARY KEY,
   name varchar(255),
   guild_id char(18),
   user_id char(18),
   extension varchar(255)
);

CREATE TABLE IF NOT EXISTS play (
   user_id char(18),
   id_sound int not null,
   times int not null,
   PRIMARY KEY(user_id, id_sound),
   FOREIGN KEY (id_sound) REFERENCES sound_id(id)
);

CREATE TABLE IF NOT EXISTS guild_settings (
   guild_id char(18) not null,
   bot_id  char(18) not null,
   prefix varchar(255) not null,
   PRIMARY KEY(guild_id, bot_id)
);