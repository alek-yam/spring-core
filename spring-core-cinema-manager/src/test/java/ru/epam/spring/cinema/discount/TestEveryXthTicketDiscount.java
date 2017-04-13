package ru.epam.spring.cinema.discount;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.service.discount.EveryXthTicketDiscount;

public class TestEveryXthTicketDiscount {

	private EveryXthTicketDiscount xthTicketDiscount;

	@Before
	public void init() {
		xthTicketDiscount = new EveryXthTicketDiscount(10, (byte) 50);
	}

	@Test
	public void getDiscountReturnsZeroIfNumberOfTicketsLessThanXthTicket() {
		DiscountReport discount = xthTicketDiscount.getDiscount(null, null, null, 4);
		assertEquals(0, discount.getPercent());	// 4 < 10
	}

	@Test
	public void getDiscountReturnsDiscountForEveryXthTicket() {
		DiscountReport discount = xthTicketDiscount.getDiscount(null, null, null, 36);
		assertEquals(4, discount.getPercent());	// (50 + 50 + 50) / 36 = 4,166666667 => 4
	}

}
