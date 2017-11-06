package ru.epam.spring.cinema.service.discount;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.domain.User;

/**
 * The class to calculate total discount if date of event is in certain time frame near to user's birthday.
 *
 * @author Alex_Yamskov
 */
@Component
public class BirthdayDiscount implements DiscountStrategy {
	private static final DiscountReport ZERO_DISCOUNT = new DiscountReport(BirthdayDiscount.class.getSimpleName(), (byte) 0);
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
    public DiscountReport getDiscount(User user, EventAssignment assignment, long numberOfTickets) {
		if (user == null || user.getBirthday() == null) {
			return ZERO_DISCOUNT;
		}

		if (isHappyAirDate(assignment.getAirDate(), user)) {
			return birthdayDiscount;
		}

	    return ZERO_DISCOUNT;
    }

	private boolean isHappyAirDate(LocalDateTime airDate, User user) {
		LocalDate eventDate = airDate.toLocalDate();
		log.log(Level.INFO, "airDate = " + eventDate.format(TIME_FORMATTER));

		LocalDate birthday = getRelevantBirthday(eventDate, user);
		log.log(Level.INFO, "birthday = " + birthday.format(TIME_FORMATTER));

		// calculating number of days before or after event
		long totalDays = ChronoUnit.DAYS.between(eventDate, birthday);
		log.log(Level.INFO, "Days before/after event: " + totalDays);

		return isInBirthdayTimeFrame(totalDays);
	}

	private LocalDate getRelevantBirthday(LocalDate eventDate, User user) {
		LocalDate birthday = user.getBirthday();
		LocalDate thisYearBirthday = birthday.withYear(eventDate.getYear());
		LocalDate prevYearBirthday = birthday.withYear(eventDate.getYear() - 1);
		LocalDate nextYearBirthday = birthday.withYear(eventDate.getYear() + 1);

		long thisYearDif = Math.abs(ChronoUnit.DAYS.between(eventDate, thisYearBirthday));
		long prevYearDif = Math.abs(ChronoUnit.DAYS.between(eventDate, prevYearBirthday));
		long nextYearDif = Math.abs(ChronoUnit.DAYS.between(eventDate, nextYearBirthday));

		LocalDate currentBirthday = thisYearBirthday;
		long currentDif = thisYearDif;

		if (prevYearDif < currentDif) {
			currentBirthday = prevYearBirthday;
		}

		if (nextYearDif < currentDif) {
			currentBirthday = nextYearBirthday;
		}

		return currentBirthday;
	}

	private boolean isInBirthdayTimeFrame(long totalDays) {
		if (totalDays >= -birthdayTimeFrame && totalDays <= birthdayTimeFrame) {
			log.log(Level.INFO, "Birthday discount is available!");
			return true;
		}

		log.log(Level.INFO, "No birthday discount is available.");
		return false;
	}
}
