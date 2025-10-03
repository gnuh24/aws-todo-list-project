package com.ec.catalog.exceptions.business.category;

import javax.xml.catalog.CatalogException;

public class CategoryNotFoundException extends CatalogException {
	public CategoryNotFoundException(String id) {
		super("Không tìm thấy danh mục với ID: " + id);
	}
}
