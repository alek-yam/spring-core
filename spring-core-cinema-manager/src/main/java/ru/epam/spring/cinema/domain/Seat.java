package ru.epam.spring.cinema.domain;

/**
 * The auditorium seat definition.
 *
 * @author Alex_Yamskov
 */
public class Seat implements Comparable<Seat> {
	private final long number;
	private final boolean vip;

	public Seat(long number, boolean vip) {
		this.number = number;
		this.vip = vip;
	}

	public long getNumber() {
		return number;
	}

	public boolean isVip() {
	    return vip;
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + (int) (number ^ (number >>> 32));
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
	    if (number != other.number)
		    return false;
	    return true;
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
	public String toString() {
		return "Seat [number=" + number + ", vip=" + vip + "]";
	}
}
