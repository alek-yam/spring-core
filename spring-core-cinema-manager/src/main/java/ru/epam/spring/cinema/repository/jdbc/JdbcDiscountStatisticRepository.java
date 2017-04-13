package ru.epam.spring.cinema.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.stereotype.Repository;

import ru.epam.spring.cinema.repository.DiscountStatisticRepository;
import ru.epam.spring.cinema.repository.exception.RepositoryException;
import ru.epam.spring.cinema.statistic.DiscountStatistic;

/**
 * The Class JdbcDiscountStatisticRepository.
 *
 * @author Alex_Yamskov
 */
@Repository("jdbcDiscountStatisticRepository")
public class JdbcDiscountStatisticRepository implements DiscountStatisticRepository {

	private JdbcTemplate template;
	private MergeDiscountStatistic mergeStatistic;

	@Autowired
    public void setDataSource(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
		this.mergeStatistic = new MergeDiscountStatistic(dataSource);
    }

	@Override
    public DiscountStatistic getByStrategyId(@Nonnull String strategyId) {
		String sql = "SELECT * FROM discountStatistic WHERE strategyId = ?";
        List<DiscountStatistic> statistics = template.query(sql, new Object[]{strategyId}, new DiscountStatisticExtractor());

        if (statistics.size() == 0) {
        	return null;
        }

        if (statistics.size() == 1) {
        	return statistics.get(0);
        }

        throw new RepositoryException("Cannot get discount statistic by strategyId [" + strategyId + "]: more than one found.");
    }

	@Override
    public @Nonnull
    Collection<DiscountStatistic> getAll() {
		String sql = "SELECT * FROM discountStatistic";
        List<DiscountStatistic> statistics = template.query(sql, new DiscountStatisticExtractor());
	    return statistics;
    }

	@Override
    public void save(@Nonnull DiscountStatistic object) {
		for (Entry<Long,Long> e : object.getUserCounts().entrySet()) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("strategyId", object.getStrategyId());
			paramMap.put("userId", e.getKey());
			paramMap.put("wasGivenCount", e.getValue());
			mergeStatistic.updateByNamedParam(paramMap);
		}

		if (object.getUnknownUserCount() > 0) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("strategyId", object.getStrategyId());
			paramMap.put("userId", null);
			paramMap.put("wasGivenCount", object.getUnknownUserCount());
			mergeStatistic.updateByNamedParam(paramMap);
		}

		mergeStatistic.flush();
    }

	private static final class DiscountStatisticExtractor implements ResultSetExtractor<List<DiscountStatistic>> {

		@Override
        public List<DiscountStatistic> extractData(ResultSet rs) throws SQLException, DataAccessException {
	        Map<String, DiscountStatistic> map = new HashMap<>();
	        DiscountStatistic stat = null;

	        while (rs.next()) {
	        	String strategyId = rs.getString("strategyId");
	        	stat = map.get(strategyId);
	        	if (stat == null) {
					stat = new DiscountStatistic(strategyId);
		            map.put(strategyId, stat);
	        	}

	        	Long userId = rs.getLong("userId");
	        	Long wasGivenCount = rs.getLong("wasGivenCount");
	        	if (userId > 0) {
		        	stat.getUserCounts().put(userId, wasGivenCount);
	        	} else {
	        		stat.setUnknownUserCount(wasGivenCount);
	        	}
	        }

	        return new ArrayList<DiscountStatistic>(map.values());
        }

	}

	private static final class MergeDiscountStatistic extends BatchSqlUpdate {
		private static final String SQL_MERGE_STATISTIC =
				"MERGE INTO discountStatistic (strategyId, userId, wasGivenCount) " +
			    "KEY (strategyId, userId) " +
				"VALUES (:strategyId, :userId, :wasGivenCount)";

		private static final int BATCH_SIZE = 10;

		public MergeDiscountStatistic(DataSource dataSource) {
			super(dataSource, SQL_MERGE_STATISTIC);
			declareParameter(new SqlParameter("strategyId", Types.VARCHAR));
			declareParameter(new SqlParameter("userId", Types.INTEGER));
			declareParameter(new SqlParameter("wasGivenCount", Types.INTEGER));
			setBatchSize(BATCH_SIZE);
		}
	}
}
