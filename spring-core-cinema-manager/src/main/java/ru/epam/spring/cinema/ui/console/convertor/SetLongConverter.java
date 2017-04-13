package ru.epam.spring.cinema.ui.console.convertor;

import java.util.HashSet;
import java.util.Set;

public class SetLongConverter implements AbstractConverter<Set<Long>> {

	@Override
    public Set<Long> convert(String s) {
		String[] ss = s.split(",");
		Set<Long> result = new HashSet<Long>(ss.length);
		for (String str : ss) {
			result.add(Long.parseLong(str.trim()));
		}
	    return result;
    }

}
