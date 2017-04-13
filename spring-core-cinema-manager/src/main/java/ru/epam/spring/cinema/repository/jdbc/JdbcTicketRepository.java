package ru.epam.spring.cinema.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.Ticket;
import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.TicketRepository;
import ru.epam.spring.cinema.repository.exception.RepositoryException;
import ru.epam.spring.cinema.repository.filter.TicketFilter;

/**
 * The Class JdbcTicketRepository.
 *
 * @author Alex_Yamskov
 */
@Repository("jdbcTicketRepository")
public class JdbcTicketRepository implements TicketRepository {

	private JdbcTemplate template;
	private UpdateTicket updateTicket;
	private InsertTicket insertTicket;

	@Autowired
    public void setDataSource(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
		this.updateTicket = new UpdateTicket(dataSource);
		this.insertTicket = new InsertTicket(dataSource);
    }

	@Override
    public Ticket getById(@Nonnull Long id) {
		/* In JdbcTemplate , queryForInt, queryForLong, queryForObject all such methods expects that
		 * executed query will return one and only one row. If you get no rows or more than one row
		 * that will result in IncorrectResultSizeDataAccessException.
		 * Now the correct way is not to catch this exception or EmptyResultDataAccessException,
		 * but make sure the query you are using should return only one row
		 *  If at all it is not possible then use query method instead.

        String sql = "SELECT * FROM tickets WHERE id = ?";
        Ticket ticket = template.queryForObject(sql, new Object[]{id}, new TicketRowMapper());

	    return ticket;
	    */

		String sql = "SELECT * FROM tickets WHERE id = ?";
        List<Ticket> tickets = template.query(sql, new Object[]{id}, new TicketRowMapper());

        if (tickets.size() == 0) {
        	return null;
        }

        if (tickets.size() == 1) {
        	return tickets.get(0);
        }

        throw new RepositoryException("Cannot get ticket by ID [" + id + "]: more than one found.");
    }

	@Override
    public @Nonnull
    Collection<Ticket> getAll() {
		String sql = "SELECT * FROM tickets";
        List<Ticket> tickets = template.query(sql, new TicketRowMapper());
	    return tickets;
    }

	@Override
    public Ticket save(@Nonnull Ticket object) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", object.getUserId());
		paramMap.put("eventId", object.getEventId());
		Timestamp airDate = new Timestamp(object.getDate().getTime().getTime());
		paramMap.put("airDate", airDate);
		paramMap.put("seatNum", object.getSeat());

	    if (object.getId() == null) {
	    	KeyHolder keyHolder = new GeneratedKeyHolder();
	    	insertTicket.updateByNamedParam(paramMap, keyHolder);
	    	object.setId(keyHolder.getKey().longValue());
	    } else {
	    	paramMap.put("id", object.getId());
	    	int updatedRows = updateTicket.updateByNamedParam(paramMap);
	    	if (updatedRows == 0) {
	    		throw new RepositoryException("Cannot update ticket with ID [" + object.getId() + "]: not found.");
	    	}
	    }

	    return object;
    }

	@Override
    public void removeById(@Nonnull Long id) {
		String sql = "delete from tickets where id = ?";
		int updatedRows = template.update(sql, id);
    	if (updatedRows == 0) {
    		throw new RepositoryException("Cannot remove ticket with ID [" + id + "]: not found.");
    	}
    }

	@Override
    public @Nonnull
    Collection<Ticket> getByFilter(@Nonnull TicketFilter filter) {
		Map<String, Object> criterias = new HashMap<>();

		User user = filter.getUser();
    	if (user != null && user.getId() != null) {
    		criterias.put("userId", user.getId());
    	}

		Event event = filter.getEvent();
    	if (event != null && event.getId() != null ) {
    		criterias.put("eventId", event.getId());
    	}

    	Calendar date = filter.getDate();
    	if (date != null) {
    		Timestamp timestamp = new Timestamp(date.getTime().getTime());
    		criterias.put("airDate", timestamp);
    	}

    	String sql = "SELECT * FROM tickets";
    	if (criterias.size() > 0) {
    		String whereClause = generateWhereClause(criterias.keySet());
    		sql += whereClause;
    	}

    	Object[] args = criterias.values().toArray(new Object[criterias.values().size()]);
        List<Ticket> tickets = template.query(sql, args, new TicketRowMapper());

	    return tickets;
    }

	private String generateWhereClause(Set<String> criteriaNames) {
		StringBuilder sb = new StringBuilder(" WHERE");
		boolean firstCriteria = true;

		for (String c : criteriaNames) {
			if (firstCriteria) {
				sb.append(" ");
				firstCriteria = false;
			} else {
				sb.append(" AND ");
			}

			sb.append(c);
			sb.append(" = ?");
		}

		return sb.toString();
	}

	private static final class TicketRowMapper implements RowMapper<Ticket> {

		@Override
        public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long id = rs.getLong("id");
			Long userId = rs.getLong("userId");
			Long eventId = rs.getLong("eventId");
			Long seatNum = rs.getLong("seatNum");
			Timestamp timestamp = rs.getTimestamp("airDate");

            Calendar airDate = new GregorianCalendar();
            airDate.setTimeInMillis(timestamp.getTime());

            if (userId == 0) {
            	userId = null;
            }

            return new Ticket(id, userId, eventId, airDate, seatNum);
        }

	}

	private static final class UpdateTicket extends SqlUpdate {
		private static final String SQL_UPDATE_TICKET =
				"UPDATE tickets SET " +
				"userId=:userId, " +
				"eventId=:eventId, " +
				"airDate=:airDate, " +
				"seatNum=:seatNum " +
				"WHERE id=:id";

		public UpdateTicket(DataSource dataSource) {
			super(dataSource, SQL_UPDATE_TICKET);
			declareParameter(new SqlParameter("id", Types.INTEGER));
			declareParameter(new SqlParameter("userId", Types.INTEGER));
			declareParameter(new SqlParameter("eventId", Types.INTEGER));
			declareParameter(new SqlParameter("airDate", Types.TIMESTAMP));
			declareParameter(new SqlParameter("seatNum", Types.INTEGER));
		}
	}

	private static final class InsertTicket extends SqlUpdate {
		private static final String SQL_INSERT_TICKET =
				"INSERT INTO tickets (userId, eventId, airDate, seatNum) " +
				"VALUES (:userId, :eventId, :airDate, :seatNum)";

		public InsertTicket(DataSource dataSource) {
			super(dataSource, SQL_INSERT_TICKET);
			declareParameter(new SqlParameter("userId", Types.INTEGER));
			declareParameter(new SqlParameter("eventId", Types.INTEGER));
			declareParameter(new SqlParameter("airDate", Types.TIMESTAMP));
			declareParameter(new SqlParameter("seatNum", Types.INTEGER));
			setGeneratedKeysColumnNames(new String[]{"id"});
			setReturnGeneratedKeys(true);
		}
	}
}
