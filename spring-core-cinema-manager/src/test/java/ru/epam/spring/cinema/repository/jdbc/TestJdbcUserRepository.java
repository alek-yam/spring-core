package ru.epam.spring.cinema.repository.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

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
    	Calendar birthday = new GregorianCalendar(1979, 9, 1);
    	assertEquals(birthday, user.getBirthday());
    	assertEquals("mary@gmail.com", user.getEmail());
    	assertEquals("mary123", user.getPassword());

//    	assertNotNull(user.getTickets());
//    	assertEquals(2, user.getTickets().size());
//    	Long[] ticketIds = user.getTickets().toArray(new Long[2]);
//    	assertEquals(1L, ticketIds[0].longValue());
//    	assertEquals(2L, ticketIds[1].longValue());

    	assertNotNull(user.getRoles());
    	assertEquals(2, user.getRoles().size());
    	assertTrue(user.getRoles().contains("BOOKING_MANAGER"));
    	assertTrue(user.getRoles().contains("REGISTERED_USER"));
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

    	List<User> usersList = new ArrayList<User>(users);
    	User user = usersList.get(1);
    	assertEquals(2, user.getId().longValue());
    	assertEquals("Ken", user.getFirstName());
    	assertEquals("Bolein", user.getLastName());
    	Calendar birthday = new GregorianCalendar(1987, 5, 21);
    	assertEquals(birthday, user.getBirthday());
    	assertEquals("ken@gmail.com", user.getEmail());
    	assertEquals("ken123", user.getPassword());
    	assertNotNull(user.getTickets());
    	assertTrue(user.getTickets().isEmpty());
    }

    @Test
    public void testAddUser() {
    	User user = new User();
    	user.setFirstName("Den");
    	user.setLastName("Braun");
    	Calendar birthday = new GregorianCalendar(1977, 3, 10);
    	user.setBirthday(birthday);
    	user.setEmail("den@mail.com");
    	user.setPassword("den123");

    	int oldSize = userRepository.getAll().size();
    	User addedUser = userRepository.save(user);
    	int newSize = userRepository.getAll().size();

    	assertNotNull(addedUser);
    	assertNotNull(addedUser.getId());
    	assertEquals(user.getFirstName(), addedUser.getFirstName());
    	assertEquals(user.getLastName(), addedUser.getLastName());
    	assertEquals(user.getBirthday(), addedUser.getBirthday());
    	assertEquals(user.getEmail(), addedUser.getEmail());
    	assertEquals(user.getPassword(), addedUser.getPassword());
    	assertNotNull(userRepository.getById(addedUser.getId()));
    	assertEquals(oldSize + 1, newSize);
    }

    @Test
    public void testUpdateUser() {
    	User user = new User();
    	user.setId(2L);
    	user.setFirstName("Ken");
    	user.setLastName("Bolein");
    	Calendar newBirthday = new GregorianCalendar(1977, 3, 10);
    	user.setBirthday(newBirthday);
    	user.setEmail("new_den@ebox.com");
    	user.setPassword("qwe123");

    	int oldSize = userRepository.getAll().size();
    	User updatedUser = userRepository.save(user);
    	int newSize = userRepository.getAll().size();

    	assertNotNull(updatedUser);
    	assertEquals(user.getId(), updatedUser.getId());
    	assertEquals(user.getFirstName(), updatedUser.getFirstName());
    	assertEquals(user.getLastName(), updatedUser.getLastName());
    	assertEquals(user.getBirthday(), updatedUser.getBirthday());
    	assertEquals(user.getEmail(), updatedUser.getEmail());
    	assertEquals(user.getPassword(), updatedUser.getPassword());
    	assertEquals(oldSize, newSize);
    }

    @Test(expected=Exception.class)
    public void testUpdateUserThrowsExeptionIfUserNotFound() {
    	User user = new User();
    	user.setId(999999L);
    	user.setFirstName("Ken");
    	user.setLastName("Bolein");
    	Calendar birthday = new GregorianCalendar(1977, 3, 10);
    	user.setBirthday(birthday);
    	user.setEmail("new_den@ebox.com");
    	user.setPassword("qwe123");

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
    	Long id = 999999L;

    	assertNull(userRepository.getById(id));
    	userRepository.removeById(id);
    }

    @Test
    public void testGetByFilter() {
    	UserFilter filter = new UserFilter();
    	filter.setEmail("ken@gmail.com");

    	Collection<User> users = userRepository.getByFilter(filter);

    	assertNotNull(users);
    	assertEquals(1, users.size());

    	List<User> usersList = new ArrayList<User>(users);
    	User user = usersList.get(0);
    	assertEquals(2, user.getId().longValue());
    }

}
