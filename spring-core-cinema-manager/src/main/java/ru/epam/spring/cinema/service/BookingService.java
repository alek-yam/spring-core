package ru.epam.spring.cinema.service;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.epam.spring.cinema.domain.BookingReport;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.PriceItem;
import ru.epam.spring.cinema.domain.PriceReport;
import ru.epam.spring.cinema.domain.Ticket;
import ru.epam.spring.cinema.domain.User;


/**
 * Manages tickets, prices, bookings.
 *
 * @author Alex_Yamskov
 */
public interface BookingService {

    /**
     * Getting price list for all events
     *
     * @return price list
     */
    public List<PriceItem> getPriceList();

    /**
     * Getting price when buying all supplied seats for particular event including available discount
     *
     * @param event
     *            Event to get base ticket price, vip seats and other
     *            information
     * @param date
     *            Date and time of event air
     * @param user
     *            User that buys ticket could be needed to calculate discount.
     *            Can be <code>null</code>
     * @param seats
     *            Set of seat numbers that user wants to buy
     * @return price report
     */
    public PriceReport getFinalPrice(@Nonnull Event event, @Nonnull Calendar date, @Nullable User user,
            @Nonnull Set<Long> seats);

    /**
     * Books tickets in internal system. If user is not
     * <code>null</code> in a ticket then booked tickets are saved with it
     *
     * @param event
     *            Event to get base ticket price, vip seats and other
     *            information
     * @param date
     *            Date and time of event air
     * @param user
     *            User that buys ticket could be needed to calculate discount.
     *            Can be <code>null</code>
     * @param seats
     *            Set of seat numbers that user wants to buy
     * @return booking
     */
    public BookingReport bookTickets(@Nonnull Event event, @Nonnull Calendar date, @Nullable User user, @Nonnull Set<Long> seats);

    /**
     * Getting all purchased tickets for event on specific air date and time
     *
     * @param event
     *            Event to get tickets for
     * @param date
     *            Date and time of airing of event
     * @return set of all purchased tickets
     */
    public @Nonnull Set<Ticket> getPurchasedTicketsForEvent(@Nonnull Event event, @Nonnull Calendar date);

    /**
     * Getting all purchased tickets for particular user
     *
     * @param user
     *            User to get tickets for
     * @return set of all purchased tickets
     */
    public @Nonnull Set<Ticket> getPurchasedTicketsForUser(@Nonnull User user);

}
