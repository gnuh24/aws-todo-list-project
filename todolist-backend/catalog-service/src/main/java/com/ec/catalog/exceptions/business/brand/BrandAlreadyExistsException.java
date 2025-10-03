package com.ec.catalog.exceptions.business.brand;

public class BrandAlreadyExistsException extends BrandException {
    public BrandAlreadyExistsException(String name) {
        super("Thương hiệu '" + name + "' đã tồn tại.");
    }
}
