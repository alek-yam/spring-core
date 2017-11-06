package ru.epam.spring.cinema.domain;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * The booking definition.
 *
 * @author Alex_Yamskov
 */
public class BookingReport extends DomainObject implements Comparable<BookingReport> {
	private User user;
	private Collection<Ticket> tickets;
	private PriceReport priceReport;
	private LocalDateTime date;

	public BookingReport(User user, Collection<Ticket> tickets, PriceReport priceReport) {
		this.user = user;
		this.tickets = tickets;
		this.priceReport = priceReport;
		this.date = LocalDateTime.now();
	}

	public User getUser() {
		return user;
	}

	public Collection<Ticket> getTickets() {
		return tickets;
	}

	public PriceReport getPriceReport() {
	    return priceReport;
    }

	public LocalDateTime getDate() {
		return date;
	}

	@Override
    public int compareTo(BookingReport other) {

		if (other == null) {
			return 1;
		}

	    return date.compareTo(other.getDate());
    }

	@Override
	public String toString() {
		return "BookingReport [user=" + user
				+ ", priceReport=" + priceReport
				+ ", date=" + date
				+ ", tickets=" + tickets + "]";
	}
}
