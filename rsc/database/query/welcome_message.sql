CREATE TABLE IF NOT EXISTS welcome_roles (
   role_id char(18) unique PRIMARY KEY,
   discord_id char(18), 
   FOREIGN KEY(discord_id) REFERENCES welcome_message(discord_id)
);









