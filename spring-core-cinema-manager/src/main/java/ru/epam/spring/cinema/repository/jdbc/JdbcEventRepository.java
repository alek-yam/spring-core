package ru.epam.spring.cinema.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
        String sql = "SELECT e.id, e.name, e.rate, e.price, a.airDate, a.auditoriumName" +
        		" FROM events e LEFT JOIN eventAssignments a ON e.id = a.eventId WHERE e.id = ?";

        List<Event> events = template.query(sql, new Object[]{id}, new EventExtractor());

        if (events.size() == 0) {
        	return null;
        }

        if (events.size() == 1) {
        	return events.get(0);
        }

        throw new RepositoryException("Cannot get event by ID [" + id + "]: more than one found.");
    }

	@Override
    public @Nonnull
    Collection<Event> getAll() {
        String sql = "SELECT e.id, e.name, e.rate, e.price, a.airDate, a.auditoriumName" +
        		" FROM events e LEFT JOIN eventAssignments a ON e.id = a.eventId";
        List<Event> events = template.query(sql, new EventExtractor());
        return events;
    }

	@Override
    public Event save(@Nonnull Event object) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("name", object.getName());
		paramMap.put("rate", object.getRating().toString());
		paramMap.put("price", object.getBasePrice());

	    if (object.getId() == null) {
	    	KeyHolder keyHolder = new GeneratedKeyHolder();
	    	insertEvent.updateByNamedParam(paramMap, keyHolder);
	    	object.setId(keyHolder.getKey().longValue());
	    	insertAssigments(object.getId(), object.getAuditoriums());
	    } else {
	    	paramMap.put("id", object.getId());
	    	int updatedRows = updateEvent.updateByNamedParam(paramMap);
	    	if (updatedRows == 0) {
	    		throw new RepositoryException("Cannot update event with ID [" + object.getId() + "]: not found.");
	    	}
	    	updateAssigments(object.getId(), object.getAuditoriums());
	    }

	    return object;
    }

	@Override
    public void removeById(@Nonnull Long id) {
		String deleteFromEventsSql = "DELETE FROM events WHERE id = ?";
		int updatedRows = template.update(deleteFromEventsSql, id);
    	if (updatedRows == 0) {
    		throw new RepositoryException("Cannot remove event with ID [" + id + "]: not found.");
    	}

    	String deleteFromAssigmentsSql = "DELETE FROM eventAssignments WHERE eventId = ?";
    	template.update(deleteFromAssigmentsSql, id);
    }

	@Override
    public @Nonnull
    Collection<Event> getByFilter(@Nonnull EventFilter filter) {
		List<Event> events = new ArrayList<>();

        String name = filter.getName();
        if (name != null) {
            String sql = "SELECT e.id, e.name, e.rate, e.price, a.airDate, a.auditoriumName" +
            		" FROM events e LEFT JOIN eventAssignments a ON e.id = a.eventId WHERE e.name = ?";

            events = template.query(sql, new Object[]{name}, new EventExtractor());
        }

        return events;
    }

	private void insertAssigments(Long eventId, Map<Calendar, String> auditoriums) {
		for (Entry<Calendar,String> e : auditoriums.entrySet()) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("eventId", eventId);
			Timestamp airDate = new Timestamp(e.getKey().getTime().getTime());
			paramMap.put("airDate", airDate);
			paramMap.put("auditoriumName", e.getValue());

			insertAssigment.updateByNamedParam(paramMap);
		}

		insertAssigment.flush();
	}

	private void updateAssigments(Long eventId, Map<Calendar, String> auditoriums) {
		Event event = getById(eventId);
		Set<EventAssigment> curentAssigments = convertToAssigments(event.getId(), event.getAuditoriums());
		Set<EventAssigment> newAssigments = convertToAssigments(eventId, auditoriums);

		for (EventAssigment a : newAssigments) {
			if (!curentAssigments.contains(a)) {
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("eventId", a.getEventId());
				Timestamp airDate = new Timestamp(a.getAirDate().getTimeInMillis());
				paramMap.put("airDate", airDate);
				paramMap.put("auditoriumName", a.getAuditoriumName());
				insertAssigment.updateByNamedParam(paramMap);
			}
		}

		insertAssigment.flush();

		for (EventAssigment a : curentAssigments) {
			if (!newAssigments.contains(a)) {
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("eventId", a.getEventId());
				Timestamp airDate = new Timestamp(a.getAirDate().getTimeInMillis());
				paramMap.put("airDate", airDate);
				paramMap.put("auditoriumName", a.getAuditoriumName());
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

	        	Timestamp timestamp = rs.getTimestamp("airDate");
	        	Calendar airDate = null;
	        	if (timestamp != null) {
		            airDate = new GregorianCalendar();
		            airDate.setTimeInMillis(timestamp.getTime());
		            event.getAirDates().add(airDate);
	        	}

	            String auditoriumName = rs.getString("auditoriumName");
	            if (airDate != null && auditoriumName != null) {
	            	event.getAuditoriums().put(airDate, auditoriumName);
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
				"INSERT INTO eventAssignments (eventId, airDate, auditoriumName) " +
				"VALUES (:eventId, :airDate, :auditoriumName)";

		private static final int BATCH_SIZE = 10;

		public InsertAssigment(DataSource dataSource) {
			super(dataSource, SQL_INCERT_ASSIGMENT);
			declareParameter(new SqlParameter("eventId", Types.INTEGER));
			declareParameter(new SqlParameter("airDate", Types.TIMESTAMP));
			declareParameter(new SqlParameter("auditoriumName", Types.VARCHAR));
			setBatchSize(BATCH_SIZE);
		}
	}

	private static final class RemoveAssigment extends BatchSqlUpdate {
		private static final String SQL_REMOVE_ASSIGMENT =
				"DELETE FROM eventAssignments " +
				"WHERE eventId=:eventId AND airDate=:airDate AND auditoriumName=:auditoriumName";

		private static final int BATCH_SIZE = 10;

		public RemoveAssigment(DataSource dataSource) {
			super(dataSource, SQL_REMOVE_ASSIGMENT);
			declareParameter(new SqlParameter("eventId", Types.INTEGER));
			declareParameter(new SqlParameter("airDate", Types.TIMESTAMP));
			declareParameter(new SqlParameter("auditoriumName", Types.VARCHAR));
			setBatchSize(BATCH_SIZE);
		}
	}

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
}
