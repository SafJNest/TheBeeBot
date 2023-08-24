CREATE TABLE
  `rewards_table` (
    `guild_id` varchar(19) NOT NULL,
    `role_id` varchar(19) NOT NULL,
    `level` smallint(6) NOT NULL,
    `message_text` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`guild_id`, `role_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1