CREATE TABLE
  `rooms_settings` (
    `guild_id` varchar(19) NOT NULL,
    `room_id` varchar(19) NOT NULL,
    `room_name` varchar(255) DEFAULT NULL,
    `has_exp` tinyint(1) DEFAULT 1,
    `exp_value` double DEFAULT 1,
    `has_command_stats` tinyint(1) DEFAULT 1,
    PRIMARY KEY (`guild_id`, `room_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1