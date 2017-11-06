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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
	//private MergeAccounts mergeAccounts;
	private UpdateAccount updateAccount;
	private InsertAccount insertAccount;

	@Autowired
    public void setDataSource(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
		//this.mergeAccounts = new MergeAccounts(dataSource);
		this.updateAccount = new UpdateAccount(dataSource);
		this.insertAccount = new InsertAccount(dataSource);
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
	public UserAccount save(UserAccount userAccount) {
		Map<String, Object> paramMap = accountToMap(userAccount);
		//mergeAccounts.updateByNamedParam(paramMap);
	    if (paramMap.get("id") == null) {
	    	KeyHolder keyHolder = new GeneratedKeyHolder();
	    	insertAccount.updateByNamedParam(paramMap, keyHolder);
	    	userAccount.setId(keyHolder.getKey().longValue());
	    } else {
	    	int updatedRows = updateAccount.updateByNamedParam(paramMap);
	    	if (updatedRows == 0) {
	    		throw new RepositoryException("User account with ID [" + userAccount.getId() + "] was not found.");
	    	}
	    }

		return userAccount;
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
			Long id = rs.getLong("id");
			Long userId = rs.getLong("userId");
			Double balance = rs.getDouble("balance");
            return new UserAccount(id, userId, balance);
        }

	}

//	private static final class MergeAccounts extends SqlUpdate {
//		private static final String SQL_MERGE_ACCOUNTS =
//				"MERGE INTO accounts (userId, balance) " +
//				"VALUES (:userId, :balance)";
//
//		public MergeAccounts(DataSource dataSource) {
//			super(dataSource, SQL_MERGE_ACCOUNTS);
//			declareParameter(new SqlParameter("userId", Types.INTEGER));
//			declareParameter(new SqlParameter("balance", Types.DOUBLE));
//		}
//	}

	private static final class UpdateAccount extends SqlUpdate {
		private static final String SQL_UPDATE_ACCOUNT =
				"UPDATE accounts SET " +
				"userId=:userId, " +
				"balance=:balance " +
				"WHERE id=:id";

		public UpdateAccount(DataSource dataSource) {
			super(dataSource, SQL_UPDATE_ACCOUNT);
			declareParameter(new SqlParameter("id", Types.INTEGER));
			declareParameter(new SqlParameter("userId", Types.INTEGER));
			declareParameter(new SqlParameter("balance", Types.DOUBLE));
		}
	}

	private static final class InsertAccount extends SqlUpdate {
		private static final String SQL_INCERT_ACCOUNT =
				"INSERT INTO accounts (userId, balance) " +
				"VALUES (:userId, :balance)";

		public InsertAccount(DataSource dataSource) {
			super(dataSource, SQL_INCERT_ACCOUNT);
			declareParameter(new SqlParameter("userId", Types.INTEGER));
			declareParameter(new SqlParameter("balance", Types.DOUBLE));
			setGeneratedKeysColumnNames(new String[]{"id"});
			setReturnGeneratedKeys(true);
		}
	}

	private Map<String, Object> accountToMap(UserAccount account) {
		Map<String, Object> paramMap = new HashMap<>();

		if (account.getId() != null) {
			paramMap.put("id", account.getId());
		}

		paramMap.put("userId", account.getUserId());
		paramMap.put("balance", account.getBalance());

		return paramMap;
	}
}
