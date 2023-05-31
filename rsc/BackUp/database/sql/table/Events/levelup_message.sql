CREATE TABLE
  `levelup_message` (
    `discord_id` varchar(19) NOT NULL,
    `message_text` varchar(255) NOT NULL,
    PRIMARY KEY (`discord_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1