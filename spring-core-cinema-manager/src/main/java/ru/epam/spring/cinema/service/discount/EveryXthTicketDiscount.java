package ru.epam.spring.cinema.service.discount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.DiscountReport;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.domain.User;

/**
 * The class to calculate total discount based on discount for every x-th ticket.
 *
 * @author Alex_Yamskov
 */
@Component
public class EveryXthTicketDiscount implements DiscountStrategy {

	private final long xthTicketNumber;
	private final byte xthTicketDiscount;

	@Autowired
	public EveryXthTicketDiscount(@Value("10") long xthTicketNumber, @Value("50") byte xthTicketDiscount) {
		this.xthTicketNumber = xthTicketNumber;
		this.xthTicketDiscount = xthTicketDiscount;
	}

	@Override
    public DiscountReport getDiscount(User user, EventAssignment assignment, long numberOfTickets) {
		long accumulatedDiscount = 0;

		for (int i = 1; i <= numberOfTickets; i++) {
			if (isHappyTicket(i)) {
				accumulatedDiscount += xthTicketDiscount;
			}
		}

	    byte discountPercent = (byte) (accumulatedDiscount / numberOfTickets);
	    return new DiscountReport(EveryXthTicketDiscount.class.getSimpleName(), discountPercent);
    }

	private boolean isHappyTicket(long ticketNumber) {
		long x = ticketNumber / xthTicketNumber;
		return (x * xthTicketNumber) == ticketNumber;
	}
}
