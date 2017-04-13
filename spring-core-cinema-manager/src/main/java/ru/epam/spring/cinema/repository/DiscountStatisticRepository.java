package ru.epam.spring.cinema.repository;

import java.util.Collection;

import javax.annotation.Nonnull;

import ru.epam.spring.cinema.statistic.DiscountStatistic;

public interface DiscountStatisticRepository {

    public DiscountStatistic getByStrategyId(@Nonnull String strategyId);

    public @Nonnull Collection<DiscountStatistic> getAll();

    public void save(@Nonnull DiscountStatistic object);

}
