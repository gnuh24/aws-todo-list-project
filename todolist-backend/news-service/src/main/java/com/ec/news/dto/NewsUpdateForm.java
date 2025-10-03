package com.ec.news.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewsUpdateForm {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    private String thumbnail;

    private Boolean highlight;

    private Boolean isPublished;
}
