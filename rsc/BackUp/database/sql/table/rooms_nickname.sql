CREATE TABLE
  `rooms_nickname` (
    `discord_id` varchar(19) NOT NULL,
    `room_id` varchar(19) NOT NULL,
    `room_name` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`discord_id`, `room_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1