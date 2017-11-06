package ru.epam.spring.cinema.statistic;

public class EventStatistic {

	private Long eventId;
	private long accessedByNameCount = 0;
	private long priceWereQueriedCount = 0;
	private long ticketsWereBookedCount = 0;

	public EventStatistic(Long eventId) {
		this.eventId = eventId;
	}

	public Long getEventId() {
	    return eventId;
    }

	public void setEventId(Long eventId) {
	    this.eventId = eventId;
    }

	public long getAccessedByNameCount() {
		return accessedByNameCount;
	}

	public void setAccessedByNameCount(long accessedByNameCount) {
		this.accessedByNameCount = accessedByNameCount;
	}

	public long getPriceWereQueriedCount() {
		return priceWereQueriedCount;
	}

	public void setPriceWereQueriedCount(long priceWereQueriedCount) {
		this.priceWereQueriedCount = priceWereQueriedCount;
	}

	public long getTicketsWereBookedCount() {
		return ticketsWereBookedCount;
	}

	public void setTicketsWereBookedCount(long ticketsWereBookedCount) {
		this.ticketsWereBookedCount = ticketsWereBookedCount;
	}

	public void incrementAccessedByNameCount() {
		this.accessedByNameCount++;
	}

	public void incrementPriceWereQueriedCount() {
		this.priceWereQueriedCount++;
	}

	public void incrementTicketsWereBookedCount() {
		this.ticketsWereBookedCount++;
	}

	@Override
    public String toString() {
	    return "EventStatistics [eventId=" + eventId
	            + ", accessedByNameCount=" + accessedByNameCount
	            + ", priceWereQueriedCount=" + priceWereQueriedCount
	            + ", ticketsWereBookedCount=" + ticketsWereBookedCount + "]";
    }

}
