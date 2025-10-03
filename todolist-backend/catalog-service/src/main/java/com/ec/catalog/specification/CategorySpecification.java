package com.ec.catalog.specification;

import com.ec.catalog.entity.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {
	
	public static Specification<Category> searchByName(String search) {
		return (root, query, cb) -> {
			if (search == null || search.trim().isEmpty()) {
				return cb.isFalse(root.get("isDeleted")); // Chỉ lọc isDeleted = false
			}
			return cb.and(
			    cb.isFalse(root.get("isDeleted")),
			    cb.like(cb.lower(root.get("categoryName")), "%" + search.trim().toLowerCase() + "%")
			);
		};
	}
}
