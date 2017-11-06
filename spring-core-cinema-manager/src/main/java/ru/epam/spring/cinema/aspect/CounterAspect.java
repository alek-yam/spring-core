package ru.epam.spring.cinema.aspect;

import java.util.Collection;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.repository.EventStatisticRepository;
import ru.epam.spring.cinema.statistic.EventStatistic;

@Aspect
@Component
public class CounterAspect {

	private EventStatisticRepository statisticRepository;

	@Autowired
	public void setStatisticRepository(EventStatisticRepository statisticRepository) {
		this.statisticRepository = statisticRepository;
	}

	public Collection<EventStatistic> getStatistics() {
	    return statisticRepository.getAll();
    }

	@Pointcut("execution(* ru.epam.spring.cinema.service.EventService.getByName(..))")
	private void getEventByName() {}

	@Pointcut("execution(* ru.epam.spring.cinema.service.BookingService.getFinalPrice(..))")
	private void getFinalPrice() {}

	@Pointcut("execution(* ru.epam.spring.cinema.service.BookingService.bookTickets(..))")
	private void bookTickets() {}

    @AfterReturning(pointcut="getEventByName()", returning="retVal")
	public void afterReturningGetEventByName(Object retVal) {
    	if (retVal == null) {
    		return;
    	}

    	Event event = (Event) retVal;
    	EventStatistic eventStat = getStatistic(event.getId());
    	eventStat.incrementAccessedByNameCount();
    	statisticRepository.save(eventStat);
	}

    @AfterReturning(pointcut="getFinalPrice()")
	public void afterReturningGetFinalPrice(JoinPoint jp) {
    	Object assignmentArg = jp.getArgs()[0];

    	if (assignmentArg == null) {
    		return;
    	}

    	EventAssignment assignment = (EventAssignment) assignmentArg;
    	EventStatistic eventStat = getStatistic(assignment.getEventId());
    	eventStat.incrementPriceWereQueriedCount();
    	statisticRepository.save(eventStat);
	}

    @AfterReturning(pointcut="bookTickets()")
	public void afterReturningBookTickets(JoinPoint jp) {
    	Object assignmentArg = jp.getArgs()[0];

    	if (assignmentArg == null) {
    		return;
    	}

    	EventAssignment assignment = (EventAssignment) assignmentArg;
    	EventStatistic eventStat = getStatistic(assignment.getEventId());
    	eventStat.incrementTicketsWereBookedCount();
    	statisticRepository.save(eventStat);
	}

    private EventStatistic getStatistic(Long eventId) {
    	EventStatistic eventStat = statisticRepository.getByEventId(eventId);

    	if (eventStat == null) {
    		eventStat = new EventStatistic(eventId);
    	}

    	return eventStat;
    }
}
