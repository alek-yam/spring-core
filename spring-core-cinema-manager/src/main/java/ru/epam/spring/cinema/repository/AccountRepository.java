package ru.epam.spring.cinema.repository;

import java.util.Collection;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.domain.UserAccount;

/**
 * The Interface UserAccountRepository.
 *
 * @author Alex_Yamskov
 */
public interface AccountRepository {

    public UserAccount getByUserId(@Nonnull Long userId);

    public @Nonnull Collection<UserAccount> getAll();

    public UserAccount save(@Nonnull UserAccount userAccount);

    public void removeByUserId(@Nonnull Long userId);

}
