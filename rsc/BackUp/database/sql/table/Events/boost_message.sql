CREATE TABLE
  `boost_message` (
    `guild_id` varchar(19) NOT NULL,
    `bot_id` varchar(19) NOT NULL,
    `channel_id` varchar(19) NOT NULL,
    `message_text` varchar(255) NOT NULL,
    PRIMARY KEY (`guild_id`, `bot_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1