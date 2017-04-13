package ru.epam.spring.cinema.discount;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.service.discount.BirthdayDiscount;

public class TestBirthdayDiscount {

	private User user;
	private BirthdayDiscount birthdayDiscount;

	@Before
	public void init() {
		user = new User();
		user.setBirthday(new GregorianCalendar(2000, 0, 1));	// 2000-Jan-1
		birthdayDiscount = new BirthdayDiscount(5, (byte) 5);
	}

	@Test
	public void noDiscountIfAirDateBeforeBirthdayTimeFrame() {
		Calendar airDate = new GregorianCalendar(2015, 11, 20);	// 2015-Dec-20
		DiscountReport discount = birthdayDiscount.getDiscount(user, null, airDate, 0);
		assertEquals(0, discount.getPercent());	// [2015-Dec-15 - 2015-Dec-25] doesn't contain user's birthday (2016-Jan-1)
	}

	@Test
	public void noDiscountIfAirDateAfterBirthdayTimeFrame() {
		Calendar airDate = new GregorianCalendar(2016, 0, 10);	// 2016-Jan-10
		DiscountReport discount = birthdayDiscount.getDiscount(user, null, airDate, 0);
		assertEquals(0, discount.getPercent());	// [2016-Jan-5 - 2016-Jan-15] doesn't contain user's birthday (2016-Jan-1)
	}

	@Test
	public void discountAvailableIfAirDateInTimeFrameBeforeBirthday() {
		Calendar airDate = new GregorianCalendar(2015, 11, 30);	// 2015-Dec-30
		DiscountReport discount = birthdayDiscount.getDiscount(user, null, airDate, 0);
		assertEquals(5, discount.getPercent());	// [2015-Dec-25 - 2016-Jan-4] contains user's birthday (2016-Jan-1)
	}

	@Test
	public void discountAvailableIfAirDateInTimeFrameAfterBirthday() {
		Calendar airDate = new GregorianCalendar(2016, 0, 4);	// 2016-Jan-4
		DiscountReport discount = birthdayDiscount.getDiscount(user, null, airDate, 0);
		assertEquals(5, discount.getPercent());	// [2015-Dec-25 - 2016-Jan-4] contains user's birthday (2016-Jan-1)
	}
}
