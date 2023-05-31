CREATE TABLE
  `guild_settings` (
    `guild_id` varchar(19) NOT NULL,
    `bot_id` varchar(19) NOT NULL,
    `prefix` varchar(32) DEFAULT NULL,
    `has_slash` tinyint(1) DEFAULT 1,
    `name_tts` varchar(19) DEFAULT NULL,
    `language_tts` varchar(19) DEFAULT NULL,
    PRIMARY KEY (`guild_id`, `bot_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1