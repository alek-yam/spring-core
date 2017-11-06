package ru.epam.spring.cinema.domain;

/**
 * The User Account definition.
 *
 * @author Alex_Yamskov
 */
public class UserAccount extends DomainObject {
	private Long userId;
	private Double balance;

	public UserAccount() {
	}

	public UserAccount(long id, long userId, double balance) {
		super(id);
		this.userId = userId;
		this.balance = balance;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "UserAccount [userId=" + userId + ", balance=" + balance + "]";
	}

}
