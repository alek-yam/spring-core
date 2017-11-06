package ru.epam.spring.cinema.domain;

import java.time.LocalDateTime;

public class EventAssignment extends DomainObject implements Comparable<EventAssignment> {
	private Long eventId;
	private Long auditoriumId;
	private LocalDateTime airDate;

	public EventAssignment() {}

	public EventAssignment(Long eventId, Long auditoriumId, LocalDateTime airDate) {
		this.eventId = eventId;
		this.auditoriumId = auditoriumId;
		this.airDate = airDate;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getAuditoriumId() {
		return auditoriumId;
	}

	public void setAuditoriumId(Long auditoriumId) {
		this.auditoriumId = auditoriumId;
	}

	public LocalDateTime getAirDate() {
		return airDate;
	}

	public void setAirDate(LocalDateTime airDate) {
		this.airDate = airDate;
	}

	@Override
	public int compareTo(EventAssignment other) {
		return this.airDate.compareTo(other.getAirDate());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((airDate == null) ? 0 : airDate.hashCode());
		result = prime * result + ((auditoriumId == null) ? 0 : auditoriumId.hashCode());
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventAssignment other = (EventAssignment) obj;
		if (airDate == null) {
			if (other.airDate != null)
				return false;
		} else if (!airDate.equals(other.airDate))
			return false;
		if (auditoriumId == null) {
			if (other.auditoriumId != null)
				return false;
		} else if (!auditoriumId.equals(other.auditoriumId))
			return false;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EventAssignment [id=" + super.getId() + ", eventId=" + eventId + ", auditoriumId=" + auditoriumId + ", airDate=" + airDate + "]";
	}

}
