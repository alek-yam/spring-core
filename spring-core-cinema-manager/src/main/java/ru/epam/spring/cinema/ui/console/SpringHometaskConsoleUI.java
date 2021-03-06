package ru.epam.spring.cinema.ui.console;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.context.ApplicationContext;

import ru.epam.spring.cinema.aspect.CounterAspect;
import ru.epam.spring.cinema.aspect.DiscountAspect;
import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.domain.EventRating;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.service.AccountService;
import ru.epam.spring.cinema.service.AuditoriumService;
import ru.epam.spring.cinema.service.BookingService;
import ru.epam.spring.cinema.service.EventService;
import ru.epam.spring.cinema.service.UserService;
import ru.epam.spring.cinema.statistic.DiscountStatistic;
import ru.epam.spring.cinema.statistic.EventStatistic;
import ru.epam.spring.cinema.ui.console.state.MainState;


/**
 * Simple console UI application for the hometask code. UI provides different
 * action to input and output data.
 *
 * @author Yuriy_Tkach/Alex_Yamskov
 */
public class SpringHometaskConsoleUI {

    private final ApplicationContext context;

    public SpringHometaskConsoleUI(ApplicationContext context) {
    	this.context = context;
    }

    public void run() {
        System.out.println("Welcome to movie theater console service");

        fillInitialData();

        MainState state = new MainState(context);

        state.run();

        System.out.println("Exiting.. Thank you.");

        printStatistics();
    }

    private void fillInitialData() {
        UserService userService = context.getBean(UserService.class);
        AccountService accountService = context.getBean(AccountService.class);
        EventService eventService = context.getBean(EventService.class);
        AuditoriumService auditoriumService = context.getBean(AuditoriumService.class);
        BookingService bookingService = context.getBean(BookingService.class);

        Auditorium auditorium = auditoriumService.getAll().iterator().next();
        if (auditorium == null) {
            throw new IllegalStateException("Failed to fill initial data - no auditoriums returned from AuditoriumService");
        }
        if (auditorium.getCapacity() <= 0) {
            throw new IllegalStateException("Failed to fill initial data - no seats in the auditorium " + auditorium.getName());
        }

        User user = new User();
        user.setFirstName("Vladimir");
        user.setLastName("Kovalev");
        user.setBirthday(LocalDate.of(1981, 4, 20));
        user.setEmail("my@email.com");
        user.setPassword("vlad123");
        user = userService.save(user);
        accountService.putMoney(user, 1000);

        Event event = new Event();
        event.setName("Grand concert");
        event.setRating(EventRating.MID);
        event.setBasePrice(500);

        EventAssignment assignment = new EventAssignment();
        assignment.setAuditoriumId(auditorium.getId());
        LocalDateTime airDate = LocalDateTime.of(2020, 7, 15, 19, 30);
        assignment.setAirDate(airDate);
        event.getAssignments().add(assignment);
        event = eventService.save(event);

        bookingService.bookTickets(assignment, user, Collections.singleton(1L));

//        if (auditorium.getCapacity() > 1) {
//            User userNotRegistered = new User();
//            userNotRegistered.setFirstName("A");
//            userNotRegistered.setLastName("Somebody");
//            userNotRegistered.setEmail("somebody@a.b");
//            bookingService.bookTickets(assignment, userNotRegistered, Collections.singleton(2L));
//        }
    }

    private void printStatistics() {
    	CounterAspect counterAspect = context.getBean(CounterAspect.class);
    	DiscountAspect discountAspect = context.getBean(DiscountAspect.class);
    	UserService userService = context.getBean(UserService.class);

    	System.out.println("\n--------------------------------------------");

    	for (EventStatistic stat : counterAspect.getStatistics()) {
    		System.out.println(stat.toString());
    	}

    	for (DiscountStatistic stat : discountAspect.getStatistics()) {
    		System.out.println(stat.toString(userService));
    	}
    }
}
