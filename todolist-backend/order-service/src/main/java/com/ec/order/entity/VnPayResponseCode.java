package com.ec.order.entity;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum VnPayResponseCode {
	SUCCESS("00", "Giao dịch thành công."),
	SUSPICIOUS("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ."),
	CARD_NOT_REGISTERED("09", "Thẻ/Tài khoản chưa đăng ký InternetBanking."),
	AUTH_FAILED("10", "Xác thực thông tin thẻ/tài khoản không đúng quá 3 lần."),
	EXPIRED("11", "Đã hết hạn chờ thanh toán."),
	ACCOUNT_LOCKED("12", "Thẻ/Tài khoản bị khóa."),
	OTP_FAILED("13", "Sai mật khẩu xác thực (OTP)."),
	CANCELLED("24", "Khách hàng đã hủy giao dịch."),
	INSUFFICIENT_FUNDS("51", "Tài khoản không đủ số dư."),
	LIMIT_EXCEEDED("65", "Vượt quá hạn mức giao dịch trong ngày."),
	MAINTENANCE("75", "Ngân hàng đang bảo trì."),
	OTP_RETRY_FAILED("79", "Nhập sai mật khẩu quá số lần."),
	UNKNOWN_ERROR("99", "Lỗi không xác định."),
	UNKNOWN(null, "Không rõ mã phản hồi.");
	
	private final String code;
	private final String description;
	
	public static String getNoteByCode(String code) {
		return Arrays.stream(values())
		    .filter(v -> Objects.equals(v.code, code))
		    .findFirst()
		    .orElse(UNKNOWN)
		    .getDescription();
	}
	
	public static VnPayResponseCode fromCode(String code) {
		return Arrays.stream(values())
		    .filter(v -> Objects.equals(v.code, code))
		    .findFirst()
		    .orElse(UNKNOWN);
	}
}
