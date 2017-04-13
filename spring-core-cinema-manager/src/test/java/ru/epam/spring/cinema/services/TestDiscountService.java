package ru.epam.spring.cinema.services;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventRating;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.service.DiscountService;
import ru.epam.spring.cinema.service.DiscountServiceImpl;
import ru.epam.spring.cinema.service.discount.DiscountStrategy;

public class TestDiscountService {

	DiscountStrategy strategyMock1;
	DiscountStrategy strategyMock2;
	DiscountService discountService;

	@Before
	public void init() {
		strategyMock1 = Mockito.mock(DiscountStrategy.class);
		strategyMock2 = Mockito.mock(DiscountStrategy.class);

		List<DiscountStrategy> strategies = new ArrayList<DiscountStrategy>();
		strategies.add(strategyMock1);
		strategies.add(strategyMock2);

		discountService = new DiscountServiceImpl(strategies);
	}

	@Test
	public void discountsAreNotAddUp() {
        User user = new User();
        user.setFirstName("Foo");
        user.setLastName("Bar");
        Calendar birthday = new GregorianCalendar(2001, 9, 21);
        user.setBirthday(birthday);
        user.setEmail("my@email.com");

        Event event = new Event();
        event.setName("Grand concert");
        event.setRating(EventRating.MID);
        event.setBasePrice(10);
        Calendar airDate = new GregorianCalendar(2020, 6, 15, 19, 30);
        Auditorium auditorium = new Auditorium("Blue room", 20, getSet(1L, 2L, 3L));
        event.addAirDateTime(airDate, auditorium);

        DiscountReport fakeDiscount1 = new DiscountReport("strategy1", (byte) 5);
        DiscountReport fakeDiscount2 = new DiscountReport("strategy2", (byte) 10);

		Mockito.when(strategyMock1.getDiscount(
				Mockito.any(User.class),
				Mockito.any(Event.class),
				Mockito.any(Calendar.class),
				Mockito.anyLong())).thenReturn(fakeDiscount1);

		Mockito.when(strategyMock2.getDiscount(
				Mockito.any(User.class),
				Mockito.any(Event.class),
				Mockito.any(Calendar.class),
				Mockito.anyLong())).thenReturn(fakeDiscount2);

		DiscountReport discount = discountService.getDiscount(user, event, airDate, 15);
		assertEquals(10, discount.getPercent());
		assertEquals("strategy2", discount.getStrategyId());
	}

	private Set<Long> getSet(Long... args) {
		Set<Long> vipSeats = new HashSet<Long>();

		for (Long e : args) {
			vipSeats.add(e);
		}

		return vipSeats;
	}
}