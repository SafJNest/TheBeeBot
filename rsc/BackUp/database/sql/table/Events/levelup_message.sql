CREATE TABLE
  `levelup_message` (
    `guild_id` varchar(19) NOT NULL,
    `message_text` varchar(255) NOT NULL,
    PRIMARY KEY (`guild_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1