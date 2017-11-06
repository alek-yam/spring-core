package ru.epam.spring.cinema.repository.jdbc;

import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.stereotype.Repository;

import ru.epam.spring.cinema.domain.Auditorium;
import ru.epam.spring.cinema.domain.Seat;
import ru.epam.spring.cinema.repository.AuditoriumRepository;
import ru.epam.spring.cinema.repository.exception.RepositoryException;

/**
 * The Class JdbcAuditoriumRepository.
 *
 * @author Alex_Yamskov
 */
@Repository("jdbcAuditoriumRepository")
public class JdbcAuditoriumRepository implements AuditoriumRepository {

	private Map<Long, Auditorium> auditoriums = new HashMap<Long, Auditorium>();
	private JdbcTemplate template;
	private InsertSeat insertSeat;

	@Autowired
	public JdbcAuditoriumRepository(Collection<Auditorium> auditoriums) {
		for (Auditorium a : auditoriums) {
			this.auditoriums.put(a.getId(), a);
		}
	}

	@Autowired
    public void setDataSource(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
		this.insertSeat = new InsertSeat(dataSource);
    }

	@PostConstruct
	private void init() {
		for (Auditorium a : auditoriums.values()) {

			template.update("INSERT INTO auditoriums (id, name) VALUES (?, ?)",
					a.getId(), a.getName());

			for (Seat s : a.getSeats().values()) {
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("audId", s.getAuditoriumId());
				paramMap.put("num", s.getNumber());
				paramMap.put("vip", s.isVip());
				insertSeat.updateByNamedParam(paramMap);
			}
		}

		insertSeat.flush();
	}

	@Override
	public @Nonnull
	Collection<Auditorium> getAll() {
		return auditoriums.values();
	}

	@Override
    public Auditorium getById(@Nonnull Long id) {
	    return auditoriums.get(id);
    }

	@Override
    public Auditorium getByName(@Nonnull String name) {
		Optional<Auditorium> auditorium = auditoriums.values().stream().filter(a-> a.getName().equals(name)).findFirst();
		return auditorium.orElseThrow(()-> new RepositoryException("Auditorium with specified name [" + name + "] was not found"));
    }

	private static final class InsertSeat extends BatchSqlUpdate {
		private static final String SQL_INCERT_SEAT =
				"INSERT INTO seats (auditoriumId, number, vip) " +
				"VALUES (:audId, :num, :vip)";

		private static final int BATCH_SIZE = 10;

		public InsertSeat(DataSource dataSource) {
			super(dataSource, SQL_INCERT_SEAT);
			declareParameter(new SqlParameter("audId", Types.INTEGER));
			declareParameter(new SqlParameter("num", Types.INTEGER));
			declareParameter(new SqlParameter("vip", Types.BOOLEAN));
			setBatchSize(BATCH_SIZE);
		}
	}
}
