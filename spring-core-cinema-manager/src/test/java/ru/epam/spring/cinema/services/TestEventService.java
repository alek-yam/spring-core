package ru.epam.spring.cinema.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.domain.EventRating;
import ru.epam.spring.cinema.repository.EventRepository;
import ru.epam.spring.cinema.service.EventService;
import ru.epam.spring.cinema.service.EventServiceImpl;

public class TestEventService {

	private EventService eventService;
	private EventRepository eventRepositoryMock;

	private Auditorium blueRoom;
	private Auditorium greenRoom;
	//private Auditorium redRoom;

	@Before
	public void init() {
		eventRepositoryMock = Mockito.mock(EventRepository.class);

		EventServiceImpl eventServiceImpl = new EventServiceImpl();
		eventServiceImpl.setAuditoriumRepository(eventRepositoryMock);
		eventService = eventServiceImpl;

		blueRoom = new Auditorium(1, "Blue room", 20, Stream.of(1L, 2L, 3L).collect(Collectors.toSet()));
		greenRoom = new Auditorium(2, "Green room", 40, Stream.of(1L, 2L, 3L, 4L, 5L).collect(Collectors.toSet()));
		//redRoom = new Auditorium(3, "Red room", 10, Stream.of(1L).collect(Collectors.toSet()));
	}

	@Test
	public void eventCanBePresentedOnSeveralDatesAndSeveralTimesWithinEachDay() {
		Event event = new Event();
		event.setName("Matrix");
		event.setRating(EventRating.MID);
		event.setBasePrice(10);

		EventAssignment assignment1 = new EventAssignment();
		assignment1.setAuditoriumId(blueRoom.getId());
		assignment1.setAirDate(LocalDateTime.of(2017, 7, 10, 19, 30));
		event.getAssignments().add(assignment1);

		EventAssignment assignment21 = new EventAssignment();
		assignment21.setAuditoriumId(blueRoom.getId());
		assignment21.setAirDate(LocalDateTime.of(2017, 7, 11, 19, 30));
		event.getAssignments().add(assignment21);

		EventAssignment assignment22 = new EventAssignment();
		assignment22.setAuditoriumId(blueRoom.getId());
		assignment22.setAirDate(LocalDateTime.of(2017, 7, 11, 21, 30));
		event.getAssignments().add(assignment22);

		EventAssignment assignment23 = new EventAssignment();
		assignment23.setAuditoriumId(greenRoom.getId());
		assignment23.setAirDate(LocalDateTime.of(2017, 7, 11, 21, 30));
		event.getAssignments().add(assignment23);

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
		assertEquals(10, eventArgument.getValue().getBasePrice(), 0.000001);
		assertTrue(eventArgument.getValue().getAssignments().contains(assignment1));
		assertTrue(eventArgument.getValue().getAssignments().contains(assignment21));
		assertTrue(eventArgument.getValue().getAssignments().contains(assignment22));
		assertTrue(eventArgument.getValue().getAssignments().contains(assignment23));
	}

	@Test
	public void eventCannotBePresentedOnTheSameDateAndTimeInTheSameAuditoriumTwice() {
		Event event = new Event();
		event.setName("Matrix");
		event.setRating(EventRating.MID);
		event.setBasePrice(10);

		EventAssignment assignment1 = new EventAssignment();
		assignment1.setAuditoriumId(blueRoom.getId());
		assignment1.setAirDate(LocalDateTime.of(2017, 7, 10, 19, 30));

		EventAssignment assignment2 = new EventAssignment();
		assignment2.setAuditoriumId(blueRoom.getId());
		assignment2.setAirDate(LocalDateTime.of(2017, 7, 10, 19, 30));

		assertTrue(event.getAssignments().add(assignment1));
		assertFalse(event.getAssignments().add(assignment2));
	}
}
