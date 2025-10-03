package com.ec.catalog.specification;

import com.ec.catalog.entity.Brand;
import org.springframework.data.jpa.domain.Specification;

public class BrandSpecification {
	public static Specification<Brand> searchByName(String keyword) {
		return (root, query, cb) -> {
			if (keyword == null || keyword.trim().isEmpty()) {
				return cb.isFalse(root.get("isDeleted"));
			}
			return cb.and(
			    cb.isFalse(root.get("isDeleted")),
			    cb.like(cb.lower(root.get("brandName")), "%" + keyword.trim().toLowerCase() + "%")
			);
		};
	}
}
