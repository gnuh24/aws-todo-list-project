package com.ec.order.dto.order;

import lombok.Data;
import java.util.List;

@Data
public class CheckoutRequest {
	
	private String note;
	
	private String receiverName;
	
	private String receiverPhone;
	
	private String receiverAddress;
	
	private List<CheckoutItem> productVariantList;
}
