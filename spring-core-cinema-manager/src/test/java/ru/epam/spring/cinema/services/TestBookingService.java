package ru.epam.spring.cinema.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.domain.EventRating;
import ru.epam.spring.cinema.domain.Ticket;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.EventRepository;
import ru.epam.spring.cinema.repository.TicketRepository;
import ru.epam.spring.cinema.service.AccountService;
import ru.epam.spring.cinema.service.AuditoriumService;
import ru.epam.spring.cinema.service.BookingService;
import ru.epam.spring.cinema.service.BookingServiceImpl;
import ru.epam.spring.cinema.service.DiscountService;

public class TestBookingService {

	private BookingService bookingService;
	private EventRepository eventRepositoryMock;
	private TicketRepository ticketRepositoryMock;
	private AuditoriumService auditoriumServiceMock;
	private DiscountService discountServiceMock;
	private AccountService accountServiceMock;
	private Auditorium auditorium;
	private Event event;
	private EventAssignment assignmet;
	private User user;

	@Before
	public void init() {
		eventRepositoryMock = Mockito.mock(EventRepository.class);
		ticketRepositoryMock = Mockito.mock(TicketRepository.class);
		auditoriumServiceMock = Mockito.mock(AuditoriumService.class);
		discountServiceMock = Mockito.mock(DiscountService.class);
		accountServiceMock = Mockito.mock(AccountService.class);

		BookingServiceImpl bookingServiceImpl = new BookingServiceImpl();
		bookingServiceImpl.setEventRepository(eventRepositoryMock);
		bookingServiceImpl.setTicketRepository(ticketRepositoryMock);
		bookingServiceImpl.setAuditoriumService(auditoriumServiceMock);
		bookingServiceImpl.setDiscountService(discountServiceMock);
		bookingServiceImpl.setAccountService(accountServiceMock);
		bookingService = bookingServiceImpl;

		Set<Long> vipSeats = new HashSet<Long>();
		vipSeats.add(1L);
		vipSeats.add(2L);
		vipSeats.add(3L);
		auditorium = new Auditorium(1, "Terminator", 10, vipSeats);

        event = new Event();
        event.setId(957L);
        event.setName("Grand concert");
        event.setRating(EventRating.MID);
        event.setBasePrice(300);

        assignmet = new EventAssignment();
        assignmet.setId(355L);
        assignmet.setEventId(event.getId());
        assignmet.setAuditoriumId(1L);
        assignmet.setAirDate(LocalDateTime.of(2016, 7, 10, 19, 30));
        event.getAssignments().add(assignmet);

		user = new User();
        user.setId(123L);
        user.setFirstName("Foo");
        user.setLastName("Bar");
        user.setBirthday(LocalDate.of(2001, 10, 21));
        user.setEmail("my@email.com");
	}

	@Test
	public void testBookTickets() {
		Mockito.when(auditoriumServiceMock.getById(Mockito.anyLong())).thenReturn(auditorium);
		Mockito.when(eventRepositoryMock.getById(Mockito.anyLong())).thenReturn(event);

		DiscountReport fakeDiscount = new DiscountReport("BirthdayDiscount", (byte) 20);
		Mockito.when(discountServiceMock.getDiscount(
				Mockito.any(User.class),
				Mockito.any(EventAssignment.class),
				Mockito.anyLong())).thenReturn(fakeDiscount);

		Mockito.when(ticketRepositoryMock.save(Mockito.any(Ticket.class)))
			.thenReturn(new Ticket()).thenReturn(new Ticket());

		Mockito.doNothing().when(accountServiceMock).withdrawMoney(Mockito.any(User.class), Mockito.anyDouble());

		Set<Long> seats = new HashSet<Long>();
		seats.add(3L);
		seats.add(4L);
		bookingService.bookTickets(assignmet, user, seats);

		// verify tickets

		ArgumentCaptor<Ticket> ticketArg = ArgumentCaptor.forClass(Ticket.class);
		Mockito.verify(ticketRepositoryMock, Mockito.times(2)).save(ticketArg.capture());

		Ticket ticket1 = ticketArg.getAllValues().get(0);
		assertNotNull(ticket1);
		assertEquals(user.getId(), ticket1.getUserId());
		assertEquals(assignmet.getId(), ticket1.getEventAssignmentId());
		assertEquals(3, ticket1.getSeat().longValue());

		Ticket ticket2 = ticketArg.getAllValues().get(1);
		assertNotNull(ticket2);
		assertEquals(user.getId(), ticket2.getUserId());
		assertEquals(assignmet.getId(), ticket2.getEventAssignmentId());
		assertEquals(4, ticket2.getSeat().longValue());

		// verify user account

		ArgumentCaptor<User> userArg = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<Double> sumArg = ArgumentCaptor.forClass(Double.class);
		Mockito.verify(accountServiceMock).withdrawMoney(userArg.capture(), sumArg.capture());
		assertEquals(user.getId(), userArg.getValue().getId());
		assertEquals(user.getEmail(), userArg.getValue().getEmail());
		assertTrue(fakeDiscount.getPercent() < 100 && sumArg.getValue() > 0);
	}
}
