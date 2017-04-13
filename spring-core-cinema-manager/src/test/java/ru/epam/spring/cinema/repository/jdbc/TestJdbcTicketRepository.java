package ru.epam.spring.cinema.repository.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.Ticket;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.filter.TicketFilter;

public class TestJdbcTicketRepository {

	private EmbeddedDatabase db;
    private JdbcTicketRepository ticketRepository;

    @Before
    public void setUp() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder
				.setName("cinemaDb")
				.setType(EmbeddedDatabaseType.H2)
				.addScript("schema.sql")
				.addScript("test-data.sql")
				.build();

		ticketRepository = new JdbcTicketRepository();
		ticketRepository.setDataSource(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void testGetById() {
    	Ticket ticket = ticketRepository.getById(1L);

    	assertNotNull(ticket);
    	assertEquals(1, ticket.getId().longValue());
    	assertEquals(1, ticket.getUserId().longValue());
    	assertEquals(1, ticket.getEventId().longValue());
    	Calendar airDate = new GregorianCalendar(2017, 0, 1, 22, 0);
    	assertEquals(airDate, ticket.getDate());
    	assertEquals(2, ticket.getSeat());
    }

    @Test
    public void testGetByIdReturnsNullIfNotFound() {
    	Ticket ticket = ticketRepository.getById(999999L);

    	assertNull(ticket);
    }

    @Test
    public void testGetAll() {
    	Collection<Ticket> tickets = ticketRepository.getAll();

    	assertNotNull(tickets);
    	assertEquals(2, tickets.size());

    	List<Ticket> ticketsList = new ArrayList<Ticket>(tickets);
    	Ticket ticket = ticketsList.get(0);
    	assertEquals(1, ticket.getId().longValue());
    	assertEquals(1, ticket.getUserId().longValue());
    	assertEquals(1, ticket.getEventId().longValue());
    	Calendar airDate = new GregorianCalendar(2017, 0, 1, 22, 0);
    	assertEquals(airDate, ticket.getDate());
    	assertEquals(2, ticket.getSeat());
    }

    @Test
    public void testAddTicket() {
    	Calendar airDate = new GregorianCalendar(2016, 5, 16);
    	Ticket ticket = new Ticket(45L, 264L, airDate, 98L);

    	int oldSize = ticketRepository.getAll().size();
    	Ticket addedTicket = ticketRepository.save(ticket);
    	int newSize = ticketRepository.getAll().size();

    	assertNotNull(addedTicket);
    	assertNotNull(addedTicket.getId());
    	assertEquals(ticket.getUserId(), addedTicket.getUserId());
    	assertEquals(ticket.getEventId(), addedTicket.getEventId());
    	assertEquals(ticket.getDate(), addedTicket.getDate());
    	assertEquals(ticket.getSeat(), addedTicket.getSeat());
    	assertEquals(oldSize + 1, newSize);
    }

    @Test
    public void testUpdateTicket() {
    	Calendar newAirDate = new GregorianCalendar(2016, 11, 28);
    	Long newSeatNum = 10L;
    	Ticket ticket = new Ticket(2L, 1L, 32L, newAirDate, newSeatNum);

    	int oldSize = ticketRepository.getAll().size();
    	Ticket updatedTicket = ticketRepository.save(ticket);
    	int newSize = ticketRepository.getAll().size();

    	assertNotNull(updatedTicket);
    	assertEquals(ticket.getId(), updatedTicket.getId());
    	assertEquals(ticket.getUserId(), updatedTicket.getUserId());
    	assertEquals(ticket.getEventId(), updatedTicket.getEventId());
    	assertEquals(ticket.getDate(), updatedTicket.getDate());
    	assertEquals(ticket.getSeat(), updatedTicket.getSeat());
    	assertEquals(oldSize, newSize);
    }

    @Test(expected=Exception.class)
    public void testUpdateTicketThrowsExeptionIfTicketNotFound() {
    	Calendar newAirDate = new GregorianCalendar(2016, 11, 28);
    	Long newSeatNum = 10L;
    	Ticket ticket = new Ticket(999999L, 1L, 32L, newAirDate, newSeatNum);

    	ticketRepository.save(ticket);
    }

    @Test
    public void testRemoveTicket() {
    	Long ticketId = 1L;

    	assertNotNull(ticketRepository.getById(ticketId));
    	ticketRepository.removeById(ticketId);
    	assertNull(ticketRepository.getById(ticketId));
    }

    @Test(expected=Exception.class)
    public void testRemoveTicketThrowsExeptionIfTicketNotFound() {
    	Long ticketId = 3695732L;

    	assertNull(ticketRepository.getById(ticketId));
    	ticketRepository.removeById(ticketId);
    }

    @Test
    public void testGetByFilter() {
    	TicketFilter filter = new TicketFilter();

    	User user = new User();
    	user.setId(1L);
    	filter.setUser(user);

    	Event event = new Event();
    	event.setId(1L);
    	filter.setEvent(event);

    	Calendar airDate = new GregorianCalendar(2017, 0, 1, 22, 0);
    	filter.setDate(airDate);

    	Collection<Ticket> tickets = ticketRepository.getByFilter(filter);

    	assertNotNull(tickets);
    	assertEquals(1, tickets.size());

    	List<Ticket> ticketsList = new ArrayList<Ticket>(tickets);
    	Ticket ticket = ticketsList.get(0);
    	assertEquals(1, ticket.getId().longValue());
    }
}
