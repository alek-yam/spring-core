package ru.epam.spring.cinema.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.BookingReport;
import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventRating;
import ru.epam.spring.cinema.domain.PriceItem;
import ru.epam.spring.cinema.domain.PriceReport;
import ru.epam.spring.cinema.domain.Ticket;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.BookingRepository;
import ru.epam.spring.cinema.repository.EventRepository;
import ru.epam.spring.cinema.repository.TicketRepository;
import ru.epam.spring.cinema.repository.filter.TicketFilter;

/**
 * The BookingService implementation.
 *
 * @author Alex_Yamskov
 */
@Service
public class BookingServiceImpl implements BookingService {

	private static final double VIP_SEAT_FACTOR = 2.0;
	private static final double DEFAULT_RATING_FACTOR = 1.0;
	private static final double HIGH_RATING_FACTOR = 1.2;
	private static final double LOW_RATING_FACTOR = 0.9;

	private EventRepository eventRepository;
	private BookingRepository bookingRepository;
	private TicketRepository ticketRepository;
	private AuditoriumService auditoriumService;
	private DiscountService discountService;
	private UserService userService;
	private AccountService accountService;

	@Autowired
	public void setBookingRepository(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;
	}

	@Autowired
	@Qualifier("jdbcEventRepository")
	public void setEventRepository(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@Autowired
	@Qualifier("jdbcTicketRepository")
	public void setTicketRepository(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	@Autowired
	public void setAuditoriumService(AuditoriumService auditoriumService) {
		this.auditoriumService = auditoriumService;
	}

	@Autowired
	public void setDiscountService(DiscountService discountService) {
		this.discountService = discountService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	@Override
	public List<PriceItem> getPriceList() {
		Collection<Event> events = eventRepository.getAll();
		List<PriceItem> priceList = new ArrayList<PriceItem>(events.size());

		for (Event event : events) {
			PriceItem item = new PriceItem();
			item.setEventName(event.getName());
			item.setPrice(getTicketPrice(event, false));
			item.setVipPrice(getTicketPrice(event, true));
			priceList.add(item);
		}

		return priceList;
	}

	@Override
	public PriceReport getFinalPrice(@Nonnull Event event, @Nonnull Calendar date, @Nullable User user, @Nonnull Set<Long> seats) {
		String audName = event.getAuditoriums().get(date);
		Auditorium auditorium = auditoriumService.getByName(audName);
		double totalPrice = 0;

		for (Long sn : seats) {
			boolean isVip = auditorium.getSeats().get(sn).isVip();
			double ticketPrice = getTicketPrice(event, isVip);
			totalPrice += ticketPrice;
		}

		DiscountReport discountReport =  discountService.getDiscount(user, event, date, seats.size());
		double discount = totalPrice * discountReport.getPercent() / 100;
		double finalPrice = totalPrice - discount;

		return new PriceReport(totalPrice, finalPrice, discountReport);
	}

	@Override
	@Transactional
	public BookingReport bookTickets(@Nonnull Event event, @Nonnull Calendar date, @Nullable User user, @Nonnull Set<Long> seats) {
		BookingReport booking = new BookingReport();
		booking.setDate(Calendar.getInstance());

		PriceReport priceReport = getFinalPrice(event, date, user, seats);
		booking.setPriceReport(priceReport);

        Map<Long, Ticket> tickets = new TreeMap<>();
        for (Long seat : seats) {
			Ticket bookedTicket = ticketRepository.save(new Ticket(user.getId(), event.getId(), date, seat));
			tickets.put(bookedTicket.getId(), bookedTicket);
        }

        booking.setTickets(new TreeSet<>(tickets.values()));

        if (user != null && user.getId() != null
        		&& userService.getById(user.getId()) != null) {
        	user.getTickets().addAll(tickets.keySet());
        	userService.save(user);
    		booking.setUser(user);
        }

        BookingReport bookingReport = bookingRepository.save(booking);
        accountService.withdrawMoney(user, priceReport.getFinalPrice());

		return bookingReport;
	}

	@Override
	public @Nonnull
	Set<Ticket> getPurchasedTicketsForEvent(@Nonnull Event event, @Nonnull Calendar date) {
		TicketFilter filter = new TicketFilter();
		filter.setEvent(event);
		filter.setDate(date);
		Collection<Ticket> foundTickets = ticketRepository.getByFilter(filter);
		return new TreeSet<Ticket>(foundTickets);
	}

	@Override
	public Set<Ticket> getPurchasedTicketsForUser(User user) {
		TicketFilter filter = new TicketFilter();
		filter.setUser(user);
		Collection<Ticket> foundTickets = ticketRepository.getByFilter(filter);
		return new TreeSet<Ticket>(foundTickets);
	}

	private static double getRatingFactor(EventRating rating) {
		if (rating == EventRating.HIGH) {
			return HIGH_RATING_FACTOR;
		}

		if (rating == EventRating.LOW) {
			return LOW_RATING_FACTOR;
		}

		return DEFAULT_RATING_FACTOR;
	}

	private static double getTicketPrice(Event event, boolean isVip) {
		double basePrice = event.getBasePrice();
		double ratingFactor = getRatingFactor(event.getRating());
		double ticketPrice = basePrice * ratingFactor;

		if (isVip) {
			ticketPrice *= VIP_SEAT_FACTOR;
		}

		return ticketPrice;
	}

}
