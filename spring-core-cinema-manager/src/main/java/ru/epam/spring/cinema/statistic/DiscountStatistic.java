package ru.epam.spring.cinema.statistic;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import ru.epam.spring.cinema.service.UserService;

public class DiscountStatistic {

	private final String strategyId;
	private Map<Long, Long> userCounts = new HashMap<Long, Long>();
	private long unknownUserCount = 0;

	public DiscountStatistic(String discountName) {
		this.strategyId = discountName;
	}

	public String getStrategyId() {
	    return strategyId;
    }

	public Map<Long, Long> getUserCounts() {
	    return userCounts;
    }

	public void setUserCounts(Map<Long, Long> userCounts) {
	    this.userCounts = userCounts;
    }

	public long getUnknownUserCount() {
	    return unknownUserCount;
    }

	public void setUnknownUserCount(long unknownUserCount) {
	    this.unknownUserCount = unknownUserCount;
    }

	public void incrementWasGivenCount(Long userId) {
		if (userId != null) {
			Long count = userCounts.get(userId);
			userCounts.put(userId, count != null ? count + 1 : 1);
		} else {
			unknownUserCount++;
		}
	}

	public long getWasGivenTotal() {
		long totalCount = unknownUserCount;

		for (Long countForUser : userCounts.values()) {
			totalCount += countForUser;
		}

		return totalCount;
	}

	@Override
    public String toString() {
		Map<String, Long> forRegisteredUsersCount = new TreeMap<String, Long>();

		for (Entry<Long,Long> e : userCounts.entrySet()) {
			forRegisteredUsersCount.put("id " + e.getKey().toString(), e.getValue());
		}

	    return "DiscountStatistic [strategyId=" + strategyId
	            + ", wasGivenTotalCount=" + getWasGivenTotal()
	            + ", forRegisteredUsersCount=" + forRegisteredUsersCount
	            + ", forUnknownUsersCount=" + unknownUserCount + "]";
    }

    public String toString(UserService userService) {
		Map<String, Long> forRegisteredUsersCount = new TreeMap<String, Long>();

		for (Entry<Long,Long> e : userCounts.entrySet()) {
			forRegisteredUsersCount.put(userService.getById(e.getKey()).getEmail(), e.getValue());
		}

	    return "DiscountStatistic [strategyId=" + strategyId
	            + ", wasGivenTotalCount=" + getWasGivenTotal()
	            + ", forKnownUsersCount=" + forRegisteredUsersCount
	            + ", forUnknownUsersCount=" + unknownUserCount + "]";
    }

}
