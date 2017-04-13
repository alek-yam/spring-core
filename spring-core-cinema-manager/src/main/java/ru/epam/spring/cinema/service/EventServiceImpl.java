package ru.epam.spring.cinema.service;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.repository.EventRepository;
import ru.epam.spring.cinema.repository.filter.EventFilter;

/**
 * The EventService implementation.
 *
 * @author Alex_Yamskov
 */
@Service
public class EventServiceImpl implements EventService {

	private EventRepository eventRepository;

	@Autowired
	@Qualifier("jdbcEventRepository")
	public void setAuditoriumRepository(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@Override
    public Event getById(Long id) {
		return eventRepository.getById(id);
    }

	@Override
    public @Nonnull
    Collection<Event> getAll() {
		return eventRepository.getAll();
    }

	@Override
    public Event save(Event object) {
		return eventRepository.save(object);
    }

	@Override
    public void remove(Event object) {
		if (object.getId() == null) {
			throw new IllegalArgumentException("The object to remove doesn't contain ID.");
		}

		eventRepository.removeById(object.getId());
    }

	@Override
    public @Nullable
    Event getByName(@Nonnull String name) {
		EventFilter filter = new EventFilter();
		filter.setName(name);

		Collection<Event> foundEvents = eventRepository.getByFilter(filter);

		if (foundEvents.isEmpty()) {
			return null;
		}

		if (foundEvents.size() > 1) {
			throw new RuntimeException("More than one events found with name: \"" + name + "\".");
		}

		return foundEvents.iterator().next();
    }

}
