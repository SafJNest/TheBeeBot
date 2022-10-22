CREATE TABLE IF NOT EXISTS play (
   user_id char(18),
   id_sound int not null,
   times int not null,
   PRIMARY KEY(user_id, id_sound),
   FOREIGN KEY (id_sound) REFERENCES sound_id(id_sound)
);