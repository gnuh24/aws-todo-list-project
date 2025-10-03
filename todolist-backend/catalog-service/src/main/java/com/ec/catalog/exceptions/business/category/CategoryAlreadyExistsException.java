package com.ec.catalog.exceptions.business.category;

import com.ec.catalog.exceptions.errorCode.CatalogBusinessErrorCode;

import javax.xml.catalog.CatalogException;

public class CategoryAlreadyExistsException extends CatalogException {
	public CategoryAlreadyExistsException(String name) {
		super("Tên danh mục '" + name + "' đã tồn tại");
	}
}
