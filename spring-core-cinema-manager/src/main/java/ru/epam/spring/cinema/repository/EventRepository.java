package ru.epam.spring.cinema.repository;

import java.util.Collection;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.repository.filter.EventFilter;

/**
 * The Interface EventRepository.
 *
 * @author Alex_Yamskov
 */
public interface EventRepository extends AbstractRepository<Event> {

    public @Nonnull
    Collection<Event> getByFilter(@Nonnull EventFilter filter);

}
