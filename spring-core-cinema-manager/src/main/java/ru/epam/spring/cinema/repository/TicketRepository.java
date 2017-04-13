package ru.epam.spring.cinema.repository;

import java.util.Collection;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.domain.Ticket;
import ru.epam.spring.cinema.repository.filter.TicketFilter;

/**
 * The Interface TicketRepository.
 */
public interface TicketRepository extends AbstractRepository<Ticket> {

    public @Nonnull
    Collection<Ticket> getByFilter(@Nonnull TicketFilter filter);

}
