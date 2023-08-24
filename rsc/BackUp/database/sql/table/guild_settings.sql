CREATE TABLE
  `guild_settings` (
    `guild_id` varchar(19) NOT NULL,
    `bot_id` varchar(19) NOT NULL,
    `prefix` varchar(32) DEFAULT NULL,
    `name_tts` varchar(19) DEFAULT NULL,
    `language_tts` varchar(19) DEFAULT NULL,
    `exp_enabled` tinyint(1) DEFAULT 0,
    PRIMARY KEY (`guild_id`, `bot_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1