package ru.epam.spring.cinema.service;

import java.util.Calendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.User;

/**
 * Counts different discounts for purchased tickets.
 *
 * @author Alex_Yamskov
 */
public interface DiscountService {

    /**
     * Getting discount based on some rules for user that buys some number of
     * tickets for the specific date time of the event
     *
     * @param user
     *            User that buys tickets. Can be <code>null</code>
     * @param event
     *            Event that tickets are bought for
     * @param airDate
     *            The date and time event will be aired
     * @param numberOfTickets
     *            Number of tickets that user buys
     * @return discount
     */
	DiscountReport getDiscount(@Nullable User user, @Nonnull Event event, @Nonnull Calendar airDate, long numberOfTickets);

}
