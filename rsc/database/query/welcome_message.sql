CREATE TABLE welcome_message(
   discord_id   VARCHAR(18) NOT NULL,
   bot_id       VARCHAR(19) NOT NULL,
   channel_id   VARCHAR(18) NOT NULL,
   message_text VARCHAR(68) NOT NULL,
   PRIMARY KEY(discord_id, bot_id)
);