CREATE TABLE auditoriums (
  id       INT         NOT NULL,
  name     VARCHAR(60) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (name)
);

CREATE TABLE seats (
  auditoriumId INT     NOT NULL,
  number       INT     NOT NULL,
  vip          BOOLEAN NOT NULL,
  PRIMARY KEY (auditoriumId, number),
  FOREIGN KEY(auditoriumId) REFERENCES auditoriums(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE events (
  id    INT              NOT NULL AUTO_INCREMENT,
  name  VARCHAR(60)      NOT NULL,
  rate  VARCHAR(60)      NOT NULL,
  price DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE eventAssignments (
  id           INT       NOT NULL AUTO_INCREMENT,
  eventId      INT       NOT NULL,
  airDate      TIMESTAMP NOT NULL,
  auditoriumId INT       NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY(eventId) REFERENCES events(id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(auditoriumId) REFERENCES auditoriums(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE users (
  id        INT          NOT NULL AUTO_INCREMENT,
  firstName VARCHAR(60)  NOT NULL,
  lastName  VARCHAR(60)  NOT NULL,
  birthday  DATE,
  email     VARCHAR(60)  NOT NULL,
  password  VARCHAR(60)  NOT NULL,
  roles     VARCHAR(512) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE accounts (
  id      INT              NOT NULL AUTO_INCREMENT,
  userId  INT              NOT NULL,
  balance DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE tickets (
  id           INT NOT NULL AUTO_INCREMENT,
  userId       INT NOT NULL,
  assignmentId INT NOT NULL,
  seatNum      INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY(assignmentId) REFERENCES eventAssignments(id) ON DELETE CASCADE ON UPDATE CASCADE
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
