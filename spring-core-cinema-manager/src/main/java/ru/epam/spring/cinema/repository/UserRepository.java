package ru.epam.spring.cinema.repository;

import java.util.Collection;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.filter.UserFilter;

/**
 * The Interface UserRepository.
 *
 * @author Alex_Yamskov
 */
public interface UserRepository extends AbstractRepository<User> {

    public @Nonnull
    Collection<User> getByFilter(@Nonnull UserFilter filter);

}
