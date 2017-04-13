package ru.epam.spring.cinema.repository;

import java.util.Collection;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.domain.DomainObject;

/**
 * The Interface AbstractRepository.
 *
 * @author Alex_Yamskov
 * @param <T> the generic type
 */
public interface AbstractRepository<T extends DomainObject> {

    public T getById(@Nonnull Long id);


    public @Nonnull Collection<T> getAll();


    public T save(@Nonnull T object);


    public void removeById(@Nonnull Long id);
}
