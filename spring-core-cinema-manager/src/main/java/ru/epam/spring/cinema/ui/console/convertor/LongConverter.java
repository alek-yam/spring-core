package ru.epam.spring.cinema.ui.console.convertor;

public class LongConverter implements AbstractConverter<Long> {

	@Override
    public Long convert(String s) {
	    return Long.parseLong(s);
    }

}
