CREATE TABLE `lol_user` (
  `discord_id` varchar(18) NOT NULL,
  `summoner_id` varchar(48) NOT NULL,
  `account_id` varchar(56) NOT NULL,
  PRIMARY KEY (`discord_id`, 'summoner_id')