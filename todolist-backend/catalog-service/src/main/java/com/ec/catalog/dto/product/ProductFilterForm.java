package com.ec.catalog.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterForm {

    private Boolean isPublished;
    
    private  String brandId;

    private  String categoryId;

}
