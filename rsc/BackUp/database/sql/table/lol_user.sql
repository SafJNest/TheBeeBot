CREATE TABLE
  `lol_user` (
    `guild_id` varchar(19) NOT NULL,
    `summoner_id` varchar(255) NOT NULL,
    `account_id` varchar(255) NOT NULL,
    PRIMARY KEY (`guild_id`, `summoner_id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1