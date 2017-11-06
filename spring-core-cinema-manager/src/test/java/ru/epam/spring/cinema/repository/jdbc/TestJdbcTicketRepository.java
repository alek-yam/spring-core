package ru.epam.spring.cinema.repository.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import ru.epam.spring.cinema.domain.Ticket;
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
    	assertEquals(2, ticket.getEventAssignmentId().longValue());
    	assertEquals(3, ticket.getSeat().longValue());
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
    	assertEquals(4, tickets.size());

    	Ticket ticket = tickets.iterator().next();
    	assertEquals(1, ticket.getId().longValue());
    	assertEquals(1, ticket.getUserId().longValue());
    	assertEquals(2, ticket.getEventAssignmentId().longValue());
    	assertEquals(3, ticket.getSeat().longValue());
    }

    @Test
    public void testAddTicket() {
    	Ticket ticket = new Ticket(2L, 3L, 4L);

    	int oldSize = ticketRepository.getAll().size();
    	Ticket addedTicket = ticketRepository.save(ticket);
    	int newSize = ticketRepository.getAll().size();

    	// size
    	assertEquals(oldSize + 1, newSize);

    	// ticket
    	assertNotNull(addedTicket);
    	assertNotNull(addedTicket.getId());
    	assertEquals(ticket.getUserId(), addedTicket.getUserId());
    	assertEquals(ticket.getEventAssignmentId(), addedTicket.getEventAssignmentId());
    	assertEquals(ticket.getSeat(), addedTicket.getSeat());

    	// DB record
    	assertNotNull(ticketRepository.getById(addedTicket.getId()));
    }

    @Test
    public void testUpdateTicket() {
    	Ticket ticket = ticketRepository.getById(1L);
    	ticket.setUserId(3L);
    	ticket.setEventAssignmentId(4L);
    	ticket.setSeat(1L);

    	int oldSize = ticketRepository.getAll().size();
    	Ticket updatedTicket = ticketRepository.save(ticket);
    	int newSize = ticketRepository.getAll().size();

    	// size
    	assertEquals(oldSize, newSize);

    	// ticket
    	assertNotNull(updatedTicket);
    	assertEquals(ticket.getId(), updatedTicket.getId());
    	assertEquals(ticket.getUserId(), updatedTicket.getUserId());
    	assertEquals(ticket.getEventAssignmentId(), updatedTicket.getEventAssignmentId());
    	assertEquals(ticket.getSeat(), updatedTicket.getSeat());
    }

    @Test(expected=Exception.class)
    public void testUpdateTicketThrowsExeptionIfTicketNotFound() {
    	Ticket ticket = new Ticket();
    	ticket.setId(999999L);	// nonexistent ticket
    	ticket.setUserId(3L);
    	ticket.setEventAssignmentId(4L);
    	ticket.setSeat(1L);

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
    	Long ticketId = 999999L;	// nonexistent ticket

    	assertNull(ticketRepository.getById(ticketId));
    	ticketRepository.removeById(ticketId);
    }

    @Test
    public void testGetByFilter() {
    	TicketFilter filter = new TicketFilter();
    	filter.setUserId(4L);
    	filter.setAssignmentId(2L);

    	Collection<Ticket> tickets = ticketRepository.getByFilter(filter);

    	// uniqueness
    	assertNotNull(tickets);
    	assertEquals(1, tickets.size());

    	// ticket id
    	Ticket ticket = tickets.iterator().next();
    	assertEquals(3, ticket.getId().longValue());
    }
}
