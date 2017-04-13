package ru.epam.spring.cinema.repository.simple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.repository.AuditoriumRepository;

/**
 * The Class SimpleAuditoriumRepository.
 *
 * @author Alex_Yamskov
 */
@Component
public class SimpleAuditoriumRepository implements AuditoriumRepository {

	private final Map<String, Auditorium> auditoriums = new HashMap<String, Auditorium>();

	@Autowired
	public SimpleAuditoriumRepository(Collection<Auditorium> auditoriums) {
		for (Auditorium a : auditoriums) {
			this.auditoriums.put(a.getName(), a);
		}
	}

	@Override
	public @Nonnull
	Collection<Auditorium> getAll() {
		return auditoriums.values();
	}

	@Override
    public Auditorium getByName(@Nonnull String name) {
	    return auditoriums.get(name);
    }

}
