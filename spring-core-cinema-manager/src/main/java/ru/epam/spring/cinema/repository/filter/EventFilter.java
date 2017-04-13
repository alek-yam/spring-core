package ru.epam.spring.cinema.repository.filter;

/**
 * Filter to perform searching for events with specific parameters.
 *
 * @author Alex_Yamskov
 */
public class EventFilter {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
