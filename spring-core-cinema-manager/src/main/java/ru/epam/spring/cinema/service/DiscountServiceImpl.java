package ru.epam.spring.cinema.service;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.service.discount.DiscountStrategy;

/**
 * The DiscountService implementation.
 *
 * @author Alex_Yamskov
 */
@Service
public class DiscountServiceImpl implements DiscountService {

	List<DiscountStrategy> strategies;

	@Autowired
	public DiscountServiceImpl(List<DiscountStrategy> strategies) {
		this.strategies = strategies;
	}

	@Override
	public DiscountReport getDiscount(@Nullable User user, @Nonnull Event event,  @Nonnull Calendar airDate, long numberOfTickets) {
		DiscountReport maxDiscount = null;

		for (DiscountStrategy s : strategies) {
			DiscountReport d = s.getDiscount(user, event, airDate, numberOfTickets);
			if (maxDiscount == null || d.getPercent() > maxDiscount.getPercent()) {
				maxDiscount = d;
			}
		}

		return maxDiscount;
	}

}