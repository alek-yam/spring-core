package ru.epam.spring.cinema.aspect;

import java.util.Collection;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.BookingReport;
import ru.epam.spring.cinema.repository.DiscountStatisticRepository;
import ru.epam.spring.cinema.statistic.DiscountStatistic;

@Aspect
@Component
public class DiscountAspect {

	private DiscountStatisticRepository statisticRepository;

	@Autowired
	public void setStatisticRepository(DiscountStatisticRepository statisticRepository) {
		this.statisticRepository = statisticRepository;
	}

	public Collection<DiscountStatistic> getStatistics() {
	    return statisticRepository.getAll();
    }

	@Pointcut("execution(* ru.epam.spring.cinema.service.BookingService.bookTickets(..))")
	private void bookTickets() {}

    @AfterReturning(pointcut="bookTickets()", returning="retVal")
	public void afterReturningBookTickets(Object retVal) {
    	BookingReport booking = (BookingReport) retVal;
    	if (booking.getPriceReport().getDiscountReport().getPercent() > 0) {
    		String strategyId = booking.getPriceReport().getDiscountReport().getStrategyId();
    		DiscountStatistic discStat = getStatistic(strategyId);
    		discStat.incrementWasGivenCount(booking.getUser().getId());
    		statisticRepository.save(discStat);
    	}
	}

    private DiscountStatistic getStatistic(String strategyId) {
    	DiscountStatistic discStat = statisticRepository.getByStrategyId(strategyId);

    	if (discStat == null) {
    		discStat = new DiscountStatistic(strategyId);
    	}

    	return discStat;
    }
}
