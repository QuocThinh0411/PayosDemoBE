package com.springboot.app.type;

import vn.payos.type.ItemData;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class CreatePaymentLinkRequestBody {
//	private String productName;
	List<ItemData> items;
	private String description;
	private String returnUrl;
	private int amount;
	private String cancelUrl;
	private int orderCode;

	
	public int getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(int orderCode) {
		this.orderCode = orderCode;
	}

	public List<ItemData> getItems() {
		return items;
	}

	public void setItems(List<ItemData> items) {
		this.items = items;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

}