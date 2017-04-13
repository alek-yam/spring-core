package ru.epam.spring.cinema.service.discount;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.User;

/**
 * The class to calculate total discount if date of event is in certain time frame near to user's birthday.
 *
 * @author Alex_Yamskov
 */
@Component
public class BirthdayDiscount implements DiscountStrategy {
	private static final DiscountReport ZERO_DISCOUNT = new DiscountReport(BirthdayDiscount.class.getSimpleName(), (byte) 0);

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static Logger log = Logger.getLogger(BirthdayDiscount.class.getName());

	static {
		log.setLevel(Level.OFF);
	}

	private final int birthdayTimeFrame;
	private final DiscountReport birthdayDiscount;

	@Autowired
	public BirthdayDiscount(@Value("5") int birthdayTimeFrame, @Value("5") byte birthdayDiscount) {
		this.birthdayTimeFrame = birthdayTimeFrame;
		this.birthdayDiscount = new DiscountReport(BirthdayDiscount.class.getSimpleName(), birthdayDiscount);
	}

	@Override
    public DiscountReport getDiscount(User user, Event event, Calendar airDate, long numberOfTickets) {
		if (user == null || user.getBirthday() == null) {
			return ZERO_DISCOUNT;
		}

		if (isHappyAirDate(airDate, user)) {
			return birthdayDiscount;
		}

	    return ZERO_DISCOUNT;
    }

	private boolean isHappyAirDate(Calendar airDate, User user) {
		log.log(Level.INFO, "airDate = " + dateFormat.format(airDate.getTime()));

		Calendar dateFrom = (Calendar) airDate.clone();
		dateFrom.add(Calendar.DAY_OF_YEAR, -birthdayTimeFrame);
		log.log(Level.INFO, "dateFrom = " + dateFormat.format(dateFrom.getTime()));

		Calendar dateTo = (Calendar) airDate.clone();
		dateTo.add(Calendar.DAY_OF_YEAR, birthdayTimeFrame);
		log.log(Level.INFO, "dateTo = " + dateFormat.format(dateTo.getTime()));

		Calendar birthday = user.getBirthday();
		log.log(Level.INFO, "birthday = " + dateFormat.format(birthday.getTime()));

		int airDateYear = airDate.get(Calendar.YEAR);
		int birthdayMonth = birthday.get(Calendar.MONTH);
		int birthdayDayOfMonth = birthday.get(Calendar.DAY_OF_MONTH);
		Calendar celebration = new GregorianCalendar(airDateYear, birthdayMonth, birthdayDayOfMonth);

		if (checkCelebrationDate(celebration, dateFrom, dateTo)) {
			return true;
		}

		celebration.add(Calendar.YEAR, 1);
		if (checkCelebrationDate(celebration, dateFrom, dateTo)) {
			return true;
		}

		celebration.add(Calendar.YEAR, -2);
		if (checkCelebrationDate(celebration, dateFrom, dateTo)) {
			return true;
		}

		return false;
	}

	private boolean checkCelebrationDate(Calendar celebration, Calendar dateFrom, Calendar dateTo) {
		log.log(Level.INFO, "celebration = " + dateFormat.format(celebration.getTime()));

		if (celebration.after(dateFrom)) {
			if (celebration.before(dateTo)) {
				log.log(Level.INFO, "Birthday discount is available!!!");
				return true;
			}
		}

		log.log(Level.INFO, "No birthday discount is available.");
		return false;
	}
}
