CREATE TABLE
  `lol_user` (
    `discord_id` varchar(19) NOT NULL,
    `summoner_id` varchar(255) NOT NULL,
    `account_id` varchar(255) NOT NULL,
    PRIMARY KEY (`discord_id`, `summoner_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1