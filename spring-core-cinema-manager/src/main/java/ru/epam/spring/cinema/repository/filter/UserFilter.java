package ru.epam.spring.cinema.repository.filter;

/**
 * Filter to perform searching for users with specific parameters.
 *
 * @author Alex_Yamskov
 */
public class UserFilter {

	private String email;

	public String getEmail() {
	    return email;
    }

	public void setEmail(String email) {
	    this.email = email;
    }

}
