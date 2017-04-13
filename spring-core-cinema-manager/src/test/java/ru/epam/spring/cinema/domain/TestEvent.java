package ru.epam.spring.cinema.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Yuriy_Tkach
 */
public class TestEvent {

	private Event event;
	private Auditorium auditorium;

	@Before
	public void initEvent() {
		event = new Event();
		event.setBasePrice(1.1);
		event.setName("aaa");
		event.setRating(EventRating.HIGH);

		Calendar now = new GregorianCalendar();
		Calendar p1 = new GregorianCalendar();
		p1.add(Calendar.DAY_OF_MONTH, 1);
		Calendar p2 = new GregorianCalendar();
		p2.add(Calendar.DAY_OF_MONTH, 2);

		event.addAirDateTime(now);
		event.addAirDateTime(p1);
		event.addAirDateTime(p2);

		Set<Long> vipSeats = new HashSet<Long>();
		vipSeats.add(1L);
		vipSeats.add(2L);
		vipSeats.add(3L);

		auditorium = new Auditorium("Terminator", 10, vipSeats);
	}

	@Test
	public void testAddRemoveAirDates() {
		int size = event.getAirDates().size();

		Calendar date = new GregorianCalendar();
		date.add(Calendar.DAY_OF_MONTH, 5);

		event.addAirDateTime(date);

		assertEquals(size+1, event.getAirDates().size());
		assertTrue(event.getAirDates().contains(date));

		event.removeAirDateTime(date);

		assertEquals(size, event.getAirDates().size());
		assertFalse(event.getAirDates().contains(date));
	}

	@Ignore
	@Test
	public void testCheckAirDates() {
		Calendar now = new GregorianCalendar();

		Calendar p1 = new GregorianCalendar();
		p1.add(Calendar.DAY_OF_MONTH, 1);

		Calendar p10 = new GregorianCalendar();
		p10.add(Calendar.DAY_OF_MONTH, 10);

		Calendar m5 = new GregorianCalendar();
		m5.add(Calendar.DAY_OF_MONTH, -5);

		Calendar m10 = new GregorianCalendar();
		m10.add(Calendar.DAY_OF_MONTH, -10);

		assertTrue(event.airsOnDate(now));
		assertTrue(event.airsOnDate(p1));
		assertFalse(event.airsOnDate(p10));

		assertTrue(event.airsOnDates(now, p10));
		assertTrue(event.airsOnDates(m10, p10));
		assertTrue(event.airsOnDates(p1, p1));
		assertFalse(event.airsOnDates(m10, m5));

		Calendar time = new GregorianCalendar();
		time.add(Calendar.HOUR_OF_DAY, 4);
		event.addAirDateTime(time);
		assertTrue(event.airsOnDateTime(time));
		time.add(Calendar.HOUR_OF_DAY, 30);
		assertFalse(event.airsOnDateTime(time));
	}

	@Test
	public void testAddRemoveAuditoriums() {
		Calendar time = event.getAirDates().first();

		assertTrue(event.getAuditoriums().isEmpty());

		event.assignAuditorium(time, auditorium);

		assertFalse(event.getAuditoriums().isEmpty());

		event.removeAuditoriumAssignment(time);

		assertTrue(event.getAuditoriums().isEmpty());
	}

	@Test
	public void testAddRemoveAuditoriumsWithAirDates() {
		Calendar time = new GregorianCalendar();
		time.add(Calendar.DAY_OF_MONTH, 10);

		assertTrue(event.getAuditoriums().isEmpty());

		event.addAirDateTime(time, auditorium);

		assertFalse(event.getAuditoriums().isEmpty());

		event.removeAirDateTime(time);

		assertTrue(event.getAuditoriums().isEmpty());
	}

	@Test
	public void testNotAddAuditoriumWithoutCorrectDate() {
		Calendar time = new GregorianCalendar();
		time.add(Calendar.DAY_OF_MONTH, 10);

		boolean result = event.assignAuditorium(time, auditorium);

		assertFalse(result);
		assertTrue(event.getAuditoriums().isEmpty());

		result = event.removeAirDateTime(time);
		assertFalse(result);

		assertTrue(event.getAuditoriums().isEmpty());
	}

}
