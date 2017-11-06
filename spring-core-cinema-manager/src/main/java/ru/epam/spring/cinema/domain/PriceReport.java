package ru.epam.spring.cinema.domain;

/**
 * The price report definition.
 *
 * @author Alex_Yamskov
 */
public class PriceReport {
	private final double totalPrice;
	private final double finalPrice;
	private final DiscountReport discountReport;

	public PriceReport(double totalPrice, double finalPrice, DiscountReport discount) {
		this.totalPrice = totalPrice;
		this.finalPrice = finalPrice;
		this.discountReport = discount;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public double getFinalPrice() {
		return finalPrice;
	}

	public double getDiscount() {
		return totalPrice - finalPrice;
	}

	public DiscountReport getDiscountReport() {
		return discountReport;
	}

	@Override
	public String toString() {
		return "PriceReport [totalPrice=" + totalPrice
				+ ", finalPrice=" + finalPrice
				+ ", discount=" + getDiscount()
				+ ", discountReport=" + discountReport + "]";
	}

}
