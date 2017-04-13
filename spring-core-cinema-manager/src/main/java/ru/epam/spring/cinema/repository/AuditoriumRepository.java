package ru.epam.spring.cinema.repository;

import java.util.Collection;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.domain.Auditorium;

/**
 * The Interface AuditoriumRepository.
 *
 * @author Alex_Yamskov
 */
public interface AuditoriumRepository {

    public @Nonnull Collection<Auditorium> getAll();

    public Auditorium getByName(@Nonnull String name);

}
