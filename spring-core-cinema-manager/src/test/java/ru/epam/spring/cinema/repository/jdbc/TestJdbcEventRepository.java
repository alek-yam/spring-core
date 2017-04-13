package ru.epam.spring.cinema.repository.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import ru.epam.spring.cinema.domain.Event;
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
    	assertEquals(900, event.getBasePrice(), 0.000001);

    	NavigableSet<Calendar> airDates = event.getAirDates();
    	assertNotNull(airDates);
    	assertEquals(4, airDates.size());
    	Calendar firstDate = new GregorianCalendar(2017, 0, 1, 18, 0);
    	assertEquals(firstDate, airDates.first());
    	Calendar lastDate = new GregorianCalendar(2017, 0, 2, 19, 30);
    	assertEquals(lastDate, airDates.last());

    	NavigableMap<Calendar, String> auditoriums = event.getAuditoriums();
    	assertNotNull(auditoriums);
    	assertEquals(4, auditoriums.size());
    	assertEquals(firstDate, auditoriums.firstEntry().getKey());
    	assertEquals("Blue Room", auditoriums.firstEntry().getValue());
    	assertEquals(lastDate, auditoriums.lastEntry().getKey());
    	assertEquals("Green Room", auditoriums.lastEntry().getValue());
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
    	assertEquals(2, events.size());

    	List<Event> evensList = new ArrayList<Event>(events);
    	Event event = evensList.get(1);
    	assertEquals(2, event.getId().longValue());
    	assertEquals("Elki-4", event.getName());
    	assertEquals(EventRating.LOW, event.getRating());
    	assertEquals(250, event.getBasePrice(), 0.000001);

    	NavigableSet<Calendar> airDates = event.getAirDates();
    	assertNotNull(airDates);
    	assertEquals(3, airDates.size());
    	Calendar firstDate = new GregorianCalendar(2016, 11, 30, 14, 0);
    	assertEquals(firstDate, airDates.first());
    	Calendar lastDate = new GregorianCalendar(2017, 0, 3, 20, 0);
    	assertEquals(lastDate, airDates.last());

    	NavigableMap<Calendar, String> auditoriums = event.getAuditoriums();
    	assertNotNull(auditoriums);
    	assertEquals(3, auditoriums.size());
    	assertEquals(firstDate, auditoriums.firstEntry().getKey());
    	assertEquals("Blue Room", auditoriums.firstEntry().getValue());
    	assertEquals(lastDate, auditoriums.lastEntry().getKey());
    	assertEquals("Red Room", auditoriums.lastEntry().getValue());
    }

    @Test
    public void testAddEvent() {
    	Event event = new Event();
    	event.setName("Rock concert");
    	event.setRating(EventRating.MID);
    	event.setBasePrice(600);

    	int oldSize = eventRepository.getAll().size();
    	Event addedEvent = eventRepository.save(event);
    	int newSize = eventRepository.getAll().size();

    	assertNotNull(addedEvent);
    	assertNotNull(addedEvent.getId());
    	assertEquals(oldSize + 1, newSize);

    	Event actualEvent = eventRepository.getById(addedEvent.getId());
    	assertEquals(event.getId(), actualEvent.getId());
    	assertEquals(event.getName(), actualEvent.getName());
    	assertEquals(event.getRating(), actualEvent.getRating());
    	assertEquals(event.getBasePrice(), actualEvent.getBasePrice(), 0.000001);
    	assertNotNull(actualEvent.getAirDates());
    	assertNotNull(actualEvent.getAuditoriums());
    	assertEquals(0, actualEvent.getAirDates().size());
    	assertEquals(0, actualEvent.getAuditoriums().size());
    }

    @Test
    public void testAddEventWithAssigments() {
    	Event event = new Event();
    	event.setName("Rock concert");
    	event.setRating(EventRating.MID);
    	event.setBasePrice(600);

        Calendar may10at0900 = new GregorianCalendar(2016, 04, 10, 9, 0);
        Calendar may10at1200 = new GregorianCalendar(2016, 04, 10, 12, 0);
        Calendar may20at1330 = new GregorianCalendar(2016, 04, 20, 13, 30);

        NavigableSet<Calendar> airDates = new TreeSet<>();
        airDates.add(may10at0900);
        airDates.add(may10at1200);
        airDates.add(may20at1330);
        event.setAirDates(airDates);

        NavigableMap<Calendar, String> auditoriums = new TreeMap<>();
        auditoriums.put(may10at0900, "Blue Room");
        auditoriums.put(may10at1200, "Green Room");
        auditoriums.put(may20at1330, "Blue Room");
        event.setAuditoriums(auditoriums);

    	int oldSize = eventRepository.getAll().size();
    	Event addedEvent = eventRepository.save(event);
    	int newSize = eventRepository.getAll().size();

    	assertNotNull(addedEvent);
    	assertNotNull(addedEvent.getId());
    	assertEquals(oldSize + 1, newSize);

    	Event actualEvent = eventRepository.getById(addedEvent.getId());
    	assertEquals(event.getId(), actualEvent.getId());
    	assertEquals(event.getName(), actualEvent.getName());
    	assertEquals(event.getRating(), actualEvent.getRating());
    	assertEquals(event.getBasePrice(), actualEvent.getBasePrice(), 0.000001);
    	assertNotNull(actualEvent.getAirDates());
    	assertNotNull(actualEvent.getAuditoriums());

    	List<Calendar> actualAirDates = new ArrayList<>(actualEvent.getAirDates());
    	assertEquals(3, actualAirDates.size());
    	assertEquals(may10at0900, actualAirDates.get(0));
    	assertEquals(may10at1200, actualAirDates.get(1));
    	assertEquals(may20at1330, actualAirDates.get(2));

    	Map<Calendar, String> actualAuditoriums = actualEvent.getAuditoriums();
    	assertEquals(3, actualAuditoriums.size());
    	assertEquals("Blue Room", actualAuditoriums.get(may10at0900));
    	assertEquals("Green Room", actualAuditoriums.get(may10at1200));
    	assertEquals("Blue Room", actualAuditoriums.get(may20at1330));
    }

    @Test
    public void testUpdateEvent() {
    	Event event = new Event();
    	event.setId(1L);
    	event.setName("Avatar - Second Part !!!");
    	event.setRating(EventRating.HIGH);
    	event.setBasePrice(1200);

    	Calendar jan2at1800 = new GregorianCalendar(2017, 0, 1, 22, 0);
    	Calendar feb17at1745 = new GregorianCalendar(2017, 1, 17, 17, 45);

        NavigableSet<Calendar> airDates = new TreeSet<>();
        airDates.add(jan2at1800);
        airDates.add(feb17at1745);
        event.setAirDates(airDates);

        NavigableMap<Calendar, String> auditoriums = new TreeMap<>();
        auditoriums.put(jan2at1800, "Blue Room");	// keep existing assignment
        auditoriums.put(feb17at1745, "Red Room");	// add new assignment
        event.setAuditoriums(auditoriums);			// remove all other assignments

    	int oldSize = eventRepository.getAll().size();
    	Event updatedEvent = eventRepository.save(event);
    	int newSize = eventRepository.getAll().size();

    	assertNotNull(updatedEvent);
    	assertEquals(event.getId(), updatedEvent.getId());
    	assertEquals(oldSize, newSize);

    	Event actualEvent = eventRepository.getById(updatedEvent.getId());
    	assertEquals(event.getId(), actualEvent.getId());
    	assertEquals(event.getName(), actualEvent.getName());
    	assertEquals(event.getRating(), actualEvent.getRating());
    	assertEquals(event.getBasePrice(), actualEvent.getBasePrice(), 0.000001);
    	assertNotNull(actualEvent.getAirDates());
    	assertNotNull(actualEvent.getAuditoriums());

    	List<Calendar> actualAirDates = new ArrayList<>(actualEvent.getAirDates());
    	assertEquals(2, actualAirDates.size());
    	assertEquals(jan2at1800, actualAirDates.get(0));
    	assertEquals(feb17at1745, actualAirDates.get(1));

    	Map<Calendar, String> actualAuditoriums = actualEvent.getAuditoriums();
    	assertEquals(2, actualAuditoriums.size());
    	assertEquals("Blue Room", actualAuditoriums.get(jan2at1800));
    	assertEquals("Red Room", actualAuditoriums.get(feb17at1745));
    }

    @Test(expected=Exception.class)
    public void testUpdateEventThrowsExeptionIfEventNotFound() {
    	Event event = new Event();
    	event.setId(999999L);
    	event.setName("Rock concert");
    	event.setRating(EventRating.MID);
    	event.setBasePrice(600);

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
    	Long id = 999999L;

    	assertNull(eventRepository.getById(id));
    	eventRepository.removeById(id);
    }

    @Test
    public void testGetByFilter() {
    	EventFilter filter = new EventFilter();
    	filter.setName("Elki-4");

    	Collection<Event> events = eventRepository.getByFilter(filter);

    	assertNotNull(events);
    	assertEquals(1, events.size());

    	List<Event> eventsList = new ArrayList<>(events);
    	Event event = eventsList.get(0);
    	assertEquals(2, event.getId().longValue());
    }
}
