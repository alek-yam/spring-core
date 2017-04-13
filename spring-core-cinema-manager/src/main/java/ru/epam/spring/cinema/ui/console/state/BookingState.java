package ru.epam.spring.cinema.ui.console.state;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.BookingReport;
import ru.epam.spring.cinema.domain.DomainObject;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.PriceReport;
import ru.epam.spring.cinema.domain.Ticket;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.service.AbstractService;
import ru.epam.spring.cinema.service.AuditoriumService;
import ru.epam.spring.cinema.service.BookingService;
import ru.epam.spring.cinema.service.EventService;
import ru.epam.spring.cinema.service.UserService;


public class BookingState extends AbstractState {

    private final BookingService bookingService;
    private final UserService userService;
    private final EventService eventService;
	private final AuditoriumService auditoriumService;

    public BookingState(ApplicationContext context) {
        this.bookingService = context.getBean(BookingService.class);
        this.userService = context.getBean(UserService.class);
        this.eventService = context.getBean(EventService.class);
        this.auditoriumService = context.getBean(AuditoriumService.class);
    }

    @Override
    protected void printDefaultInformation() {
        System.out.println("Lets book tickets!");
    }

    @Override
    protected int printMainActions() {
        System.out.println(" 1) Get tickets price");
        System.out.println(" 2) Book tickets");
        System.out.println(" 3) Get booked tickets");
        return 4;
    }

    @Override
    protected void runAction(int action) {
        switch (action) {
        case 1:
            getTicketsPrice();
            break;
        case 2:
            bookTickets();
            break;
        case 3:
            getBookedTickets();
            break;
        default:
            System.err.println("Unknown action");
        }
    }

    private void getBookedTickets() {
        System.out.println("> Select event: ");
        Event event = selectDomainObject(eventService);
        if (event == null) {
            System.err.println("No event found");
            return;
        }

        System.out.println("> Select air dates: ");
        Calendar airDate = selectAirDate(event.getAirDates());

        printDelimiter();
        Set<Ticket> bookedTickets = bookingService.getPurchasedTicketsForEvent(event, airDate);
        for (Ticket t : bookedTickets) {
        	String email = "unknown user";
        	if (t.getUserId() != null) {
        		User user = userService.getById(t.getUserId());
        		email = user.getEmail();
        	}
        	System.out.println("Seat " + t.getSeat() + "\t for " + email);
        }
    }

    private void bookTickets() {
        System.out.println("> Select event: ");
        final Event event = selectDomainObject(eventService);
        if (event == null) {
            System.err.println("No event found");
            return;
        }

        System.out.println("> Select air dates: ");
        final Calendar airDate = selectAirDate(event.getAirDates());
        System.out.println("> Select seats: ");
        final Set<Long> seats = selectSeats(event, airDate);
        System.out.println("> Select user: ");
        final User userForBooking;
        User user = selectDomainObject(userService);
        if (user == null) {
            System.out.println("No user found. Input user info for booking: ");
            String email = readStringInput("Email: ");
            String firstName = readStringInput("First name: ");
            String lastName = readStringInput("Last name: ");
            userForBooking = new User();
            userForBooking.setEmail(email);
            userForBooking.setFirstName(firstName);
            userForBooking.setLastName(lastName);
        } else {
            userForBooking = user;
        }

        BookingReport booking = bookingService.bookTickets(event, airDate, userForBooking, seats);
        double finalPrice = booking.getPriceReport().getFinalPrice();
        byte discountPercent = booking.getPriceReport().getDiscountReport().getPercent();

        System.out.println("Tickets booked! Total price: " + finalPrice
        		+ ", with discount: " + discountPercent + "%");
    }

    private void getTicketsPrice() {
        System.out.println("> Select event: ");
        Event event = selectDomainObject(eventService);
        if (event == null) {
            System.err.println("No event found");
            return;
        }

        System.out.println("> Select air dates: ");
        Calendar airDate = selectAirDate(event.getAirDates());
        System.out.println("> Select seats: ");
        Set<Long> seats = selectSeats(event, airDate);
        System.out.println("> Select user: ");
        User user = selectDomainObject(userService);
        if (user == null) {
            System.out.println("No user found");
        }

        PriceReport priceReport = bookingService.getFinalPrice(event, airDate, user, seats);
        double finalPrice = priceReport.getFinalPrice();
        byte discountPercent = priceReport.getDiscountReport().getPercent();
        printDelimiter();
        System.out.println("Price for tickets: " + finalPrice + ", with discount: " + discountPercent + "%");
    }

    private Set<Long> selectSeats(Event event, Calendar airDate) {
		String audName = event.getAuditoriums().get(airDate);
		Auditorium aud = auditoriumService.getByName(audName);

        Set<Ticket> tickets = bookingService.getPurchasedTicketsForEvent(event, airDate);

        List<Long> bookedSeats = new ArrayList<Long>();
        for (Ticket t : tickets) {
        	bookedSeats.add(t.getSeat());
        }

        List<Long> freeSeats = new ArrayList<Long>();
        for (Long seat : aud.getAllSeatNumbers()) {
        	if (!bookedSeats.contains(seat)) {
        		freeSeats.add(seat);
        	}
        }

        System.out.println("Free seats: ");
        System.out.println(freeSeats);

        return inputSeats();
    }

    private Set<Long> inputSeats() {
    	return readInput("Input seats (comma separated): ", setLongConverter);
    }

    private Calendar selectAirDate(NavigableSet<Calendar> airDates) {
    	List<Calendar> list = new ArrayList<Calendar>(airDates);
        for (int i = 0; i < list.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + formatDateTime(list.get(i)));
        }
        int dateIndex = readIntInput("Input air date index: ", list.size()) - 1;
        return list.get(dateIndex);
    }

    private <T extends DomainObject> T selectDomainObject(AbstractService<T> service) {
        if (!service.getAll().isEmpty()) {
        	for (T obj : service.getAll()) {
        		System.out.println("[" + obj.getId() + "] " + display(obj));
        	}
            long id = readIntInput("Input id (-1 for nothing): ");
            return service.getById(id);
        } else {
            return null;
        }
    }

    private <T extends DomainObject> String display(T obj) {
    	if (obj instanceof Event) {
    		Event e = (Event) obj;
    		return e.getName();
    	}

    	if (obj instanceof User) {
    		User u = (User) obj;
    		return u.getFirstName() + " " + u.getLastName();
    	}

    	return obj.getId().toString();
    }
}
