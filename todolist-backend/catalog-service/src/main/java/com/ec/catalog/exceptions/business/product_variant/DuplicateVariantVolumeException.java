package com.ec.catalog.exceptions.business.product_variant;

import com.ec.catalog.exceptions.business.product.ProductException;

public class DuplicateVariantVolumeException extends ProductException {
    public DuplicateVariantVolumeException(int volume) {
        super("Tồn tại nhiều phiên bản sản phẩm với cùng dung tích: " + volume + "ml.");
    }
}
