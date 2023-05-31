CREATE TABLE
  `sound` (
    `id` smallint(6) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `guild_id` varchar(19) NOT NULL,
    `user_id` varchar(19) NOT NULL,
    `extension` varchar(4) NOT NULL,
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB AUTO_INCREMENT = 178 DEFAULT CHARSET = latin1