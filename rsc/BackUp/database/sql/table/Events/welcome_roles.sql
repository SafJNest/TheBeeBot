CREATE TABLE
  `welcome_roles` (
    `role_id` varchar(19) NOT NULL,
    `guild_id` varchar(19) NOT NULL,
    `bot_id` varchar(19) NOT NULL,
    PRIMARY KEY (`role_id`, `bot_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1