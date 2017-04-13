package ru.epam.spring.cinema.domain;

/**
 * The price item definition.
 *
 * @author Alex_Yamskov
 */
public class PriceItem {
	private String eventName;
	private double price;
	private double vipPrice;

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getVipPrice() {
		return vipPrice;
	}

	public void setVipPrice(double vipPrice) {
		this.vipPrice = vipPrice;
	}
}
