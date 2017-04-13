package ru.epam.spring.cinema.service;

import java.util.Collection;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.domain.DomainObject;


/**
 * The base DAO interface for domain objects.
 *
 * @author Alex_Yamskov
 *
 * @param <T>
 *            DomainObject subclass
 */
public interface AbstractService<T extends DomainObject> {

    /**
     * Getting object by id from storage
     *
     * @param id
     *            id of the object
     * @return Found object or <code>null</code>
     */
    public T getById(@Nonnull Long id);

    /**
     * Getting all objects from storage
     *
     * @return collection of objects
     */
    public @Nonnull Collection<T> getAll();

    /**
     * Saving new object to storage or updating existing one
     *
     * @param object
     *            Object to save
     * @return saved object with assigned id
     */
    public T save(@Nonnull T object);

    /**
     * Removing object from storage
     *
     * @param object
     *            Object to remove
     */
    public void remove(@Nonnull T object);
}
