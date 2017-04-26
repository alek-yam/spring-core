package ru.epam.spring.cinema.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * The auditorium definition.
 *
 * @author Alex_Yamskov
 */
public class Auditorium extends DomainObject {
    private String name;
    private Map<Long, Seat> seats;

    public Auditorium() {}

    public Auditorium(long id, String name, long numberOfSeats, Set<Long> vipSeats) {
    	super(id);
    	this.name = name;
    	this.seats = createSeats(id, numberOfSeats, vipSeats);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Long, Seat> getSeats() {
        return seats;
    }

    public void setSeats(Map<Long, Seat> seats) {
        this.seats = seats;
    }

    public long getCapacity() {
        return seats.size();
    }

    public Set<Long> getAllSeatNumbers() {
    	return seats.keySet();
    }

    public Set<Long> getVipSeatNumbers() {
        return seats.values().stream()
        		.filter(s-> s.isVip())
        		.mapToLong(s-> s.getNumber())
        		.boxed().collect(Collectors.toSet());
    }

/*
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
*/

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

	@Override
	public String toString() {
		return "Auditorium [id=" + getId()
			+ ", name=" + name
			+ ", seats=" + Arrays.toString(seats.values().toArray()) + "]";
	}

	public static Map<Long, Seat> createSeats(Long auditoriumId, long numberOfSeats, Set<Long> vipSeats) {
    	Map<Long, Seat> seats = new TreeMap<Long, Seat>();

    	for (long i = 0; i < numberOfSeats; i++) {
    		Long number = Long.valueOf(i + 1);
    		seats.put(number, new Seat(auditoriumId, number, vipSeats.contains(i)));
    	}

    	return seats;
    }
}
