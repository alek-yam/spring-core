package ru.epam.spring.cinema.domain;

/**
 * The discount definition.
 *
 * @author Alex_Yamskov
 */
public class DiscountReport {
	private final String strategyId;
	private final byte percent;

	/**
	 * Instantiates a new discount.
	 *
	 * @param strategy
	 * 			The discount strategy
	 * @param percent
	 * 			The discount percent from 0 to 100
	 */
	public DiscountReport(String strategyId, byte percent) {
		this.strategyId = strategyId;
		this.percent = percent;
	}

	public String getStrategyId() {
	    return strategyId;
    }

	public byte getPercent() {
	    return percent;
    }

	@Override
	public String toString() {
		return "DiscountReport [strategyId=" + strategyId + ", percent=" + percent + "]";
	}
}
