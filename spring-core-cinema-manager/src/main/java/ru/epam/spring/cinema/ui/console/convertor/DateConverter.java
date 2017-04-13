package ru.epam.spring.cinema.ui.console.convertor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter implements AbstractConverter<Date> {
	private final DateFormat format;

	public DateConverter(String pattern) {
		format = new SimpleDateFormat(pattern);
	}

	@Override
    public Date convert(String s) {
	    try {
	        return format.parse(s);
        } catch (ParseException e) {
        	throw new IllegalArgumentException("Invalid date format.", e);
        }
    }

}
