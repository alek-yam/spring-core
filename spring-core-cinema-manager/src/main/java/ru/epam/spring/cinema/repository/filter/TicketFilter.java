package ru.epam.spring.cinema.repository.filter;

import java.util.Calendar;

import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.User;

/**
 * Filter to perform searching for users with specific parameters.
 *
 * @author Alex_Yamskov
 */
public class TicketFilter {

	private Event event;
	private Calendar date;
	private User user;

	public Event getEvent() {
	    return event;
    }

	public void setEvent(Event event) {
	    this.event = event;
    }

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

}
