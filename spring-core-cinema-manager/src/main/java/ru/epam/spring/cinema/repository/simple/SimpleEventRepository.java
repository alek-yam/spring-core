package ru.epam.spring.cinema.repository.simple;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.repository.EventRepository;
import ru.epam.spring.cinema.repository.filter.EventFilter;

/**
 * The Class SimpleEventRepository.
 *
 * @author Alex_Yamskov
 */
@Component
public class SimpleEventRepository extends AbstractSimpleRepository<Event>
	implements EventRepository {

	@Override
    public @Nonnull
    Collection<Event> getByFilter(@Nonnull EventFilter filter) {
		Collection<Event> foundEvents = new ArrayList<Event>();

	    for (Event e : this.getAll()) {
	    	if (filter.getName() != null && !filter.getName().isEmpty()
	    			&& filter.getName().equals(e.getName())) {
	    		foundEvents.add(e);
	    	}
	    }

	    return foundEvents;
    }

}
