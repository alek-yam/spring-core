package ru.epam.spring.cinema.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventRating;
import ru.epam.spring.cinema.repository.EventRepository;
import ru.epam.spring.cinema.service.EventService;
import ru.epam.spring.cinema.service.EventServiceImpl;

public class TestEventService {

	private EventService eventService;
	private EventRepository eventRepositoryMock;

	private Auditorium blueRoom;
	private Auditorium greenRoom;
	private Auditorium redRoom;

	@Before
	public void init() {
		eventRepositoryMock = Mockito.mock(EventRepository.class);

		EventServiceImpl eventServiceImpl = new EventServiceImpl();
		eventServiceImpl.setAuditoriumRepository(eventRepositoryMock);
		eventService = eventServiceImpl;

		blueRoom = new Auditorium("Blue room", 20, getSet(1L, 2L, 3L));
		greenRoom = new Auditorium("Green room", 40, getSet(1L, 2L, 3L, 4L, 5L));
		redRoom = new Auditorium("Red room", 10, getSet(1L));
	}

	@Test
	public void eventCanBePresentedOnSeveralDatesAndSeveralTimesWithinEachDay() {
		Event event = new Event();
		event.setName("Matrix");
		event.setRating(EventRating.MID);
		event.setBasePrice(10);

        Calendar airDate1 = new GregorianCalendar(2016, 6, 10, 19, 30);
        event.addAirDateTime(airDate1, blueRoom);

        Calendar airDate21 = new GregorianCalendar(2016, 6, 11, 19, 30);
        event.addAirDateTime(airDate21, blueRoom);

        Calendar airDate22 = new GregorianCalendar(2016, 6, 11, 21, 30);
        event.addAirDateTime(airDate22, blueRoom);

        Calendar airDate23 = new GregorianCalendar(2016, 6, 11, 21, 45);
        event.addAirDateTime(airDate23, greenRoom);

        Event fakeEvent = new Event();
        fakeEvent.setId(123L);

        Mockito.when(eventRepositoryMock.save(Mockito.any(Event.class))).thenReturn(fakeEvent);

        Event resultEvent= eventService.save(event);
		assertNotNull(resultEvent);
		assertNotNull(resultEvent.getId());
		assertEquals(123, resultEvent.getId().longValue());

		ArgumentCaptor<Event> eventArgument = ArgumentCaptor.forClass(Event.class);
		Mockito.verify(eventRepositoryMock).save(eventArgument.capture());
		assertNotNull(eventArgument.getValue());
		assertEquals("Matrix", eventArgument.getValue().getName());
		assertEquals(EventRating.MID, eventArgument.getValue().getRating());
		assertTrue(eventArgument.getValue().getBasePrice() == 10);
		assertTrue(eventArgument.getValue().getAirDates().contains(airDate1));
		assertTrue(eventArgument.getValue().getAirDates().contains(airDate21));
		assertTrue(eventArgument.getValue().getAirDates().contains(airDate22));
		assertTrue(eventArgument.getValue().getAirDates().contains(airDate23));
		assertTrue(eventArgument.getValue().getAuditoriums().get(airDate1).equals(blueRoom.getName()));
		assertTrue(eventArgument.getValue().getAuditoriums().get(airDate21).equals(blueRoom.getName()));
		assertTrue(eventArgument.getValue().getAuditoriums().get(airDate22).equals(blueRoom.getName()));
		assertTrue(eventArgument.getValue().getAuditoriums().get(airDate23).equals(greenRoom.getName()));
	}

	@Test
	public void forEachDateTimeEventWillBePresentedOnlyInSingleAuditorium() {
		Event event = new Event();
		event.setName("Matrix");
		event.setRating(EventRating.MID);
		event.setBasePrice(10);

        Calendar airDate1 = new GregorianCalendar(2016, 6, 10, 19, 30);
        event.addAirDateTime(airDate1, blueRoom);

        Calendar airDate2 = new GregorianCalendar(2016, 6, 10, 19, 30);	// similar to airDate1
        event.addAirDateTime(airDate2, redRoom);

        Event fakeEvent = new Event();
        fakeEvent.setId(123L);

        Mockito.when(eventRepositoryMock.save(Mockito.any(Event.class))).thenReturn(fakeEvent);

        Event resultEvent = eventService.save(event);
		assertNotNull(resultEvent);

		ArgumentCaptor<Event> eventArgument = ArgumentCaptor.forClass(Event.class);
		Mockito.verify(eventRepositoryMock).save(eventArgument.capture());
		assertNotNull(eventArgument.getValue());
		assertTrue(eventArgument.getValue().getAirDates().contains(airDate1));
		assertTrue(eventArgument.getValue().getAirDates().contains(airDate2));
		assertTrue(eventArgument.getValue().getAuditoriums().get(airDate1).equals(blueRoom.getName()));
		assertFalse(eventArgument.getValue().getAuditoriums().get(airDate2).equals(redRoom.getName()));
		assertTrue(eventArgument.getValue().getAuditoriums().get(airDate2).equals(blueRoom.getName()));
	}

	private Set<Long> getSet(Long... args) {
		Set<Long> vipSeats = new HashSet<Long>();

		for (Long e : args) {
			vipSeats.add(e);
		}

		return vipSeats;
	}
}
