package ru.epam.spring.cinema.repository.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import ru.epam.spring.cinema.statistic.EventStatistic;

public class TestJdbcEventStatisticRepository {

	private EmbeddedDatabase db;
    private JdbcEventStatisticRepository eventStatRepository;

    @Before
    public void setUp() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder
				.setName("cinemaDb")
				.setType(EmbeddedDatabaseType.H2)
				.addScript("schema.sql")
				.addScript("test-data.sql")
				.build();

		eventStatRepository = new JdbcEventStatisticRepository();
		eventStatRepository.setDataSource(db);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void testGetById() {
    	EventStatistic stat = eventStatRepository.getByEventName("Avatar-2");

    	assertNotNull(stat);
    	assertEquals("Avatar-2", stat.getEventName());
    	assertEquals(2, stat.getAccessedByNameCount());
    	assertEquals(1, stat.getPriceWereQueriedCount());
    	assertEquals(0, stat.getTicketsWereBookedCount());
    }

    @Test
    public void testGetByIdReturnsNullIfNotFound() {
    	EventStatistic stat = eventStatRepository.getByEventName("XXX-XXX-XXX");

    	assertNull(stat);
    }

    @Test
    public void testGetAll() {
    	Collection<EventStatistic> statistics = eventStatRepository.getAll();

    	assertNotNull(statistics);
    	assertEquals(1, statistics.size());

    	List<EventStatistic> statisticsList = new ArrayList<>(statistics);
    	EventStatistic stat = statisticsList.get(0);
    	assertEquals("Avatar-2", stat.getEventName());
    	assertEquals(2, stat.getAccessedByNameCount());
    	assertEquals(1, stat.getPriceWereQueriedCount());
    	assertEquals(0, stat.getTicketsWereBookedCount());
    }

    @Test
    public void testSaveWhenAlreadyExists() {
    	EventStatistic stat = new EventStatistic("Avatar-2");
    	stat.setAccessedByNameCount(3);
    	stat.setPriceWereQueriedCount(2);
    	stat.setTicketsWereBookedCount(1);

    	int oldSize = eventStatRepository.getAll().size();
    	eventStatRepository.save(stat);
    	int newSize = eventStatRepository.getAll().size();

    	assertEquals(oldSize, newSize);

    	EventStatistic actualStat = eventStatRepository.getByEventName("Avatar-2");
    	assertNotNull(actualStat);
    	assertEquals(stat.getAccessedByNameCount(), actualStat.getAccessedByNameCount());
    	assertEquals(stat.getPriceWereQueriedCount(), actualStat.getPriceWereQueriedCount());
    	assertEquals(stat.getTicketsWereBookedCount(), actualStat.getTicketsWereBookedCount());
    }

    @Test
    public void testSaveWhenNotExists() {
    	EventStatistic stat = new EventStatistic("Avatar-3");
    	stat.setAccessedByNameCount(7);
    	stat.setPriceWereQueriedCount(6);
    	stat.setTicketsWereBookedCount(5);

    	int oldSize = eventStatRepository.getAll().size();
    	eventStatRepository.save(stat);
    	int newSize = eventStatRepository.getAll().size();

    	assertEquals(oldSize + 1, newSize);

    	EventStatistic actualStat = eventStatRepository.getByEventName("Avatar-3");
    	assertNotNull(actualStat);
    	assertEquals(stat.getAccessedByNameCount(), actualStat.getAccessedByNameCount());
    	assertEquals(stat.getPriceWereQueriedCount(), actualStat.getPriceWereQueriedCount());
    	assertEquals(stat.getTicketsWereBookedCount(), actualStat.getTicketsWereBookedCount());
    }
}
