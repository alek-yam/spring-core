package ru.epam.spring.cinema.domain;

/**
 * The auditorium seat definition.
 *
 * @author Alex_Yamskov
 */
public class Seat implements Comparable<Seat> {
	private final Long auditoriumId;
	private final Long number;
	private final Boolean vip;

	public Seat(Long auditoriumId, Long number, Boolean vip) {
		this.auditoriumId = auditoriumId;
		this.number = number;
		this.vip = vip;
	}

	public Long getAuditoriumId() {
		return auditoriumId;
	}

	public Long getNumber() {
		return number;
	}

	public Boolean isVip() {
	    return vip;
    }

	@Override
    public int compareTo(Seat other) {

		if (other == null || this.number > other.number) {
			return 1;
		}

		if (this.number < other.number) {
			return -1;
		}

	    return 0;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((auditoriumId == null) ? 0 : auditoriumId.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((vip == null) ? 0 : vip.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Seat other = (Seat) obj;
		if (auditoriumId == null) {
			if (other.auditoriumId != null)
				return false;
		} else if (!auditoriumId.equals(other.auditoriumId))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (vip == null) {
			if (other.vip != null)
				return false;
		} else if (!vip.equals(other.vip))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Seat [auditoriumId=" + auditoriumId + ", number=" + number + ", vip=" + vip + "]";
	}


}
