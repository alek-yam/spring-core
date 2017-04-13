package ru.epam.spring.cinema.services;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.BookingReport;
import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventRating;
import ru.epam.spring.cinema.domain.Ticket;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.BookingRepository;
import ru.epam.spring.cinema.repository.TicketRepository;
import ru.epam.spring.cinema.service.AuditoriumService;
import ru.epam.spring.cinema.service.BookingService;
import ru.epam.spring.cinema.service.BookingServiceImpl;
import ru.epam.spring.cinema.service.DiscountService;
import ru.epam.spring.cinema.service.UserService;

public class TestBookingService {

	private BookingService bookingService;
	private BookingRepository bookingRepositoryMock;
	private TicketRepository ticketRepositoryMock;
	private DiscountService discountServiceMock;
	private AuditoriumService auditoriumServiceMock;
	private UserService userServiceMock;
	private Auditorium auditorium;
	private Calendar airDate;
	private Event event;
	private User user;

	@Before
	public void init() {
		bookingRepositoryMock = Mockito.mock(BookingRepository.class);
		ticketRepositoryMock = Mockito.mock(TicketRepository.class);
		discountServiceMock = Mockito.mock(DiscountService.class);
		auditoriumServiceMock = Mockito.mock(AuditoriumService.class);
		userServiceMock = Mockito.mock(UserService.class);

		BookingServiceImpl bookingServiceImpl = new BookingServiceImpl();
		bookingServiceImpl.setBookingRepository(bookingRepositoryMock);
		bookingServiceImpl.setTicketRepository(ticketRepositoryMock);
		bookingServiceImpl.setAuditoriumService(auditoriumServiceMock);
		bookingServiceImpl.setDiscountService(discountServiceMock);
		bookingServiceImpl.setUserService(userServiceMock);
		bookingService = bookingServiceImpl;

		airDate = new GregorianCalendar(2016, 6, 10, 19, 30);

		Set<Long> vipSeats = new HashSet<Long>();
		vipSeats.add(1L);
		vipSeats.add(2L);
		vipSeats.add(3L);
		auditorium = new Auditorium("Terminator", 10, vipSeats);

        event = new Event();
        event.setId(957L);
        event.setName("Grand concert");
        event.setRating(EventRating.MID);
        event.setBasePrice(10);
        event.addAirDateTime(airDate, auditorium);

		user = new User();
        user.setId(123L);
        user.setFirstName("Foo");
        user.setLastName("Bar");
        user.setBirthday(new GregorianCalendar(2001, 9, 21));
        user.setEmail("my@email.com");
	}

	@Test
	public void testBookTickets() {
		Mockito.when(auditoriumServiceMock.getByName(Mockito.anyString())).thenReturn(auditorium);

		DiscountReport fakeDiscount = new DiscountReport("BirthdayDiscount", (byte) 50);
		Mockito.when(discountServiceMock.getDiscount(
				Mockito.any(User.class),
				Mockito.any(Event.class),
				Mockito.any(Calendar.class),
				Mockito.anyLong())).thenReturn(fakeDiscount);

		Ticket fakeTicket1 = new Ticket(user.getId(), event.getId(), airDate, 5);
		fakeTicket1.setId(96L);
		Ticket fakeTicket2 = new Ticket(user.getId(), event.getId(), airDate, 6);
		fakeTicket2.setId(97L);
		Mockito.when(ticketRepositoryMock.save(Mockito.any(Ticket.class))).thenReturn(fakeTicket1).thenReturn(fakeTicket2);

		Mockito.when(userServiceMock.save(Mockito.any(User.class))).thenReturn(null);

		BookingReport fakeBooking = new BookingReport();
		Mockito.when(bookingRepositoryMock.save(Mockito.any(BookingReport.class))).thenReturn(fakeBooking);

		Set<Long> seats = new HashSet<Long>();
		seats.add(5L);
		seats.add(6L);
		bookingService.bookTickets(event, airDate, user, seats);
	}
}
