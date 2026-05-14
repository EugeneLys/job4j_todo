CREATE TABLE tasks (
   id SERIAL PRIMARY KEY,
   name VARCHAR,
   description TEXT,
   created TIMESTAMP,
   done BOOLEAN
);