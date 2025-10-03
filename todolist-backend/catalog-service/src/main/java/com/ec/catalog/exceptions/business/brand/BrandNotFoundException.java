package com.ec.catalog.exceptions.business.brand;

public class BrandNotFoundException extends BrandException {
    public BrandNotFoundException(String id) {
        super("Không tìm thấy thương hiệu với ID: " + id);
    }
}
