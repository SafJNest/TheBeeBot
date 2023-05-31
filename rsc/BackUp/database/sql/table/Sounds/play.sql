CREATE TABLE
  `play` (
    `user_id` varchar(19) NOT NULL,
    `id_sound` smallint(6) NOT NULL,
    `times` smallint(6) NOT NULL,
    PRIMARY KEY (`user_id`, `id_sound`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1