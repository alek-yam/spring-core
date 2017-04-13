package ru.epam.spring.cinema.repository.simple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.domain.DomainObject;
import ru.epam.spring.cinema.repository.AbstractRepository;

/**
 * The Class AbstractSimpleRepository.
 *
 * @author Alex_Yamskov
 * @param <T> the generic type
 */
public class AbstractSimpleRepository<T extends DomainObject> implements AbstractRepository<T> {

	private static final int MAX_ID_LIMIT = 1000;

	private final Map<Long, T> objects = new HashMap<Long, T>();
	private final Random rand = new Random();

	@Override
    public T getById(Long id) {
		return objects.get(id);
    }

	@Override
    public @Nonnull
    Collection<T> getAll() {
		return objects.values();
    }

	@Override
    public T save(T object) {
		if (object.getId() == null) {
			Long id = getNextId();
			object.setId(id);
		}

		objects.put(object.getId(), object);
		return objects.get(object.getId());
    }

	@Override
    public void removeById(Long id) {
		objects.remove(id);
    }

	private Long getNextId() {
		for (int i = 0; i < 1000000; i++) {
			Long id = (long)rand.nextInt(MAX_ID_LIMIT);
			if (!objects.containsKey(id)) {
				return id;
			}
		}

		throw new RuntimeException("Cannot generate new id.");
	}
}
