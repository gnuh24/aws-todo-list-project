package com.ec.user.exceptions;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailError {
    
    @NonNull
    private String code;     	 // Mã lỗi cụ thể (ví dụ: PRD-PRD-001)
    
    @NonNull
    private String message;  	 // Mô tả chi tiết lỗi cho từng phần cụ thể
}
