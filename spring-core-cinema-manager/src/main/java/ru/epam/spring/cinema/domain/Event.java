package ru.epam.spring.cinema.domain;

import java.util.Calendar;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * The event definition.
 *
 * @author Alex_Yamskov
 */
public class Event extends DomainObject {
    private String name;
    private EventRating rating;
    private double basePrice;
    private NavigableSet<Calendar> airDates = new TreeSet<>();
    private NavigableMap<Calendar, String> auditoriums = new TreeMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventRating getRating() {
        return rating;
    }

    public void setRating(EventRating rating) {
        this.rating = rating;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public NavigableSet<Calendar> getAirDates() {
        return airDates;
    }

    public void setAirDates(NavigableSet<Calendar> airDates) {
        this.airDates = airDates;
    }

    public NavigableMap<Calendar, String> getAuditoriums() {
        return auditoriums;
    }

    public void setAuditoriums(NavigableMap<Calendar, String> auditoriums) {
        this.auditoriums = auditoriums;
    }

    /**
     * Checks if event is aired on particular <code>dateTime</code> and assigns
     * auditorium to it.
     *
     * @param date
     *            Date and time of aired event for which to assign
     * @param auditorium
     *            Auditorium that should be assigned
     * @return <code>true</code> if successful, <code>false</code> if event is
     *         not aired on that date
     */
    public boolean assignAuditorium(Calendar date, Auditorium auditorium) {
        if (airDates.contains(date)) {
            auditoriums.put(date, auditorium.getName());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes auditorium assignment from event
     *
     * @param dateTime
     *            Date and time to remove auditorium for
     * @return <code>true</code> if successful, <code>false</code> if not
     *         removed
     */
    public boolean removeAuditoriumAssignment(Calendar dateTime) {
        return auditoriums.remove(dateTime) != null;
    }

    /**
     * Add date and time of event air
     *
     * @param date
     *            Date and time to add
     * @return <code>true</code> if successful, <code>false</code> if already
     *         there
     */
    public boolean addAirDateTime(Calendar date) {
        return airDates.add(date);
    }

    /**
     * Adding date and time of event air and assigning auditorium to that
     *
     * @param date
     *            Date and time to add
     * @param auditorium
     *            Auditorium to add if success in date time add
     * @return <code>true</code> if successful, <code>false</code> if already
     *         there
     */
    public boolean addAirDateTime(Calendar date, Auditorium auditorium) {
        boolean result = airDates.add(date);
        if (result) {
            auditoriums.put(date, auditorium.getName());
        }
        return result;
    }

    /**
     * Removes the date and time of event air. If auditorium was assigned to
     * that date and time - the assignment is also removed
     *
     * @param date
     *            Date and time to remove
     * @return <code>true</code> if successful, <code>false</code> if not there
     */
    public boolean removeAirDateTime(Calendar date) {
        boolean result = airDates.remove(date);
        if (result) {
            auditoriums.remove(date);
        }
        return result;
    }

    /**
     * Checks if event airs on particular date and time
     *
     * @param date
     *            Date and time to check
     * @return <code>true</code> event airs on that date and time
     */
    public boolean airsOnDateTime(Calendar date) {
        //return airDates.stream().anyMatch(dt -> dt.equals(dateTime));
    	return airDates.contains(date);
    }

    /**
     * Checks if event airs on particular date
     *
     * @param date
     *            Date to check
     * @return <code>true</code> event airs on that date
     */
    public boolean airsOnDate(Calendar date) {
        //return airDates.stream().anyMatch(dt -> dt.toLocalDate().equals(date));
    	for (Calendar d : airDates) {
    		if (d.get(Calendar.YEAR) == date.get(Calendar.YEAR)
    				&& d.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)) {
    			return true;
    		}
    	}

    	return false;
    }

    /**
     * Checking if event airs on dates between <code>from</code> and
     * <code>to</code> inclusive
     *
     * @param from
     *            Start date to check
     * @param to
     *            End date to check
     * @return <code>true</code> event airs on dates
     */
    public boolean airsOnDates(Calendar from, Calendar to) {
        //return airDates.stream().anyMatch(dt -> dt.toLocalDate().compareTo(from) >= 0 && dt.toLocalDate().compareTo(to) <= 0);
    	throw new RuntimeException("Not implemented.");
    }
}
