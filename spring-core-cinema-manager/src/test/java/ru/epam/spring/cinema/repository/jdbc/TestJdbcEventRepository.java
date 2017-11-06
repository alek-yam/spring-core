package ru.epam.spring.cinema.repository.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.domain.EventRating;
import ru.epam.spring.cinema.repository.filter.EventFilter;

public class TestJdbcEventRepository {

    private EmbeddedDatabase db;
    private JdbcEventRepository eventRepository;

    @Before
    public void setUp() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder
				.setName("cinemaDb")
				.setType(EmbeddedDatabaseType.H2)
				.addScript("schema.sql")
				.addScript("test-data.sql")
				.build();

		eventRepository = new JdbcEventRepository();
		eventRepository.setDataSource(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void testGetById() {
    	Event event = eventRepository.getById(1L);

    	assertNotNull(event);
    	assertEquals(1, event.getId().longValue());
    	assertEquals("Avatar-2", event.getName());
    	assertEquals(EventRating.HIGH, event.getRating());
    	assertEquals(500, event.getBasePrice(), 0.000001);

    	// assignments
    	assertNotNull(event.getAssignments());
    	assertEquals(4, event.getAssignments().size());

    	// 2017-09-01 18-00
    	EventAssignment assignment = event.getAssignments().iterator().next();
    	assertEquals(1, assignment.getEventId().longValue());
    	assertEquals(1, assignment.getAuditoriumId().longValue());
    	assertEquals(LocalDateTime.of(2017, 9, 1, 18, 00), assignment.getAirDate());
    }

    @Test
    public void testGetByIdReturnsNullIfNotFound() {
    	Event event = eventRepository.getById(999999L);

    	assertNull(event);
    }

    @Test
    public void testGetAll() {
    	Collection<Event> events = eventRepository.getAll();

    	assertNotNull(events);
    	assertEquals(3, events.size());

    	Optional<Event> elki4 = events.stream().filter(e -> "Elki-4".equals(e.getName())).findFirst();
    	assertNotNull(elki4.get().getId());
    	assertEquals("Elki-4", elki4.get().getName());
    	assertEquals(EventRating.LOW, elki4.get().getRating());
    	assertEquals(250, elki4.get().getBasePrice(), 0.000001);

    	// assignments
    	assertNotNull(elki4.get().getAssignments());
    	assertEquals(3, elki4.get().getAssignments().size());

    	// 2017-12-30 14:00
    	EventAssignment assignment = elki4.get().getAssignments().iterator().next();
    	assertEquals(elki4.get().getId(), assignment.getEventId());
    	assertEquals(1, assignment.getAuditoriumId().longValue());
    	assertEquals(LocalDateTime.of(2017, 12, 30, 14, 00), assignment.getAirDate());
    }

    @Test
    public void testAddEvent() {
    	Event event = new Event();
    	event.setName("Rock concert");
    	event.setRating(EventRating.MID);
    	event.setBasePrice(300);

    	Set<EventAssignment> assignments = new TreeSet<EventAssignment>();
    	assignments.add(new EventAssignment(null, 1L, LocalDateTime.of(2017, 10, 12, 12, 00)));
    	assignments.add(new EventAssignment(null, 3L, LocalDateTime.of(2017, 10, 14, 21, 30)));
    	event.setAssignments(assignments);

    	int oldSize = eventRepository.getAll().size();
    	Event addedEvent = eventRepository.save(event);
    	int newSize = eventRepository.getAll().size();

    	// size
    	assertEquals(oldSize + 1, newSize);

    	// event
    	assertNotNull(addedEvent);
    	assertNotNull(addedEvent.getId());
    	assertEquals(event.getName(), addedEvent.getName());
    	assertEquals(event.getRating(), addedEvent.getRating());
    	assertEquals(event.getBasePrice(), addedEvent.getBasePrice(), 0.000001);

    	// assignments
    	assertNotNull(addedEvent.getAssignments());
    	assertEquals(2, addedEvent.getAssignments().size());

    	// 2017-10-12 12:00
    	EventAssignment addedAssignment = addedEvent.getAssignments().iterator().next();
    	assertNotNull(addedAssignment.getId());
    	assertEquals(addedEvent.getId(), addedAssignment.getEventId());
    	assertEquals(1, addedAssignment.getAuditoriumId().longValue());
    	assertEquals(LocalDateTime.of(2017, 10, 12, 12, 00), addedAssignment.getAirDate());

    	// DB record
    	assertNotNull(eventRepository.getById(addedEvent.getId()));
    }

    @Test
    public void testUpdateEvent() {
    	Event event = eventRepository.getById(3L);
    	event.setName("Test-NEW");
    	event.setRating(EventRating.LOW);
    	event.setBasePrice(100);

    	// adding assignment
    	EventAssignment newAssignment = new EventAssignment(3L, 3L, LocalDateTime.of(2017, 8, 30, 14, 00));
    	event.getAssignments().add(newAssignment);

    	// existing assignment
    	EventAssignment existingAssignment = event.getAssignments().stream()
    			.filter(a -> a.getAirDate().equals(LocalDateTime.of(2017, 9, 1, 12, 00))).findFirst().get();

    	// removing assignment
    	EventAssignment deletedAssignment = event.getAssignments().stream()
    			.filter(a -> a.getAirDate().equals(LocalDateTime.of(2017, 9, 2, 18, 00))).findFirst().get();
    	event.getAssignments().remove(deletedAssignment);

    	int oldSize = eventRepository.getAll().size();
    	Event updatedEvent = eventRepository.save(event);
    	int newSize = eventRepository.getAll().size();

    	// size
    	assertEquals(oldSize, newSize);

    	// event
    	assertNotNull(updatedEvent);
    	assertEquals(event.getId(), updatedEvent.getId());
    	assertEquals(event.getName(), updatedEvent.getName());
    	assertEquals(event.getRating(), updatedEvent.getRating());
    	assertEquals(event.getBasePrice(), updatedEvent.getBasePrice(), 0.000001);

    	// assignments
    	assertNotNull(updatedEvent.getAssignments());
    	assertEquals(2, updatedEvent.getAssignments().size());
    	assertTrue(updatedEvent.getAssignments().contains(newAssignment));
    	assertTrue(updatedEvent.getAssignments().contains(existingAssignment));
    	assertFalse(updatedEvent.getAssignments().contains(deletedAssignment));
    }

    @Test(expected=Exception.class)
    public void testUpdateEventThrowsExeptionIfEventNotFound() {
    	Event event = new Event();
    	event.setId(999999L);		// nonexistent event
    	event.setName("Test-NEW");
    	event.setRating(EventRating.LOW);
    	event.setBasePrice(100);

    	eventRepository.save(event);
    }

    @Test
    public void testRemoveById() {
    	Long id = 1L;

    	assertNotNull(eventRepository.getById(id));
    	eventRepository.removeById(id);
    	assertNull(eventRepository.getById(id));
    }

    @Test(expected=Exception.class)
    public void testRemoveByIdThrowsExeptionIfNotFound() {
    	Long id = 999999L;	// nonexistent event

    	assertNull(eventRepository.getById(id));
    	eventRepository.removeById(id);
    }

    @Test
    public void testGetByFilter() {
    	EventFilter filter = new EventFilter();
    	filter.setName("Elki-4");

    	Collection<Event> events = eventRepository.getByFilter(filter);

    	// uniqueness
    	assertNotNull(events);
    	assertEquals(1, events.size());

    	// event id
    	Event event = events.iterator().next();
    	assertEquals(2, event.getId().longValue());
    }
}
