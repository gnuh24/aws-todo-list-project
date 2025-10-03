package com.ec.order.exceptions;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
	
	@NonNull
	private Integer status;         // HTTP status (vd: 400, 401, 500)
	
	@NonNull
	private String code;           // Mã lỗi chính (dành cho FE tra tài liệu & xử lý logic)
	
	@NonNull
	private String message;         // Thông báo chung (cho người dùng)
	
	private String detailMessage;   // Mô tả chi tiết kỹ thuật (cho dev)
	
	private List<DetailError> errors;   // Danh sách lỗi cụ thể (mỗi lỗi có code & message)

}
