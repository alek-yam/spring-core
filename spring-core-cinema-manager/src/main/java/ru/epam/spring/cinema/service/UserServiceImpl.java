package ru.epam.spring.cinema.service;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.UserRepository;
import ru.epam.spring.cinema.repository.filter.UserFilter;

/**
 * The UserService implementation.
 *
 * @author Alex_Yamskov
 */
@Service
public class UserServiceImpl implements UserService {

	private UserRepository userRepository;

	@Autowired
	@Qualifier("jdbcUserRepository")
	public void setAuditoriumRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User getById(Long id) {
		return userRepository.getById(id);
	}

	@Override
	public @Nonnull
	Collection<User> getAll() {
		return userRepository.getAll();
	}

	@Override
	public User save(User object) {
		return userRepository.save(object);
	}

	@Override
	public void remove(User object) {
		if (object.getId() == null) {
			throw new IllegalArgumentException("The object to remove doesn't contain ID.");
		}

		userRepository.removeById(object.getId());
	}

	@Override
	public @Nullable
	User getByEmail(@Nonnull String email) {
		UserFilter filter = new UserFilter();
		filter.setEmail(email);

		Collection<User> foundUsers = userRepository.getByFilter(filter);

		if (foundUsers.isEmpty()) {
			return null;
		}

		if (foundUsers.size() > 1) {
			throw new RuntimeException("More than one users found with email address: \"" + email + "\".");
		}

		return foundUsers.iterator().next();
	}

}
