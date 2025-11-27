package aws.todolist.api_gateway.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailError {
    
    @NonNull
    private String code;     	 // Mã lỗi cụ thể (ví dụ: PRD-PRD-001)
    
    @NonNull
    private String message;  	 // Mô tả chi tiết lỗi cho từng phần cụ thể
}
