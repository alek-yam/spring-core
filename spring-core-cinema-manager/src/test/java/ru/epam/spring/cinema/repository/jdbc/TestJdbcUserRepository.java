package ru.epam.spring.cinema.repository.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.filter.UserFilter;

public class TestJdbcUserRepository {

    private EmbeddedDatabase db;
    private JdbcUserRepository userRepository;

    @Before
    public void setUp() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder
				.setName("cinemaDb")
				.setType(EmbeddedDatabaseType.H2)
				.addScript("schema.sql")
				.addScript("test-data.sql")
				.build();

        userRepository = new JdbcUserRepository();
        userRepository.setDataSource(db);
    }

    @After
    public void tearDown() {
    	db.shutdown();
    }

    @Test
    public void testGetById() {
    	User user = userRepository.getById(4L);

    	assertNotNull(user);
    	assertEquals(4, user.getId().longValue());
    	assertEquals("Mary", user.getFirstName());
    	assertEquals("Wilams", user.getLastName());
    	assertEquals(LocalDate.of(1979, 10, 1), user.getBirthday());
    	assertEquals("mary@gmail.com", user.getEmail());
    	assertEquals("$2a$10$/lTwOdR6QJAwbYjNEaDBz.KvnNbSYQajAiW2KF3NYXntizJBSq7/a", user.getPassword());

    	// roles
    	assertNotNull(user.getRoles());
    	assertEquals(2, user.getRoles().size());
    	assertTrue(user.getRoles().contains("BOOKING_MANAGER"));
    	assertTrue(user.getRoles().contains("REGISTERED_USER"));

    	// tickets
    	assertNotNull(user.getTickets());
    	assertEquals(2, user.getTickets().size());
    	assertTrue(user.getTickets().contains(3L));
    	assertTrue(user.getTickets().contains(4L));
    }

    @Test
    public void testGetByIdReturnsNullIfNotFound() {
    	User user = userRepository.getById(999999L);
    	assertNull(user);
    }

    @Test
    public void testGetAll() {
    	Collection<User> users = userRepository.getAll();

    	assertNotNull(users);
    	assertEquals(4, users.size());

    	Optional<User> john = users.stream().filter(u -> "ken@gmail.com".equals(u.getEmail())).findFirst();
    	assertTrue(john.isPresent());
    	assertNotNull(john.get().getId());
    	assertEquals("Ken", john.get().getFirstName());
    	assertEquals("Bolein", john.get().getLastName());
    	assertEquals(LocalDate.of(1987, 6, 21), john.get().getBirthday());
    	assertEquals("ken@gmail.com", john.get().getEmail());
    	assertEquals("$2a$10$5W.YP1pCyd.f2B2vnZA.f.jiIr7zx/sG2hnG5kBcRuKD5q2OSIZoy", john.get().getPassword());

    	// roles
    	assertNotNull(john.get().getRoles());
    	assertEquals(1, john.get().getRoles().size());
    	assertTrue(john.get().getRoles().contains("REGISTERED_USER"));

    	// tickets
    	assertNotNull(john.get().getTickets());
    	assertTrue(john.get().getTickets().isEmpty());
    }

    @Test
    public void testAddUser() {
    	User user = new User();
    	user.setFirstName("Den");
    	user.setLastName("Braun");
    	user.setBirthday(LocalDate.of(1977, 4, 10));
    	user.setEmail("den@mail.com");
    	user.setPassword("den123");
    	user.setRoles(Stream.of("BOOKING_MANAGER", "REGISTERED_USER").collect(Collectors.toSet()));

    	int oldSize = userRepository.getAll().size();
    	User addedUser = userRepository.save(user);
    	int newSize = userRepository.getAll().size();

    	// size
    	assertEquals(oldSize + 1, newSize);

    	// user
    	assertNotNull(addedUser);
    	assertNotNull(addedUser.getId());
    	assertEquals(user.getFirstName(), addedUser.getFirstName());
    	assertEquals(user.getLastName(), addedUser.getLastName());
    	assertEquals(user.getBirthday(), addedUser.getBirthday());
    	assertEquals(user.getEmail(), addedUser.getEmail());
    	assertEquals(user.getPassword(), addedUser.getPassword());

    	// roles
    	assertNotNull(addedUser.getRoles());
    	assertEquals(2, addedUser.getRoles().size());
    	assertTrue(addedUser.getRoles().contains("BOOKING_MANAGER"));
    	assertTrue(addedUser.getRoles().contains("REGISTERED_USER"));

    	// DB record
    	assertNotNull(userRepository.getById(addedUser.getId()));
    }

    @Test
    public void testUpdateUser() {
    	User user = userRepository.getById(2L);
    	user.setFirstName("Lakshmi");
    	user.setLastName("Kant");
    	user.setBirthday(LocalDate.of(2000, 1, 1));
    	user.setEmail("kant@info.com");
    	user.setPassword("kant123");
    	user.setRoles(Stream.of("BOOKING_MANAGER", "REGISTERED_USER").collect(Collectors.toSet()));

    	int oldSize = userRepository.getAll().size();
    	User updatedUser = userRepository.save(user);
    	int newSize = userRepository.getAll().size();

    	// size
    	assertEquals(oldSize, newSize);

    	// user
    	assertNotNull(updatedUser);
    	assertEquals(user.getId(), updatedUser.getId());
    	assertEquals(user.getFirstName(), updatedUser.getFirstName());
    	assertEquals(user.getLastName(), updatedUser.getLastName());
    	assertEquals(user.getBirthday(), updatedUser.getBirthday());
    	assertEquals(user.getEmail(), updatedUser.getEmail());
    	assertEquals(user.getPassword(), updatedUser.getPassword());

    	// roles
    	assertNotNull(user.getRoles());
    	assertEquals(2, user.getRoles().size());
    	assertTrue(user.getRoles().contains("BOOKING_MANAGER"));
    	assertTrue(user.getRoles().contains("REGISTERED_USER"));
    }

    @Test(expected=Exception.class)
    public void testUpdateUserThrowsExeptionIfUserNotFound() {
    	User user = new User();
    	user.setId(999999L);	// nonexistent user
    	user.setFirstName("Lakshmi");
    	user.setLastName("Kant");
    	user.setBirthday(LocalDate.of(2000, 1, 1));
    	user.setEmail("kant@info.com");
    	user.setPassword("kant123");
    	user.setRoles(Stream.of("BOOKING_MANAGER", "REGISTERED_USER").collect(Collectors.toSet()));

    	userRepository.save(user);
    }

    @Test
    public void testRemoveById() {
    	Long id = 1L;

    	assertNotNull(userRepository.getById(id));
    	userRepository.removeById(id);
    	assertNull(userRepository.getById(id));
    }

    @Test(expected=Exception.class)
    public void testRemoveByIdThrowsExeptionIfNotFound() {
    	Long id = 999999L;	// nonexistent user

    	assertNull(userRepository.getById(id));
    	userRepository.removeById(id);
    }

    @Test
    public void testGetByFilter() {
    	UserFilter filter = new UserFilter();
    	filter.setEmail("ken@gmail.com");

    	Collection<User> users = userRepository.getByFilter(filter);

    	// uniqueness
    	assertNotNull(users);
    	assertEquals(1, users.size());

    	// user id
    	User user = users.iterator().next();
    	assertEquals(2, user.getId().longValue());
    }

}
