package ru.epam.spring.cinema.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import ru.epam.spring.cinema.domain.User;
import ru.epam.spring.cinema.repository.UserRepository;
import ru.epam.spring.cinema.repository.exception.RepositoryException;
import ru.epam.spring.cinema.repository.filter.UserFilter;

/**
 * The Class JdbcUserRepository.
 *
 * @author Alex_Yamskov
 */
@Repository("jdbcUserRepository")
public class JdbcUserRepository implements UserRepository {

	private JdbcTemplate template;
	private UpdateUser updateUser;
	private InsertUser insertUser;

	@Autowired
    public void setDataSource(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
		this.updateUser = new UpdateUser(dataSource);
		this.insertUser = new InsertUser(dataSource);
    }

	@Override
    public User getById(@Nonnull Long id) {
        String sql = "SELECT u.id, u.firstName, u.lastName, u.birthday, u.email, u.password, u.roles, t.id as ticketId " +
        		"FROM users u LEFT JOIN tickets t ON u.id = t.userId WHERE u.id = ?";

        List<User> users = template.query(sql, new Object[]{id}, new UserWithTicketsExtractor());

        if (users.size() == 0) {
        	return null;
        }

        if (users.size() == 1) {
        	return users.get(0);
        }

        throw new RepositoryException("More than one users with ID [" + id + "] were found.");
    }

	@Override
    public @Nonnull
    Collection<User> getAll() {
        String sql = "SELECT u.id, u.firstName, u.lastName, u.birthday, u.email, u.password, u.roles, t.id as ticketId " +
        		"FROM users u LEFT JOIN tickets t ON u.id = t.userId";
	    return template.query(sql, new UserWithTicketsExtractor());
    }

	@Override
    public User save(@Nonnull User object) {
		Map<String, Object> paramMap = userToMap(object);
	    if (paramMap.get("id") == null) {
	    	KeyHolder keyHolder = new GeneratedKeyHolder();
	    	insertUser.updateByNamedParam(paramMap, keyHolder);
	    	object.setId(keyHolder.getKey().longValue());
	    } else {
	    	int updatedRows = updateUser.updateByNamedParam(paramMap);
	    	if (updatedRows == 0) {
	    		throw new RepositoryException("User with ID [" + object.getId() + "] was not found.");
	    	}
	    }

	    return object;
    }

	@Override
    public void removeById(@Nonnull Long id) {
		String sql = "delete from users where id = ?";
		int updatedRows = template.update(sql, id);
    	if (updatedRows == 0) {
    		throw new RepositoryException("User with ID [" + id + "] was not found.");
    	}
    }

	@Override
    public @Nonnull
    Collection<User> getByFilter(@Nonnull UserFilter filter) {
		List<User> users = new ArrayList<>();

        String email = filter.getEmail();
        if (email != null) {
            String sql = "SELECT u.id, u.firstName, u.lastName, u.birthday, u.email, u.password, u.roles, t.id as ticketId " +
            		"FROM users u LEFT JOIN tickets t ON u.id = t.userId WHERE u.email = ?";

        	users = template.query(sql, new Object[]{email}, new UserWithTicketsExtractor());
        }

        return users;
    }

	private static final class UserWithTicketsExtractor implements ResultSetExtractor<List<User>> {

		@Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
	        Map<Long, User> map = new HashMap<Long, User>();
	        User user = null;

	        while (rs.next()) {
	        	Long id = rs.getLong("id");
	        	user = map.get(id);
	        	if (user == null) {
					String firstName = rs.getString("firstName");
					String lastName = rs.getString("lastName");
					LocalDate birthday = rs.getDate("birthday").toLocalDate();
					String email = rs.getString("email");
					String password = rs.getString("password");

		            String rolesStr = rs.getString("roles");
		            String[] rolesArray = rolesStr.split(",");
		            Set<String> roles = new HashSet<String>(Arrays.asList(rolesArray));

		            user = new User();
		            user.setId(id);
		            user.setFirstName(firstName);
		            user.setLastName(lastName);
		            user.setBirthday(birthday);
		            user.setEmail(email);
		            user.setPassword(password);
		            user.setRoles(roles);
		            map.put(id, user);
	        	}

	        	Long ticketId = rs.getLong("ticketId");
	        	if (ticketId > 0) {
	        		user.getTickets().add(ticketId);
	        	}
	        }

	        return new ArrayList<User>(map.values());
        }

	}

	private static final class UpdateUser extends SqlUpdate {
		private static final String SQL_UPDATE_USER =
				"UPDATE users SET " +
				"firstName=:firstName, " +
				"lastName=:lastName, " +
				"birthday=:birthday, " +
				"email=:email, " +
				"password=:password, " +
				"roles=:roles " +
				"WHERE id=:id";

		public UpdateUser(DataSource dataSource) {
			super(dataSource, SQL_UPDATE_USER);
			declareParameter(new SqlParameter("id", Types.INTEGER));
			declareParameter(new SqlParameter("firstName", Types.VARCHAR));
			declareParameter(new SqlParameter("lastName", Types.VARCHAR));
			declareParameter(new SqlParameter("birthday", Types.DATE));
			declareParameter(new SqlParameter("email", Types.VARCHAR));
			declareParameter(new SqlParameter("password", Types.VARCHAR));
			declareParameter(new SqlParameter("roles", Types.VARCHAR));
		}
	}

	private static final class InsertUser extends SqlUpdate {
		private static final String SQL_INCERT_USER =
				"INSERT INTO users (firstName, lastName, birthday, email, password, roles) " +
				"VALUES (:firstName, :lastName, :birthday, :email, :password, :roles)";

		public InsertUser(DataSource dataSource) {
			super(dataSource, SQL_INCERT_USER);
			declareParameter(new SqlParameter("firstName", Types.VARCHAR));
			declareParameter(new SqlParameter("lastName", Types.VARCHAR));
			declareParameter(new SqlParameter("birthday", Types.DATE));
			declareParameter(new SqlParameter("email", Types.VARCHAR));
			declareParameter(new SqlParameter("password", Types.VARCHAR));
			declareParameter(new SqlParameter("roles", Types.VARCHAR));
			setGeneratedKeysColumnNames(new String[]{"id"});
			setReturnGeneratedKeys(true);
		}
	}

	private Map<String, Object> userToMap(User user) {
		Map<String, Object> paramMap = new HashMap<>();

		if (user.getId() != null) {
			paramMap.put("id", user.getId());
		}

		paramMap.put("firstName", user.getFirstName());
		paramMap.put("lastName", user.getLastName());
		paramMap.put("birthday", java.sql.Date.valueOf(user.getBirthday()));
		paramMap.put("email", user.getEmail());
		paramMap.put("password", user.getPassword());
		String roles = String.join(",", user.getRoles());
		paramMap.put("roles", roles);

		return paramMap;
	}
}
