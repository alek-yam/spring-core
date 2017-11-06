package ru.epam.spring.cinema.domain;

/**
 * The ticket definition.
 *
 * @author Alex_Yamskov
 */
public class Ticket extends DomainObject implements Comparable<Ticket> {
    private Long userId;
    private Long eventAssignmentId;
    private Long seat;

    public Ticket() {}

    public Ticket(long userId, long eventAssignmentId, long seat) {
        this.userId = userId;
        this.eventAssignmentId = eventAssignmentId;
        this.seat = seat;
    }

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEventAssignmentId() {
		return eventAssignmentId;
	}

	public void setEventAssignmentId(Long eventAssignmentId) {
		this.eventAssignmentId = eventAssignmentId;
	}

	public Long getSeat() {
		return seat;
	}

	public void setSeat(Long seat) {
		this.seat = seat;
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

	@Override
	public String toString() {
		return "Ticket [userId=" + userId + ", eventAssignmentId=" + eventAssignmentId + ", seat=" + seat + "]";
	}
}
