package ru.epam.spring.cinema.service;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.repository.AuditoriumRepository;

/**
 * The AuditoriumService implementation.
 *
 * @author Alex_Yamskov
 */
@Service
public class AuditoriumServiceImpl implements AuditoriumService {

	private AuditoriumRepository auditoriumRepository;

	@Autowired
	@Qualifier("jdbcAuditoriumRepository")
	public void setAuditoriumRepository(AuditoriumRepository auditoriumRepository) {
		this.auditoriumRepository = auditoriumRepository;
	}

	@Override
	public @Nonnull
	Set<Auditorium> getAll() {
		return new HashSet<Auditorium>(auditoriumRepository.getAll());
	}

	@Override
	public @Nullable
	Auditorium getById(@Nonnull Long id) {
		return auditoriumRepository.getById(id);
	}

	@Override
	public @Nullable
	Auditorium getByName(@Nonnull String name) {
		return auditoriumRepository.getByName(name);
	}

}
