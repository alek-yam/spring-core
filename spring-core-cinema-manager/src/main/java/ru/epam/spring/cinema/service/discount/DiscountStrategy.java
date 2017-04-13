package ru.epam.spring.cinema.service.discount;

import java.util.Calendar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.User;

/**
 * Contains logic for calculating discount.
 *
 * @author Alex_Yamskov
 */
public interface DiscountStrategy {

	DiscountReport getDiscount(@Nullable User user, @Nonnull Event event, @Nonnull Calendar airDate, long numberOfTickets);

}
