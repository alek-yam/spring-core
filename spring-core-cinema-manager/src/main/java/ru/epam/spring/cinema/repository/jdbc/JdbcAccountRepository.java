package ru.epam.spring.cinema.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.stereotype.Repository;

import ru.epam.spring.cinema.domain.UserAccount;
import ru.epam.spring.cinema.repository.AccountRepository;
import ru.epam.spring.cinema.repository.exception.RepositoryException;

/**
 * The Class JdbcAccountRepository.
 *
 * @author Alex_Yamskov
 */
@Repository("jdbcAccountRepository")
public class JdbcAccountRepository implements AccountRepository {

	private JdbcTemplate template;
	private MergeAccounts mergeAccounts;

	@Autowired
    public void setDataSource(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
		this.mergeAccounts = new MergeAccounts(dataSource);
    }

	@Override
	public UserAccount getByUserId(Long userId) {
		String sql = "SELECT * FROM accounts WHERE userId = ?";
        List<UserAccount> accounts = template.query(sql, new Object[]{userId}, new AccountRowMapper());

        if (accounts.size() == 0) {
        	return null;
        }

        if (accounts.size() == 1) {
        	return accounts.get(0);
        }

        throw new RepositoryException("Cannot get account by user ID [" + userId + "]: more than one found.");
	}

	@Override
	public Collection<UserAccount> getAll() {
		String sql = "SELECT * FROM accounts";
        List<UserAccount> accounts = template.query(sql, new AccountRowMapper());
	    return accounts;
	}

	@Override
	public void save(UserAccount userAccount) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", userAccount.getUserId());
		paramMap.put("balance", userAccount.getBalance());
    	mergeAccounts.updateByNamedParam(paramMap);
	}

	@Override
	public void removeByUserId(Long userId) {
		String sql = "delete from accounts where userId = ?";
		int updatedRows = template.update(sql, userId);
    	if (updatedRows == 0) {
    		throw new RepositoryException("Cannot remove account by user ID [" + userId + "]: not found.");
    	}
	}

	private static final class AccountRowMapper implements RowMapper<UserAccount> {

		@Override
        public UserAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long userId = rs.getLong("userId");
			Double balance = rs.getDouble("balance");
            return new UserAccount(userId, balance);
        }

	}

	private static final class MergeAccounts extends SqlUpdate {
		private static final String SQL_MERGE_ACCOUNTS =
				"MERGE INTO accounts (userId, balance) " +
				"VALUES (:userId, :balance)";

		public MergeAccounts(DataSource dataSource) {
			super(dataSource, SQL_MERGE_ACCOUNTS);
			declareParameter(new SqlParameter("userId", Types.INTEGER));
			declareParameter(new SqlParameter("balance", Types.DOUBLE));
		}
	}
}
