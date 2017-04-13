package ru.epam.spring.cinema.repository.simple;

import org.springframework.stereotype.Component;

import ru.epam.spring.cinema.domain.BookingReport;
import ru.epam.spring.cinema.repository.BookingRepository;

/**
 * The Class SimpleBookingRepository.
 *
 * @author Alex_Yamskov
 */
@Component
public class SimpleBookingRepository extends AbstractSimpleRepository<BookingReport>
        implements BookingRepository {

}
