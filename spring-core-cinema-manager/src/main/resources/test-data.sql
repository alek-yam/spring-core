INSERT INTO events (name, rate, price) VALUES ('Avatar-2', 'HIGH', 500);
INSERT INTO events (name, rate, price) VALUES ('Elki-4', 'LOW', 250);
INSERT INTO events (name, rate, price) VALUES ('Test', 'MID', 300);
INSERT INTO eventAssignments (eventId, airDate, auditoriumId) VALUES (1, {ts '2017-09-01 18:00:00.00'}, 1);
INSERT INTO eventAssignments (eventId, airDate, auditoriumId) VALUES (1, {ts '2017-09-01 22:00:00.00'}, 1);
INSERT INTO eventAssignments (eventId, airDate, auditoriumId) VALUES (1, {ts '2017-09-02 18:00:00.00'}, 1);
INSERT INTO eventAssignments (eventId, airDate, auditoriumId) VALUES (1, {ts '2017-09-02 19:30:00.00'}, 2);
INSERT INTO eventAssignments (eventId, airDate, auditoriumId) VALUES (2, {ts '2017-12-30 14:00:00.00'}, 1);
INSERT INTO eventAssignments (eventId, airDate, auditoriumId) VALUES (2, {ts '2017-12-31 14:00:00.00'}, 2);
INSERT INTO eventAssignments (eventId, airDate, auditoriumId) VALUES (2, {ts '2018-01-03 20:00:00.00'}, 3);
INSERT INTO eventAssignments (eventId, airDate, auditoriumId) VALUES (3, {ts '2017-09-01 12:00:00.00'}, 1);
INSERT INTO eventAssignments (eventId, airDate, auditoriumId) VALUES (3, {ts '2017-09-02 18:00:00.00'}, 2);
INSERT INTO users (firstName, lastName, birthday, email, password, roles) VALUES ('John', 'Richards', '2000-02-03', 'john@gmail.com', '$2a$10$JV7imjHMfnrDKS0H6mG1fub/WoKL48WTUuk1BB2idGgySC.vfaxE6', 'REGISTERED_USER');
INSERT INTO users (firstName, lastName, birthday, email, password, roles) VALUES ('Ken', 'Bolein', '1987-06-21', 'ken@gmail.com', '$2a$10$5W.YP1pCyd.f2B2vnZA.f.jiIr7zx/sG2hnG5kBcRuKD5q2OSIZoy', 'REGISTERED_USER');
INSERT INTO users (firstName, lastName, birthday, email, password, roles) VALUES ('Tom', 'Suu', '1994-07-24', 'tom@gmail.com', '$2a$10$pdYc0vWfMboSIBHI8rIhu.7/5uUxdEEz0vJK1QbKA84xUxJ.e8era', 'BOOKING_MANAGER');
INSERT INTO users (firstName, lastName, birthday, email, password, roles) VALUES ('Mary', 'Wilams', '1979-10-01', 'mary@gmail.com', '$2a$10$/lTwOdR6QJAwbYjNEaDBz.KvnNbSYQajAiW2KF3NYXntizJBSq7/a', 'BOOKING_MANAGER,REGISTERED_USER');
