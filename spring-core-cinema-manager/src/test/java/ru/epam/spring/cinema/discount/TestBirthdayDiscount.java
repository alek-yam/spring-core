package ru.epam.spring.cinema.discount;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.service.discount.BirthdayDiscount;

public class TestBirthdayDiscount {

	private User user;
	private BirthdayDiscount birthdayDiscount;

	@Before
	public void init() {
		user = new User();
		user.setBirthday(LocalDate.of(2000, 1, 1));
		birthdayDiscount = new BirthdayDiscount(5, (byte) 5);
	}

	@Test
	public void noDiscountIfAirDateBeforeBirthdayTimeFrame() {
		EventAssignment assignment = createEventAssignment(2015, 12, 20, 8, 0);
		DiscountReport discount = birthdayDiscount.getDiscount(user, assignment, 0);
		assertEquals(0, discount.getPercent());	// [2015-Dec-15 - 2015-Dec-25] doesn't contain user's birthday (2016-Jan-1)
	}

	@Test
	public void noDiscountIfAirDateAfterBirthdayTimeFrame() {
		EventAssignment assignment = createEventAssignment(2016, 1, 10, 8, 0);
		DiscountReport discount = birthdayDiscount.getDiscount(user, assignment, 0);
		assertEquals(0, discount.getPercent());	// [2016-Jan-5 - 2016-Jan-15] doesn't contain user's birthday (2016-Jan-1)
	}

	@Test
	public void discountAvailableIfAirDateInTimeFrameBeforeBirthday() {
		EventAssignment assignment = createEventAssignment(2015, 12, 30, 12, 0);
		DiscountReport discount = birthdayDiscount.getDiscount(user, assignment, 0);
		assertEquals(5, discount.getPercent());	// [2015-Dec-25 - 2016-Jan-4] contains user's birthday (2016-Jan-1)
	}

	@Test
	public void discountAvailableIfAirDateInTimeFrameAfterBirthday() {
		EventAssignment assignment = createEventAssignment(2016, 1, 4, 23, 0);
		DiscountReport discount = birthdayDiscount.getDiscount(user, assignment, 0);
		assertEquals(5, discount.getPercent());	// [2015-Dec-25 - 2016-Jan-4] contains user's birthday (2016-Jan-1)
	}

	@Test
	public void discountAvailableIfAirDateEqualsThisYearBirthday() {
		int monthOfBirthday = user.getBirthday().getMonthValue();
		int dayOfBirthday = user.getBirthday().getDayOfMonth();
		EventAssignment assignment = createEventAssignment(2016, monthOfBirthday, dayOfBirthday, 23, 0);
		DiscountReport discount = birthdayDiscount.getDiscount(user, assignment, 0);
		assertEquals(5, discount.getPercent());	// [2015-Dec-25 - 2016-Jan-4] contains user's birthday (2016-Jan-1)
	}

	private EventAssignment createEventAssignment(int year, int month, int dayOfMonth, int hour, int minute) {
		EventAssignment assignment = new EventAssignment();
		assignment.setAirDate(LocalDateTime.of(year, month, dayOfMonth, hour, minute));
		return assignment;
	}
}
