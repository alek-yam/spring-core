package ru.epam.spring.cinema.service.discount;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.domain.User;

/**
 * Contains logic for calculating discount.
 *
 * @author Alex_Yamskov
 */
public interface DiscountStrategy {

	DiscountReport getDiscount(@Nullable User user, @Nonnull EventAssignment assignment, long numberOfTickets);

}
