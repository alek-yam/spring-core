package ru.epam.spring.cinema.domain;

/**
 * The User Account definition.
 *
 * @author Alex_Yamskov
 */
public class UserAccount {
	private final Long userId;
	private double balance;

	public UserAccount(Long userId) {
		this.userId = userId;
		this.balance = 0;
	}

	public UserAccount(Long userId, double balance) {
		this.userId = userId;
		this.balance = balance;
	}

	public Long getUserId() {
		return userId;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

}
