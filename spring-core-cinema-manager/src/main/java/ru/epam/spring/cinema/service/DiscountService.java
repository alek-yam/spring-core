package ru.epam.spring.cinema.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.EventAssignment;
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
     * @param assignment
     *            Event assignment that tickets are bought for
     * @param numberOfTickets
     *            Number of tickets that user buys
     * @return discount
     */
	DiscountReport getDiscount(@Nullable User user, @Nonnull EventAssignment assignment, long numberOfTickets);

}
