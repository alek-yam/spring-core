package ru.epam.spring.cinema.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.domain.UserAccount;
import ru.epam.spring.cinema.repository.AccountRepository;

/**
 * The AccountService implementation.
 *
 * @author Alex_Yamskov
 */
@Service
public class AccountServiceImpl implements AccountService {

	private AccountRepository accountRepository;

	@Autowired
	@Qualifier("jdbcAccountRepository")
	public void setAccountRepository(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public double getBalance(User user) {
		UserAccount account = accountRepository.getByUserId(user.getId());

		if (account == null) {
			return 0;
		} else {
			return account.getBalance();
		}
	}

	@Override
	public void putMoney(User user, double sum) {
		if (sum <= 0) {
			throw new IllegalArgumentException("Invalid sum to put: " + sum);
		}

		UserAccount account = accountRepository.getByUserId(user.getId());

		if (account == null) {
			account = new UserAccount();
			account.setUserId(user.getId());
			account.setBalance(sum);
		} else {
			double balance = account.getBalance();
			account.setBalance(balance + sum);
		}

		accountRepository.save(account);
	}

	@Override
	public void withdrawMoney(User user, double sum) {
		if (sum <= 0) {
			throw new IllegalArgumentException("Invalid sum to withdraw: " + sum);
		}

		UserAccount account = accountRepository.getByUserId(user.getId());

		if (account == null) {
			throw new RuntimeException("The account is empty.");
		} else {
			double balance = account.getBalance();
			if (sum > balance) {
				throw new RuntimeException("Not enough money on the account.");
			}
			account.setBalance(balance - sum);
		}

		accountRepository.save(account);
	}

}
