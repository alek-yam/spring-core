package ru.epam.spring.cinema.repository;

import java.util.Collection;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.statistic.EventStatistic;

public interface EventStatisticRepository {

    public EventStatistic getByEventName(@Nonnull String eventName);

    public @Nonnull Collection<EventStatistic> getAll();

    public void save(@Nonnull EventStatistic object);

}
