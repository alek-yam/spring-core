package ru.epam.spring.cinema.ui.console.state;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.BookingReport;
import ru.epam.spring.cinema.domain.DomainObject;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventAssignment;
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
        Event event = selectDomainObject(eventService, e -> e.getName());
        if (event == null) {
            System.err.println("No event found");
            return;
        }

        System.out.println("> Select event assignment: ");
        final EventAssignment assignment = selectEventAssignment(event.getAssignments());

        printDelimiter();
        Set<Ticket> bookedTickets = bookingService.getPurchasedTicketsForEvent(assignment);
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
        final Event event = selectDomainObject(eventService, e -> e.getName());
        if (event == null) {
            System.err.println("No event found");
            return;
        }

        System.out.println("> Select event assignment: ");
        final EventAssignment assignment = selectEventAssignment(event.getAssignments());

        System.out.println("> Select seats: ");
        final Set<Long> seats = selectSeats(assignment);

        System.out.println("> Select user: ");
        final User userForBooking;
        User user = selectDomainObject(userService, u -> u.getFirstName() + " " + u.getLastName());
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

        BookingReport booking = bookingService.bookTickets(assignment, userForBooking, seats);
        double finalPrice = booking.getPriceReport().getFinalPrice();
        byte discountPercent = booking.getPriceReport().getDiscountReport().getPercent();

        System.out.println("Tickets booked! Total price: " + finalPrice
        		+ ", with discount: " + discountPercent + "%");
    }

    private void getTicketsPrice() {
        System.out.println("> Select event: ");
        Event event = selectDomainObject(eventService, e -> e.getName());
        if (event == null) {
            System.err.println("No event found");
            return;
        }

        System.out.println("> Select event assignment: ");
        final EventAssignment assignment = selectEventAssignment(event.getAssignments());

        System.out.println("> Select seats: ");
        final Set<Long> seats = selectSeats(assignment);

        System.out.println("> Select user: ");
        final User user = selectDomainObject(userService, u -> u.getFirstName() + " " + u.getLastName());
        if (user == null) {
            System.out.println("No user found");
        }

        PriceReport priceReport = bookingService.getFinalPrice(assignment, user, seats);
        double finalPrice = priceReport.getFinalPrice();
        byte discountPercent = priceReport.getDiscountReport().getPercent();
        printDelimiter();
        System.out.println("Price for tickets: " + finalPrice + ", with discount: " + discountPercent + "%");
    }

    private Set<Long> selectSeats(EventAssignment assignment) {
        Auditorium aud = auditoriumService.getById(assignment.getAuditoriumId());

        Set<Ticket> tickets = bookingService.getPurchasedTicketsForEvent(assignment);
        List<Long> bookedSeats = tickets.stream().map(t -> t.getSeat()).collect(Collectors.toList());
        List<Long> freeSeats = aud.getSeatNumbers().stream().filter(seat -> !bookedSeats.contains(seat))
                .collect(Collectors.toList());

        System.out.println("Free seats: ");
        System.out.println(freeSeats);

        return inputSeats();
    }

    private Set<Long> inputSeats() {
        Set<Long> set = readInput("Input seats (comma separated): ", s ->
            Arrays.stream(s.split(","))
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .boxed().collect(Collectors.toSet()));
        return set;
    }

    private EventAssignment selectEventAssignment(Set<EventAssignment> assignments) {
    	List<EventAssignment> list = assignments.stream().collect(Collectors.toList());
    	EventAssignment currAssignment;
        for (int i = 0; i < list.size(); i++) {
        	currAssignment = list.get(i);
        	Auditorium auditorium = auditoriumService.getById(currAssignment.getAuditoriumId());
        	LocalDateTime airDate = currAssignment.getAirDate();
            System.out.println("[" + (i + 1) + "] " + auditorium.getName() + ", " + formatDateTime(airDate));
        }
        int assignmentIndex = readIntInput("Input event assignment index: ", list.size()) - 1;
        return list.get(assignmentIndex);
    }

    private <T extends DomainObject> T selectDomainObject(AbstractService<T> service, Function<T, String> displayFunction) {
        if (!service.getAll().isEmpty()) {
        	for (T obj : service.getAll()) {
        		System.out.println("[" + obj.getId() + "] " + displayFunction.apply(obj));
        	}
            long id = readIntInput("Input id (-1 for nothing): ");
            return service.getById(id);
        } else {
            return null;
        }
    }
}
