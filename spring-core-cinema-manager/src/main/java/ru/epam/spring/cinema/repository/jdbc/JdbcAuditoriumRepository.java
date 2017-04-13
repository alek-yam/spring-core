package ru.epam.spring.cinema.repository.jdbc;

import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

/**
 * The Class JdbcAuditoriumRepository.
 *
 * @author Alex_Yamskov
 */
@Repository("jdbcAuditoriumRepository")
public class JdbcAuditoriumRepository implements AuditoriumRepository {

	private final Map<String, Auditorium> auditoriums = new HashMap<String, Auditorium>();
	private JdbcTemplate template;
	private InsertSeat insertSeat;

	@Autowired
	public JdbcAuditoriumRepository(Collection<Auditorium> auditoriums) {
		for (Auditorium a : auditoriums) {
			this.auditoriums.put(a.getName(), a);
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

			template.update("INSERT INTO auditoriums (name, capacity) VALUES (?, ?)",
					a.getName(), a.getSeats().size());

			for (Seat s : a.getSeats().values()) {
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("num", s.getNumber());
				paramMap.put("vip", s.isVip());
				paramMap.put("auditoriumName", a.getName());
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
    public Auditorium getByName(@Nonnull String name) {
	    return auditoriums.get(name);
    }

	private static final class InsertSeat extends BatchSqlUpdate {
		private static final String SQL_INCERT_SEAT =
				"INSERT INTO seats (num, vip, auditoriumName) " +
				"VALUES (:num, :vip, :auditoriumName)";

		private static final int BATCH_SIZE = 10;

		public InsertSeat(DataSource dataSource) {
			super(dataSource, SQL_INCERT_SEAT);
			declareParameter(new SqlParameter("num", Types.INTEGER));
			declareParameter(new SqlParameter("vip", Types.BOOLEAN));
			declareParameter(new SqlParameter("auditoriumName", Types.VARCHAR));
			setBatchSize(BATCH_SIZE);
		}
	}
}
