package ru.epam.spring.cinema.service;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ru.epam.spring.cinema.domain.Auditorium;


/**
 * Returns info about auditoriums and places.
 *
 * @author Alex_Yamskov
 */
public interface AuditoriumService {

    /**
     * Getting all auditoriums from the system
     *
     * @return set of all auditoriums
     */
    public @Nonnull Set<Auditorium> getAll();

    /**
     * Finding auditorium by name
     *
     * @param name
     *            Name of the auditorium
     * @return found auditorium or <code>null</code>
     */
    public @Nullable Auditorium getByName(@Nonnull String name);

}
