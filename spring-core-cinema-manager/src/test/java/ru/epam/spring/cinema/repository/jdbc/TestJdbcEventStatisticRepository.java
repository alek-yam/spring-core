package ru.epam.spring.cinema.repository.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;

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
    	EventStatistic stat = eventStatRepository.getByEventId(1L);

    	assertNotNull(stat);
    	assertEquals(1, stat.getEventId().longValue());
    	assertEquals(2, stat.getAccessedByNameCount());
    	assertEquals(1, stat.getPriceWereQueriedCount());
    	assertEquals(3, stat.getTicketsWereBookedCount());
    }

    @Test
    public void testGetByIdReturnsNullIfNotFound() {
    	EventStatistic stat = eventStatRepository.getByEventId(999999999L);

    	assertNull(stat);
    }

    @Test
    public void testGetAll() {
    	Collection<EventStatistic> statistics = eventStatRepository.getAll();

    	assertNotNull(statistics);
    	assertEquals(2, statistics.size());

    	EventStatistic stat1 = statistics.stream().filter(s -> s.getEventId().equals(1L)).findFirst().get();
    	assertEquals(1, stat1.getEventId().longValue());
    	assertEquals(2, stat1.getAccessedByNameCount());
    	assertEquals(1, stat1.getPriceWereQueriedCount());
    	assertEquals(3, stat1.getTicketsWereBookedCount());

    	EventStatistic stat2 = statistics.stream().filter(s -> s.getEventId().equals(2L)).findFirst().get();
    	assertEquals(2, stat2.getEventId().longValue());
    	assertEquals(1, stat2.getAccessedByNameCount());
    	assertEquals(3, stat2.getPriceWereQueriedCount());
    	assertEquals(1, stat2.getTicketsWereBookedCount());
    }

    @Test
    public void testSaveWhenAlreadyExists() {
    	EventStatistic stat = new EventStatistic(1L);
    	stat.setAccessedByNameCount(44);
    	stat.setPriceWereQueriedCount(55);
    	stat.setTicketsWereBookedCount(66);

    	int oldSize = eventStatRepository.getAll().size();
    	eventStatRepository.save(stat);
    	int newSize = eventStatRepository.getAll().size();

    	assertEquals(oldSize, newSize);

    	EventStatistic actualStat = eventStatRepository.getByEventId(1L);
    	assertNotNull(actualStat);
    	assertEquals(stat.getAccessedByNameCount(), actualStat.getAccessedByNameCount());
    	assertEquals(stat.getPriceWereQueriedCount(), actualStat.getPriceWereQueriedCount());
    	assertEquals(stat.getTicketsWereBookedCount(), actualStat.getTicketsWereBookedCount());
    }

    @Test
    public void testSaveWhenNotExists() {
    	EventStatistic stat = new EventStatistic(3L);
    	stat.setAccessedByNameCount(77);
    	stat.setPriceWereQueriedCount(88);
    	stat.setTicketsWereBookedCount(99);

    	int oldSize = eventStatRepository.getAll().size();
    	eventStatRepository.save(stat);
    	int newSize = eventStatRepository.getAll().size();

    	assertEquals(oldSize + 1, newSize);

    	EventStatistic actualStat = eventStatRepository.getByEventId(3L);
    	assertNotNull(actualStat);
    	assertEquals(stat.getAccessedByNameCount(), actualStat.getAccessedByNameCount());
    	assertEquals(stat.getPriceWereQueriedCount(), actualStat.getPriceWereQueriedCount());
    	assertEquals(stat.getTicketsWereBookedCount(), actualStat.getTicketsWereBookedCount());
    }
}
