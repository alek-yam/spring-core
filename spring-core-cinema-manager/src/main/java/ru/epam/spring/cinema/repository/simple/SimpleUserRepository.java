package ru.epam.spring.cinema.repository.simple;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.UserRepository;
import ru.epam.spring.cinema.repository.filter.UserFilter;

/**
 * The Class SimpleUserRepository.
 *
 * @author Alex_Yamskov
 */
@Component
public class SimpleUserRepository extends AbstractSimpleRepository<User>
	implements UserRepository {

	@Override
    public @Nonnull
    Collection<User> getByFilter(UserFilter filter) {
		Collection<User> foundUsers = new ArrayList<User>();

	    for (User u : this.getAll()) {
	    	if (filter.getEmail() != null && !filter.getEmail().isEmpty()
	    			&& filter.getEmail().equals(u.getEmail())) {
	    		foundUsers.add(u);
	    	}
	    }

	    return foundUsers;
    }

}
