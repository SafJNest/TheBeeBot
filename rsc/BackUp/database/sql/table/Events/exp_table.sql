CREATE TABLE
  `exp_table` (
    `user_id` varchar(19) NOT NULL,
    `guild_id` varchar(19) NOT NULL,
    `exp` mediumint(9) NOT NULL,
    `level` smallint(6) NOT NULL,
    `messages` mediumint(9) DEFAULT NULL,
    PRIMARY KEY (`user_id`, `guild_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1