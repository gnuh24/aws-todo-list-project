package com.ec.catalog.dto.brand;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BrandResponseDTO {

    private String id;
    private String brandName;
    private Integer productCount;

//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private Boolean isDeleted;
}
