package ru.epam.spring.cinema.aspect;

import java.util.Collection;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.Event;
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

	@Pointcut("execution(* ru.epam.spring.cinema.service.BookingService.getTicketsPrice(..))")
	private void getTicketsPrice() {}

	@Pointcut("execution(* ru.epam.spring.cinema.service.BookingService.bookTickets(..))")
	private void bookTickets() {}

    @AfterReturning(pointcut="getEventByName()", returning="retVal")
	public void afterReturningGetEventByName(Object retVal) {
    	if (retVal == null) {
    		return;
    	}

    	Event event = (Event) retVal;
    	EventStatistic eventStat = getStatistic(event.getName());
    	eventStat.incrementAccessedByNameCount();
    	statisticRepository.save(eventStat);
	}

    @AfterReturning(pointcut="getTicketsPrice()")
	public void afterReturningGetTicketsPrice(JoinPoint jp) {
    	Object eventArg = jp.getArgs()[0];

    	if (eventArg == null) {
    		return;
    	}

    	Event event = (Event) eventArg;
    	EventStatistic eventStat = getStatistic(event.getName());
    	eventStat.incrementPriceWereQueriedCount();
    	statisticRepository.save(eventStat);
	}

    @AfterReturning(pointcut="bookTickets()")
	public void afterReturningBookTickets(JoinPoint jp) {
    	Object eventArg = jp.getArgs()[0];

    	if (eventArg == null) {
    		return;
    	}

    	Event event = (Event) eventArg;
    	EventStatistic eventStat = getStatistic(event.getName());
    	eventStat.incrementTicketsWereBookedCount();
    	statisticRepository.save(eventStat);
	}

    private EventStatistic getStatistic(String eventName) {
    	EventStatistic eventStat = statisticRepository.getByEventName(eventName);

    	if (eventStat == null) {
    		eventStat = new EventStatistic(eventName);
    	}

    	return eventStat;
    }
}
