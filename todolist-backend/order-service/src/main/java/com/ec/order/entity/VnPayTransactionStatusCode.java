package com.ec.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum VnPayTransactionStatusCode {
	SUCCESS("00", "Giao dịch thành công."),
	PENDING("01", "Giao dịch chưa hoàn tất."),
	FAILED("02", "Giao dịch bị lỗi."),
	REVERSED("04", "Giao dịch đảo."),
	REFUND_PROCESSING("05", "VNPAY đang xử lý hoàn tiền."),
	REFUND_REQUESTED("06", "VNPAY đã gửi yêu cầu hoàn tiền."),
	SUSPICIOUS("07", "Giao dịch bị nghi ngờ gian lận."),
	REFUND_REJECTED("09", "Giao dịch hoàn trả bị từ chối."),
	UNKNOWN(null, "Mã transactionStatus không xác định.");
	
	private final String code;
	private final String description;
	
	public static String getNoteByCode(String code) {
		for (VnPayTransactionStatusCode t : values()) {
			if (Objects.equals(t.code, code)) {
				return t.description;
			}
		}
		return UNKNOWN.description;
	}
	
	public static VnPayTransactionStatusCode fromCode(String code) {
		for (VnPayTransactionStatusCode t : values()) {
			if (Objects.equals(t.code, code)) {
				return t;
			}
		}
		return UNKNOWN;
	}
}
