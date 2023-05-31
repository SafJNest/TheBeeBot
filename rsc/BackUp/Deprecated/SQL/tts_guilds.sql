CREATE TABLE
  `tts_guilds` (
    `discord_id` varchar(19) NOT NULL,
    `bot_id` varchar(19) NOT NULL,
    `name_tts` varchar(6) NOT NULL,
    `language_tts` varchar(5) NOT NULL,
    PRIMARY KEY (`discord_id`, `bot_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1