package ru.epam.spring.cinema.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.stereotype.Repository;

import ru.epam.spring.cinema.repository.EventStatisticRepository;
import ru.epam.spring.cinema.repository.exception.RepositoryException;
import ru.epam.spring.cinema.statistic.EventStatistic;

/**
 * The Class JdbcEventStatisticRepository.
 *
 * @author Alex_Yamskov
 */
@Repository("jdbcEventStatisticRepository")
public class JdbcEventStatisticRepository implements EventStatisticRepository {

	private JdbcTemplate template;
	private MergeEventStatistic mergeStatistic;

	@Autowired
    public void setDataSource(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
		this.mergeStatistic = new MergeEventStatistic(dataSource);
    }

    @Override
    public EventStatistic getByEventId(@Nonnull Long eventId) {
		String sql = "SELECT * FROM eventStatistic WHERE eventId = ?";
        List<EventStatistic> statistics = template.query(sql, new Object[]{eventId}, new EventStatisticRowMapper());

        if (statistics.size() == 0) {
        	return null;
        }

        if (statistics.size() == 1) {
        	return statistics.get(0);
        }

        throw new RepositoryException("Cannot get event statistic by event ID [" + eventId + "]: more than one found.");
    }

    @Override
    public @Nonnull Collection<EventStatistic> getAll() {
		String sql = "SELECT * FROM eventStatistic";
        List<EventStatistic> statistics = template.query(sql, new EventStatisticRowMapper());
	    return statistics;
    }

    @Override
    public void save(@Nonnull EventStatistic object) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("eventId", object.getEventId());
		paramMap.put("accessedByNameCount", object.getAccessedByNameCount());
		paramMap.put("priceWereQueriedCount", object.getPriceWereQueriedCount());
		paramMap.put("ticketsWereBookedCount", object.getTicketsWereBookedCount());
    	mergeStatistic.updateByNamedParam(paramMap);
    }

	private static final class EventStatisticRowMapper implements RowMapper<EventStatistic> {

		@Override
        public EventStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long eventId = rs.getLong("eventId");
			Long accessedByNameCount = rs.getLong("accessedByNameCount");
			Long priceWereQueriedCount = rs.getLong("priceWereQueriedCount");
			Long ticketsWereBookedCount = rs.getLong("ticketsWereBookedCount");

			EventStatistic stat = new EventStatistic(eventId);
			stat.setAccessedByNameCount(accessedByNameCount);
			stat.setPriceWereQueriedCount(priceWereQueriedCount);
			stat.setTicketsWereBookedCount(ticketsWereBookedCount);

            return stat;
        }

	}

	private static final class MergeEventStatistic extends SqlUpdate {
		private static final String SQL_MERGE_STATISTIC =
				"MERGE INTO eventStatistic (eventId, accessedByNameCount, priceWereQueriedCount, ticketsWereBookedCount) " +
				"VALUES (:eventId, :accessedByNameCount, :priceWereQueriedCount, :ticketsWereBookedCount)";

		public MergeEventStatistic(DataSource dataSource) {
			super(dataSource, SQL_MERGE_STATISTIC);
			declareParameter(new SqlParameter("eventId", Types.INTEGER));
			declareParameter(new SqlParameter("accessedByNameCount", Types.INTEGER));
			declareParameter(new SqlParameter("priceWereQueriedCount", Types.INTEGER));
			declareParameter(new SqlParameter("ticketsWereBookedCount", Types.INTEGER));
		}
	}
}
