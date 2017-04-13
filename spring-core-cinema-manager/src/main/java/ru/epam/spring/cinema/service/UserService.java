package ru.epam.spring.cinema.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.epam.spring.cinema.domain.User;


/**
 * Manages registered users.
 *
 * @author Alex_Yamskov
 */
public interface UserService extends AbstractService<User> {

    /**
     * Finding user by email
     *
     * @param email
     *            Email of the user
     * @return found user or <code>null</code>
     */
    public @Nullable User getByEmail(@Nonnull String email);

}
