package ru.epam.spring.cinema.service;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.epam.spring.cinema.domain.BookingReport;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.domain.PriceReport;
import ru.epam.spring.cinema.domain.Ticket;
import ru.epam.spring.cinema.domain.TicketPrice;
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
    public List<TicketPrice> getPriceList();

    /**
     * Getting price when buying all supplied seats for particular event including available discount
     *
     * @param assignment
     *            Event assignment to get base ticket price, vip seats and other
     *            information
     * @param user
     *            User that buys ticket could be needed to calculate discount.
     *            Can be <code>null</code>
     * @param seats
     *            Set of seat numbers that user wants to buy
     * @return price report
     */
    public PriceReport getFinalPrice(@Nonnull EventAssignment assignment, @Nullable User user, @Nonnull Set<Long> seats);

    /**
     * Books tickets in internal system. If user is not
     * <code>null</code> in a ticket then booked tickets are saved with it
     *
     * @param assignment
     *            Event assignment to get base ticket price, vip seats and other
     *            information
     * @param user
     *            User that buys ticket could be needed to calculate discount.
     *            Can be <code>null</code>
     * @param seats
     *            Set of seat numbers that user wants to buy
     * @return booking
     */
    public BookingReport bookTickets(@Nonnull EventAssignment assignment, @Nullable User user, @Nonnull Set<Long> seats);

    /**
     * Getting all purchased tickets for specific event assignment
     *
     * @param assignment
     *            Event assignment to get tickets for
     * @param date
     *            Date and time of airing of event
     * @return set of all purchased tickets
     */
    public @Nonnull Set<Ticket> getPurchasedTicketsForEvent(@Nonnull EventAssignment assignment);

    /**
     * Getting all purchased tickets for particular user
     *
     * @param user
     *            User to get tickets for
     * @return set of all purchased tickets
     */
    public @Nonnull Set<Ticket> getPurchasedTicketsForUser(@Nonnull User user);

}
