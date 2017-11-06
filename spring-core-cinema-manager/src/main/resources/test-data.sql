INSERT INTO auditoriums (id, name) VALUES (1, 'Blue Room');
INSERT INTO auditoriums (id, name) VALUES (2, 'Green Room');
INSERT INTO auditoriums (id, name) VALUES (3, 'Red Room');
INSERT INTO seats (auditoriumId, number, vip) VALUES (1, 1, TRUE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (1, 2, TRUE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (1, 3, TRUE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (1, 4, TRUE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (1, 5, FALSE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (1, 6, FALSE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (1, 7, FALSE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (1, 8, FALSE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (1, 9, FALSE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (1, 10, FALSE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (2, 1, FALSE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (2, 2, FALSE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (2, 3, FALSE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (2, 4, FALSE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (3, 1, TRUE);
INSERT INTO seats (auditoriumId, number, vip) VALUES (3, 2, TRUE);
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
INSERT INTO tickets (userId, assignmentId, seatNum) VALUES (1, 2, 3);
INSERT INTO tickets (userId, assignmentId, seatNum) VALUES (1, 2, 4);
INSERT INTO tickets (userId, assignmentId, seatNum) VALUES (4, 2, 1);
INSERT INTO tickets (userId, assignmentId, seatNum) VALUES (4, 6, 2);
INSERT INTO eventStatistic (eventId, accessedByNameCount, priceWereQueriedCount, ticketsWereBookedCount) VALUES (1, 2, 1, 3);
INSERT INTO eventStatistic (eventId, accessedByNameCount, priceWereQueriedCount, ticketsWereBookedCount) VALUES (2, 1, 3, 1);
