package com.ec.news.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewsCreateForm {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    @Size(max = 500, message = "Đường dẫn thumbnail không được vượt quá 500 ký tự")
    private String thumbnail;

    @NotNull(message = "Trạng thái nổi bật (highlight) không được để trống")
    private Boolean highlight;

    @NotNull(message = "Trạng thái công bố bài viết (isPublished) không được để trống")
    private Boolean isPublished;
}
