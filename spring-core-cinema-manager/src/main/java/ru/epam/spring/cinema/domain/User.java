package ru.epam.spring.cinema.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * The user definition.
 *
 * @author Alex_Yamskov
 */
public class User extends DomainObject {

	@NotNull
    private String firstName;

	@NotNull
    private String lastName;

	@DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthday;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Set<String> roles = new HashSet<String>();

    @NotNull
    private Set<Long> ticketIds = new TreeSet<Long>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

	public LocalDate getBirthday() {
	    return birthday;
    }

	public void setBirthday(LocalDate birthday) {
	    this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

    public Set<Long> getTickets() {
        return ticketIds;
    }

    public void setTickets(Set<Long> ticketIds) {
        this.ticketIds = ticketIds;
    }

    public String getFullName() {
    	return this.firstName + " " + this.lastName;
    }

	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName + ", birthday=" + birthday + ", email=" + email
				+ ", roles=" + roles + "]";
	}

}
