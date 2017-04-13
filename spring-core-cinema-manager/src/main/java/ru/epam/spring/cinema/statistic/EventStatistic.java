package ru.epam.spring.cinema.statistic;

public class EventStatistic {

	private final String eventName;
	private long accessedByNameCount = 0;
	private long priceWereQueriedCount = 0;
	private long ticketsWereBookedCount = 0;

	public EventStatistic(String eventName) {
		this.eventName = eventName;
	}

	public String getEventName() {
	    return eventName;
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
	    return "EventStatistics [eventName=" + eventName
	            + ", accessedByNameCount=" + accessedByNameCount
	            + ", priceWereQueriedCount=" + priceWereQueriedCount
	            + ", ticketsWereBookedCount=" + ticketsWereBookedCount + "]";
    }

}
