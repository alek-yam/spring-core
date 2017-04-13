package ru.epam.spring.cinema.ui.console.convertor;

import javax.annotation.Nonnull;

public interface AbstractConverter<T> {

	public T convert(@Nonnull String s);

}
