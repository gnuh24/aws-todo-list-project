package com.ec.order.exceptions.errorCode;

public final class OrderBusinessErrorCode {
	
	private OrderBusinessErrorCode() {}
	
	// ======= ORDER CONTROLLER =======
	public static final String ORD_ORDER_VARIANT_NOT_FOUND = "ORD-ORDER-001";  // Không tìm thấy ProductVariant
	public static final String ORD_ORDER_OUT_OF_STOCK      = "ORD-ORDER-002";  // Số lượng không đủ để tạo đơn
	public static final String ORD_ORDER_INVALID_INPUT     = "ORD-ORDER-003";  // Dữ liệu đầu vào không hợp lệ
	public static final String ORD_ORDER_CREATE_FAILED     = "ORD-ORDER-004";  // Tạo đơn hàng thất bại
	
	// ======= PAYMENT CONTROLLER =======
	public static final String ORD_PAYMENT_FAILED          = "ORD-PAYMENT-001";  // Thanh toán thất bại
	public static final String ORD_PAYMENT_INVALID_METHOD  = "ORD-PAYMENT-002";  // Phương thức không hợp lệ
	public static final String ORD_PAYMENT_ALREADY_PAID    = "ORD-PAYMENT-003";  // Đơn hàng đã thanh toán
	
	// ======= CANCEL ORDER =======
	public static final String ORD_CANCEL_NOT_ALLOWED      = "ORD-CANCEL-001";   // Không thể hủy đơn trong trạng thái hiện tại
	public static final String ORD_CANCEL_ALREADY_DONE     = "ORD-CANCEL-002";   // Đơn đã bị hủy trước đó
}
