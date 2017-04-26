package ru.epam.spring.cinema.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ru.epam.spring.cinema.domain.Event;
import ru.epam.spring.cinema.domain.EventAssignment;
import ru.epam.spring.cinema.domain.EventRating;
import ru.epam.spring.cinema.repository.EventRepository;
import ru.epam.spring.cinema.repository.exception.RepositoryException;
import ru.epam.spring.cinema.repository.filter.EventFilter;

/**
 * The Class JdbcEventRepository.
 *
 * @author Alex_Yamskov
 */
@Repository("jdbcEventRepository")
public class JdbcEventRepository implements EventRepository {

	private JdbcTemplate template;
	private UpdateEvent updateEvent;
	private InserEvent insertEvent;
	private InsertAssigment insertAssigment;
	private RemoveAssigment removeAssigment;

	@Autowired
    public void setDataSource(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
		this.updateEvent = new UpdateEvent(dataSource);
		this.insertEvent = new InserEvent(dataSource);
		this.insertAssigment = new InsertAssigment(dataSource);
		this.removeAssigment = new RemoveAssigment(dataSource);
    }

	@Override
    public Event getById(@Nonnull Long id) {
        String sql = "SELECT e.id, e.name, e.rate, e.price, a.id AS assignmentId, a.airDate, a.auditoriumId" +
        		" FROM events e LEFT JOIN eventAssignments a ON e.id = a.eventId WHERE e.id = ?";

        List<Event> events = template.query(sql, new Object[]{id}, new EventExtractor());

        if (events.size() == 0) {
        	return null;
        }

        if (events.size() == 1) {
        	return events.get(0);
        }

        throw new RepositoryException("More than one event with ID [" + id + "] were found.");
    }

	@Override
    public @Nonnull
    Collection<Event> getAll() {
        String sql = "SELECT e.id, e.name, e.rate, e.price, a.id AS assignmentId, a.airDate, a.auditoriumId" +
        		" FROM events e LEFT JOIN eventAssignments a ON e.id = a.eventId";
        List<Event> events = template.query(sql, new EventExtractor());
        return events;
    }

	@Override
    public Event save(@Nonnull Event object) {
		Map<String, Object> paramMap = eventToMap(object);
	    if (paramMap.get("id") == null) {
	    	KeyHolder keyHolder = new GeneratedKeyHolder();
	    	insertEvent.updateByNamedParam(paramMap, keyHolder);
	    	object.setId(keyHolder.getKey().longValue());
	    	insertAssigments(object.getId(), object.getAssignments());
	    } else {
	    	int updatedRows = updateEvent.updateByNamedParam(paramMap);
	    	if (updatedRows == 0) {
	    		throw new RepositoryException("Event with ID [" + object.getId() + "] was not found.");
	    	}
	    	updateAssigments(object.getId(), object.getAssignments());
	    }

	    return object;
    }

	@Override
    public void removeById(@Nonnull Long id) {
		String deleteFromEventsSql = "DELETE FROM events WHERE id = ?";
		int updatedRows = template.update(deleteFromEventsSql, id);
    	if (updatedRows == 0) {
    		throw new RepositoryException("Event with ID [" + id + "] was not found.");
    	}

    	// Not required. Event assignments will be removed automatically while removing corresponding event
    	//String deleteFromAssigmentsSql = "DELETE FROM eventAssignments WHERE eventId = ?";
    	//template.update(deleteFromAssigmentsSql, id);
    }

	@Override
    public @Nonnull
    Collection<Event> getByFilter(@Nonnull EventFilter filter) {
		List<Event> events = new ArrayList<>();

        String name = filter.getName();
        if (name != null) {
            String sql = "SELECT e.id, e.name, e.rate, e.price, a.id AS assignmentId, a.airDate, a.auditoriumId" +
            		" FROM events e LEFT JOIN eventAssignments a ON e.id = a.eventId WHERE e.name = ?";

            events = template.query(sql, new Object[]{name}, new EventExtractor());
        }

        return events;
    }

	private void insertAssigments(Long eventId, Set<EventAssignment> assignments) {
		for (EventAssignment a : assignments) {

			if (a.getEventId() == null) {
				a.setEventId(eventId);
			} else if (!a.getEventId().equals(eventId)) {
				throw new RepositoryException("Specified assignment event ID [" + a.getEventId()
					+ "] doesn't match with parent event ID [" + eventId + "].");
			}

			Map<String, Object> paramMap = assignmentToMap(a);
			insertAssigment.updateByNamedParam(paramMap);
		}

		insertAssigment.flush();
	}

	private void updateAssigments(Long eventId, Set<EventAssignment> assignments) {
		Event event = getById(eventId);
		Set<EventAssignment> curentAssigments = event.getAssignments();
		Set<EventAssignment> newAssigments = assignments;

		for (EventAssignment a : newAssigments) {
			if (!curentAssigments.contains(a)) {
				Map<String, Object> paramMap = assignmentToMap(a);
				insertAssigment.updateByNamedParam(paramMap);
			}
		}

		insertAssigment.flush();

		for (EventAssignment a : curentAssigments) {
			if (!newAssigments.contains(a)) {
				Map<String, Object> paramMap = assignmentToMap(a);
				removeAssigment.updateByNamedParam(paramMap);
			}
		}

		removeAssigment.flush();
	}

	private static final class EventExtractor implements ResultSetExtractor<List<Event>> {

		@Override
        public List<Event> extractData(ResultSet rs) throws SQLException, DataAccessException {
	        Map<Long, Event> map = new HashMap<Long, Event>();
	        Event event = null;

	        while (rs.next()) {
	        	Long id = rs.getLong("id");
	        	event = map.get(id);
	        	if (event == null) {
	    			String name = rs.getString("name");
	    			String rate = rs.getString("rate");
	    			Double price = rs.getDouble("price");

	    			event = new Event();
	    			event.setId(id);
	    			event.setName(name);
	    			event.setBasePrice(price);

	    			EventRating ratingType = EventRating.valueOf(rate);
	                event.setRating(ratingType);

		            map.put(id, event);
	        	}

	        	long assignmentId = rs.getLong("assignmentId");
	        	if (assignmentId != 0) {

	        		long auditoriumId = rs.getLong("auditoriumId");

	        		if (auditoriumId == 0) {
	        			throw new RepositoryException("Auditorium ID is not defined.");
	        		}

	        		Timestamp airTimestamp = rs.getTimestamp("airDate");

	        		if (airTimestamp == null) {
	        			throw new RepositoryException("Air date is not defined.");
	        		}

	        		LocalDateTime airDate = airTimestamp.toLocalDateTime();

	        		EventAssignment assignment = new EventAssignment();
	        		assignment.setId(id);
	        		assignment.setEventId(event.getId());
	        		assignment.setAuditoriumId(auditoriumId);
	        		assignment.setAirDate(airDate);

	        		Set<EventAssignment> assignments = event.getAssignments();
	        		if (assignments == null) {
	        			assignments = new HashSet<EventAssignment>();
	        		}

	        		assignments.add(assignment);
	        	}
	        }

	        return new ArrayList<Event>(map.values());
        }

	}

	private static final class UpdateEvent extends SqlUpdate {
		private static final String SQL_UPDATE_EVENT =
				"UPDATE events SET " +
				"name=:name, " +
				"rate=:rate, " +
				"price=:price " +
				"WHERE id=:id";

		public UpdateEvent(DataSource dataSource) {
			super(dataSource, SQL_UPDATE_EVENT);
			declareParameter(new SqlParameter("id", Types.INTEGER));
			declareParameter(new SqlParameter("name", Types.VARCHAR));
			declareParameter(new SqlParameter("rate", Types.VARCHAR));
			declareParameter(new SqlParameter("price", Types.DOUBLE));
		}
	}

	private static final class InserEvent extends SqlUpdate {
		private static final String SQL_INCERT_EVENT =
				"INSERT INTO events (name, rate, price) " +
				"VALUES (:name, :rate, :price)";

		public InserEvent(DataSource dataSource) {
			super(dataSource, SQL_INCERT_EVENT);
			declareParameter(new SqlParameter("name", Types.VARCHAR));
			declareParameter(new SqlParameter("rate", Types.VARCHAR));
			declareParameter(new SqlParameter("price", Types.DOUBLE));
			setGeneratedKeysColumnNames(new String[]{"id"});
			setReturnGeneratedKeys(true);
		}
	}

	private static final class InsertAssigment extends BatchSqlUpdate {
		private static final String SQL_INCERT_ASSIGMENT =
				"INSERT INTO eventAssignments (eventId, airDate, auditoriumId) " +
				"VALUES (:eventId, :airDate, :auditoriumId)";

		private static final int BATCH_SIZE = 10;

		public InsertAssigment(DataSource dataSource) {
			super(dataSource, SQL_INCERT_ASSIGMENT);
			declareParameter(new SqlParameter("eventId", Types.INTEGER));
			declareParameter(new SqlParameter("airDate", Types.TIMESTAMP));
			declareParameter(new SqlParameter("auditoriumId", Types.INTEGER));
			setBatchSize(BATCH_SIZE);
		}
	}

	private static final class RemoveAssigment extends BatchSqlUpdate {
		private static final String SQL_REMOVE_ASSIGMENT =
				"DELETE FROM eventAssignments WHERE id=:id";

		private static final int BATCH_SIZE = 10;

		public RemoveAssigment(DataSource dataSource) {
			super(dataSource, SQL_REMOVE_ASSIGMENT);
			declareParameter(new SqlParameter("id", Types.INTEGER));
			setBatchSize(BATCH_SIZE);
		}
	}

	private Map<String, Object> eventToMap(Event event) {
		Map<String, Object> paramMap = new HashMap<>();

		if (event.getId() != null) {
			paramMap.put("id", event.getId());
		}

		paramMap.put("name", event.getName());
		paramMap.put("rate", event.getRating().toString());
		paramMap.put("price", event.getBasePrice());

		return paramMap;
	}

	private Map<String, Object> assignmentToMap(EventAssignment assignment) {
		Map<String, Object> paramMap = new HashMap<>();

		if (assignment.getId() != null) {
			paramMap.put("id", assignment.getId());
		}

		paramMap.put("eventId", assignment.getEventId());
		paramMap.put("auditoriumId", assignment.getAuditoriumId());

		ZoneId zone = ZoneId.systemDefault();
		Timestamp airTimestamp = new Timestamp(assignment.getAirDate().atZone(zone).toEpochSecond());
		paramMap.put("airDate", airTimestamp);

		return paramMap;
	}

	/*
	private static final class EventAssigment {
		private Long eventId;
		private Calendar airDate;
		private String auditoriumName;

		public Long getEventId() {
			return eventId;
		}

		public void setEventId(Long eventId) {
			this.eventId = eventId;
		}

		public Calendar getAirDate() {
			return airDate;
		}

		public void setAirDate(Calendar airDate) {
			this.airDate = airDate;
		}

		public String getAuditoriumName() {
			return auditoriumName;
		}

		public void setAuditoriumName(String auditoriumName) {
			this.auditoriumName = auditoriumName;
		}

		@Override
        public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result
	                + ((airDate == null) ? 0 : airDate.hashCode());
	        result = prime
	                * result
	                + ((auditoriumName == null) ? 0 : auditoriumName.hashCode());
	        result = prime * result
	                + ((eventId == null) ? 0 : eventId.hashCode());
	        return result;
        }

		@Override
        public boolean equals(Object obj) {
	        if (this == obj)
		        return true;
	        if (obj == null)
		        return false;
	        if (getClass() != obj.getClass())
		        return false;
	        EventAssigment other = (EventAssigment) obj;
	        if (airDate == null) {
		        if (other.airDate != null)
			        return false;
	        } else if (!airDate.equals(other.airDate))
		        return false;
	        if (auditoriumName == null) {
		        if (other.auditoriumName != null)
			        return false;
	        } else if (!auditoriumName.equals(other.auditoriumName))
		        return false;
	        if (eventId == null) {
		        if (other.eventId != null)
			        return false;
	        } else if (!eventId.equals(other.eventId))
		        return false;
	        return true;
        }
	}

	private Set<EventAssigment> convertToAssigments(Long eventId, Map<Calendar, String> auditoriums) {
		Set<EventAssigment> assigments = new HashSet<>(auditoriums.size());

		for (Entry<Calendar,String> e : auditoriums.entrySet()) {
			EventAssigment a = new EventAssigment();
			a.setEventId(eventId);
			a.setAirDate(e.getKey());
			a.setAuditoriumName(e.getValue());
			assigments.add(a);
		}

		return assigments;
	}
	*/
}
