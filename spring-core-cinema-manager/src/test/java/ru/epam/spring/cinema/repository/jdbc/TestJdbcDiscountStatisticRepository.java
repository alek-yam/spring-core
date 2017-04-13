package ru.epam.spring.cinema.repository.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import ru.epam.spring.cinema.statistic.DiscountStatistic;

public class TestJdbcDiscountStatisticRepository {

	private EmbeddedDatabase db;
    private JdbcDiscountStatisticRepository discStatRepository;

    @Before
    public void setUp() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder
				.setName("cinemaDb")
				.setType(EmbeddedDatabaseType.H2)
				.addScript("schema.sql")
				.addScript("test-data.sql")
				.build();

		discStatRepository = new JdbcDiscountStatisticRepository();
		discStatRepository.setDataSource(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void testGetById() {
    	DiscountStatistic stat = discStatRepository.getByStrategyId("BirthdayDiscount");

    	assertNotNull(stat);
    	assertEquals("BirthdayDiscount", stat.getStrategyId());
    	assertNotNull(stat.getUserCounts());
    	assertEquals(1, stat.getUserCounts().size());
    	assertEquals(1, stat.getUserCounts().get(1L).longValue());
    	assertEquals(0, stat.getUnknownUserCount());
    	assertEquals(1, stat.getWasGivenTotal());
    }

    @Test
    public void testGetByIdReturnsNullIfNotFound() {
    	DiscountStatistic stat = discStatRepository.getByStrategyId("XXX-XXX-XXX");

    	assertNull(stat);
    }

    @Test
    public void testGetAll() {
    	Collection<DiscountStatistic> statistics = discStatRepository.getAll();

    	assertNotNull(statistics);
    	assertEquals(2, statistics.size());
    }

    @Test
    public void testSaveWhenAlreadyExists() {
    	DiscountStatistic stat = new DiscountStatistic("BirthdayDiscount");
    	Map<Long,Long> userCounts = new HashMap<>();
    	userCounts.put(1L, 2L);
    	stat.setUserCounts(userCounts);
    	stat.setUnknownUserCount(0);

    	int oldSize = discStatRepository.getAll().size();
    	discStatRepository.save(stat);
    	int newSize = discStatRepository.getAll().size();

    	assertEquals(oldSize, newSize);

    	DiscountStatistic actualStat = discStatRepository.getByStrategyId("BirthdayDiscount");
    	assertNotNull(actualStat);
    	assertNotNull(actualStat.getUserCounts());
    	assertEquals(stat.getUserCounts().size(), actualStat.getUserCounts().size());
    	assertEquals(stat.getUserCounts().get(1L), actualStat.getUserCounts().get(1L));
    }

    @Test
    public void testSaveWhenStratagyNotExists() {
    	DiscountStatistic stat = new DiscountStatistic("NewDiscount");
    	stat.setUnknownUserCount(1);

    	int oldSize = discStatRepository.getAll().size();
    	discStatRepository.save(stat);
    	int newSize = discStatRepository.getAll().size();

    	assertEquals(oldSize + 1, newSize);

    	DiscountStatistic actualStat = discStatRepository.getByStrategyId("NewDiscount");
    	assertNotNull(actualStat);
    	assertNotNull(actualStat.getUserCounts());
    	assertEquals(0, actualStat.getUserCounts().size());
    	assertEquals(stat.getUnknownUserCount(), actualStat.getUnknownUserCount());
    }

    @Test
    public void testSaveWhenUserNotExists() {
    	DiscountStatistic stat = new DiscountStatistic("BirthdayDiscount");
    	Map<Long,Long> userCounts = new HashMap<>();
    	userCounts.put(1L, 3L);
    	userCounts.put(2L, 1L);
    	stat.setUserCounts(userCounts);
    	stat.setUnknownUserCount(0);

    	int oldSize = discStatRepository.getAll().size();
    	discStatRepository.save(stat);
    	int newSize = discStatRepository.getAll().size();

    	assertEquals(oldSize, newSize);

    	DiscountStatistic actualStat = discStatRepository.getByStrategyId("BirthdayDiscount");
    	assertNotNull(actualStat);
    	assertNotNull(actualStat.getUserCounts());
    	assertEquals(stat.getUserCounts().size(), actualStat.getUserCounts().size());
    	assertEquals(stat.getUserCounts().get(1L), actualStat.getUserCounts().get(1L));
    	assertEquals(stat.getUserCounts().get(2L), actualStat.getUserCounts().get(2L));
    }
}
