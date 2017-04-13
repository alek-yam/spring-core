CREATE TABLE auditoriums (
  name VARCHAR(60) NOT NULL,
  capacity INT NOT NULL,
  PRIMARY KEY (name)
);

CREATE TABLE seats (
  num INT NOT NULL,
  vip BOOLEAN NOT NULL,
  auditoriumName VARCHAR(60) NOT NULL,
  UNIQUE (num, auditoriumName)
);

CREATE TABLE events (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(60) NOT NULL,
  rate VARCHAR(60) NOT NULL,
  price DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE eventAssignments (
  eventId INT NOT NULL,
  airDate TIMESTAMP NOT NULL,
  auditoriumName VARCHAR(60) NOT NULL,
  UNIQUE (eventId, airDate, auditoriumName)
);

CREATE TABLE tickets (
  id INT NOT NULL AUTO_INCREMENT,
  userId INT,
  eventId INT NOT NULL,
  airDate TIMESTAMP NOT NULL,
  seatNum INT NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT,
  firstName VARCHAR(60) NOT NULL,
  lastName VARCHAR(60) NOT NULL,
  birthday DATE,
  email VARCHAR(60),
  password VARCHAR(60),
  roles VARCHAR(512),
  PRIMARY KEY (id)
);

CREATE TABLE accounts (
  userId INT NOT NULL,
  balance DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (userId)
);

CREATE TABLE eventStatistic (
  eventName VARCHAR(60) NOT NULL,
  accessedByNameCount INT DEFAULT 0,
  priceWereQueriedCount INT DEFAULT 0,
  ticketsWereBookedCount INT DEFAULT 0,
  PRIMARY KEY (eventName)
);

CREATE TABLE discountStatistic (
  strategyId VARCHAR(60) NOT NULL,
  userId INT,
  wasGivenCount INT DEFAULT 0,
  UNIQUE (strategyId, userId)
);

create table persistent_logins (
    username varchar(64) not null,
    series varchar(64) primary key,
    token varchar(64) not null,
    last_used timestamp not null
);
