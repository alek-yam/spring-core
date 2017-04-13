package ru.epam.spring.cinema;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import ru.epam.spring.cinema.config.SpringRootConfig;
import ru.epam.spring.cinema.ui.console.SpringHometaskConsoleUI;

public class CinemaManagerApp {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context
			= new AnnotationConfigApplicationContext(SpringRootConfig.class);
		//DatabaseManagerSwing.main(new String[] { "--url", "jdbc:derby:memory:testdb", "--user", "", "--password", "" });
        SpringHometaskConsoleUI ui = new SpringHometaskConsoleUI(context);
        ui.run();
	}

}
