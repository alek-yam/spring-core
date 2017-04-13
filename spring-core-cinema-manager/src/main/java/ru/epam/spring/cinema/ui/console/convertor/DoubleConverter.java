package ru.epam.spring.cinema.ui.console.convertor;

public class DoubleConverter implements AbstractConverter<Double> {

	@Override
    public Double convert(String s) {
	    return Double.parseDouble(s);
    }

}
