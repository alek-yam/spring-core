package ru.epam.spring.cinema.repository.simple;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.Ticket;
import ru.epam.spring.cinema.repository.TicketRepository;
import ru.epam.spring.cinema.repository.filter.TicketFilter;

/**
 * The Class SimpleTicketRepository.
 *
 * @author Alex_Yamskov
 */
@Component
public class SimpleTicketRepository extends AbstractSimpleRepository<Ticket>
	implements TicketRepository {

	@Override
    public @Nonnull
    Collection<Ticket> getByFilter(@Nonnull TicketFilter filter) {
		Collection<Ticket> foundTickets = new ArrayList<Ticket>();

	    for (Ticket t : this.getAll()) {
	    	if (filter.getEvent() != null && !filter.getEvent().equals(t.getEventId())) {
	    		continue;
	    	}

	    	if (filter.getDate() != null && !filter.getDate().equals(t.getDate())) {
	    		continue;
	    	}

	    	if (filter.getUser() != null && !filter.getUser().equals(t.getUserId())) {
	    		continue;
	    	}

	    	foundTickets.add(t);
	    }

	    return foundTickets;
    }

}
