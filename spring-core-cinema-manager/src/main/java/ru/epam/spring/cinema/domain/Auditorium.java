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

    public Auditorium(long id, String name, long capacity, Set<Long> vipSeats) {
    	super(id);
    	this.name = name;
    	this.seats = createSeats(id, capacity, vipSeats);
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

    public Set<Long> getSeatNumbers() {
    	return seats.keySet();
    }

    public Set<Long> getVipSeatNumbers() {
        return seats.values().stream()
        		.filter(s-> s.isVip())
        		.mapToLong(s-> s.getNumber())
        		.boxed().collect(Collectors.toSet());
    }

	@Override
	public String toString() {
		return "Auditorium [id=" + getId()
			+ ", name=" + name
			+ ", seats=" + Arrays.toString(seats.values().toArray()) + "]";
	}

	public static Map<Long, Seat> createSeats(long auditoriumId, long capacity, Set<Long> vipSeats) {
    	Map<Long, Seat> seats = new TreeMap<Long, Seat>();

    	for (long i = 0; i < capacity; i++) {
    		long number = i + 1;
    		boolean isVip = vipSeats.contains(number);
    		seats.put(number, new Seat(auditoriumId, number, isVip));
    	}

    	return seats;
    }
}
