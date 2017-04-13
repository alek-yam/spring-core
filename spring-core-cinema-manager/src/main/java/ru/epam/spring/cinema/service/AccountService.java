package ru.epam.spring.cinema.service;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.domain.User;

/**
 * Manages user accounts.
 *
 * @author Alex_Yamskov
 */
public interface AccountService {

	/**
	 * Gets the balance.
	 *
	 * @param user the user
	 * @return the balance
	 */
	double getBalance(@Nonnull User user);

	/**
	 * Put money.
	 *
	 * @param user the user
	 * @param sum the sum
	 */
	void putMoney(@Nonnull User user, double sum);

	/**
	 * Withdraw money.
	 *
	 * @param user the user
	 * @param sum the sum
	 */
	void withdrawMoney(@Nonnull User user, double sum);

}
