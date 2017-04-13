package ru.epam.spring.cinema.domain;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The auditorium definition.
 *
 * @author Alex_Yamskov
 */
public class Auditorium {
    private final String name;
    private final Map<Long, Seat> seats;

    public Auditorium(String name, long numberOfSeats, Set<Long> vipSeats) {
    	this.name = name;
    	this.seats = createSeats(numberOfSeats, vipSeats);
    }

    public String getName() {
        return name;
    }

    public Map<Long, Seat> getSeats() {
        return seats;
    }

    public long getNumberOfSeats() {
        return seats.size();
    }

    public Set<Long> getAllSeatNumbers() {
    	return seats.keySet();
    }

    public Set<Long> getVipSeatNumbers() {
    	Set<Long> vipSeats = new TreeSet<Long>();

    	for (Seat s : seats.values()) {
    		if (s.isVip()) {
    			vipSeats.add(s.getNumber());
    		}
    	}

        return vipSeats;
    }

    /**
     * Counts how many VIP seats are there in supplied <code>seats</code>
     *
     * @param seats
     *            Seats to process
     * @return number of VIP seats in request
     */
    public long countVipSeats(Collection<Long> seatNumbers) {
    	long count = 0;

    	for (Long sn : seatNumbers) {
    		Seat seat = seats.get(sn);
    		if (seat != null && seat.isVip()) {
    			count++;
    		}
    	}

    	return count;
    }

    private static Map<Long, Seat> createSeats(long numberOfSeats, Set<Long> vipSeats) {
    	Map<Long, Seat> seats = new TreeMap<Long, Seat>();

    	for (long i = 0; i < numberOfSeats; i++) {
    		long number = i + 1;
    		seats.put(Long.valueOf(number), new Seat(number, vipSeats.contains(i)));
    	}

    	return seats;
    }

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
	    Auditorium other = (Auditorium) obj;
	    if (name == null) {
		    if (other.name != null)
			    return false;
	    } else if (!name.equals(other.name))
		    return false;
	    return true;
    }
}
