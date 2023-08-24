CREATE TABLE
  `command_analytic` (
    `time` timestamp NOT NULL DEFAULT current_timestamp(),
    `name` varchar(255) NOT NULL,
    `user_id` varchar(19) NOT NULL,
    PRIMARY KEY (`time`, `name`)
  ) ENGINE = InnoDB DEFAULT CHARSET = latin1