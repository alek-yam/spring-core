package ru.epam.spring.cinema.domain;

import java.util.Calendar;

/**
 * The ticket definition.
 *
 * @author Alex_Yamskov
 */
public class Ticket extends DomainObject implements Comparable<Ticket> {
    private final Long userId;
    private final Long eventId;
    private final Calendar date;
    private final long seat;

    public Ticket(Long id, Long userId, Long eventId, Calendar date, long seat) {
    	super.setId(id);
        this.userId = userId;
        this.eventId = eventId;
        this.date = date;
        this.seat = seat;
    }

    public Ticket(Long userId, Long eventId, Calendar date, long seat) {
        this.userId = userId;
        this.eventId = eventId;
        this.date = date;
        this.seat = seat;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public Calendar getDate() {
        return date;
    }

    public long getSeat() {
        return seat;
    }

    @Override
    public int compareTo(Ticket other) {
        if (other == null) {
            return 1;
        }

        if (this.getId() != null) {
        	return this.getId().compareTo(other.getId());
        }

        if (other.getId() != null) {
        	return -1;
        }

        return 0;
    }
}
