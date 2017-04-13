package ru.epam.spring.cinema.ui.console.convertor;

public class IntegerConverter implements AbstractConverter<Integer> {
	private Integer max = null;

	public IntegerConverter() {

	}

	public IntegerConverter(int max) {
		this.max = max;
	}

	@Override
    public Integer convert(String s) {
        int i = Integer.parseInt(s);

        if (max != null && i > max) {
            throw new IllegalArgumentException("Input can't be more than " + max);
        }

        return i;
    }

}
