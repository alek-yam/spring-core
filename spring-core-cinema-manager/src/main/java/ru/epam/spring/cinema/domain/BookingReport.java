package ru.epam.spring.cinema.domain;

import java.util.Calendar;
import java.util.Set;

/**
 * The booking definition.
 *
 * @author Alex_Yamskov
 */
public class BookingReport extends DomainObject implements Comparable<BookingReport> {

	private Calendar date;
	private User user;
	private PriceReport priceReport;
	private Set<Ticket> tickets;

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PriceReport getPriceReport() {
	    return priceReport;
    }

	public void setPriceReport(PriceReport priceReport) {
	    this.priceReport = priceReport;
    }

	public Set<Ticket> getTickets() {
		return tickets;
	}

	public void setTickets(Set<Ticket> tickets) {
		this.tickets = tickets;
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
		return "BookingReport [date=" + date
				+ ", user=" + user
				+ ", priceReport=" + priceReport
				+ ", tickets=" + tickets + "]";
	}
}
