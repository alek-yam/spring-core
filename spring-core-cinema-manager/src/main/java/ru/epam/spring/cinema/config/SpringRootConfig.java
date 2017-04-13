package ru.epam.spring.cinema.config;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.hsqldb.util.DatabaseManagerSwing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

import ru.epam.spring.cinema.domain.Auditorium;

@Configuration
@Import({DataSourceConfig.class})
@ComponentScan("ru.epam.spring.cinema")
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class SpringRootConfig {

	private static final String AUDITORIUM_PROPERTIES_FILE_PATH = "auditorium.properties";

	@Autowired
	DataSource dataSource;

	@Bean
	public static PropertyPlaceholderConfigurer getPropertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
		Resource auditoriumProperties = new ClassPathResource(AUDITORIUM_PROPERTIES_FILE_PATH);
		configurer.setLocations(auditoriumProperties);
		configurer.setIgnoreResourceNotFound(true);
		configurer.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
		return configurer;
	}

	@Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(dataSource);
    }

	// it is required for DatabaseManagerSwing
	@Bean
	public JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public Auditorium getBlueRoom(
			@Value("${blueRoom.name}") String name,
			@Value("${blueRoom.numberOfSeats}") long numberOfSeats,
			@Value("${blueRoom.vipSeats}") String vipSeats) {
		return new Auditorium(name, numberOfSeats, toSetOfLongs(vipSeats));
	}

	@Bean
	public Auditorium getGreenRoom(
			@Value("${greenRoom.name}") String name,
			@Value("${greenRoom.numberOfSeats}") long numberOfSeats,
			@Value("${greenRoom.vipSeats}") String vipSeats) {
		return new Auditorium(name, numberOfSeats, toSetOfLongs(vipSeats));
	}

	@Bean
	public Auditorium getRedRoom(
			@Value("${redRoom.name}") String name,
			@Value("${redRoom.numberOfSeats}") long numberOfSeats,
			@Value("${redRoom.vipSeats}") String vipSeats) {
		return new Auditorium(name, numberOfSeats, toSetOfLongs(vipSeats));
	}

	@PostConstruct
	public void startDBManager() {

		//hsqldb
		//DatabaseManagerSwing.main(new String[] { "--url", "jdbc:hsqldb:mem:cinemaDb", "--user", "sa", "--password", "" });

		//derby
		//DatabaseManagerSwing.main(new String[] { "--url", "jdbc:derby:memory:cinemaDb", "--user", "", "--password", "" });

		//h2
		DatabaseManagerSwing.main(new String[] { "--url", "jdbc:h2:mem:cinemaDb", "--user", "sa", "--password", "" });

	}

	private Set<Long> toSetOfLongs(String vipSeats) {
		Set<String> strings = StringUtils.commaDelimitedListToSet(vipSeats);
		Set<Long> longs = new HashSet<Long>(strings.size());

		for (String s : strings) {
			longs.add(Long.parseLong(s));
		}

		return longs;
	}

}
