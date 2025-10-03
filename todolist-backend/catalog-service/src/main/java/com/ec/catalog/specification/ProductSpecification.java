package com.ec.catalog.specification;

import com.ec.catalog.dto.product.ProductFilterForm;
import com.ec.catalog.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
	
	// Dành cho người dùng cuối: chỉ lấy sản phẩm đang publish và chưa bị xóa
	public static Specification<Product> buildWhere(String search, ProductFilterForm form) {
		return Specification
		    .where(hasSearch(search))
		    .and(hasCategoryId(form.getCategoryId()))
		    .and(hasBrandId(form.getBrandId()))
		    .and(isPublished(true))
		    .and(isNotDeleted());
	}
	
	// Dành cho admin: linh hoạt theo isPublished và không loại bỏ soft-deleted nếu không cần
	public static Specification<Product> buildSpecification(String search, Boolean isPublished, String categoryId, String brandId) {
		return Specification
		    .where(hasSearch(search))
		    .and(isPublished(isPublished))
		    .and(hasCategoryId(categoryId))
		    .and(hasBrandId(brandId))
		    .and(isNotDeleted()); // Admin vẫn loại bỏ soft-delete
	}
	
	// ===== Common Specification Helpers =====
	
	public static Specification<Product> hasSearch(String search) {
		return (root, query, cb) -> {
			if (search == null || search.trim().isEmpty()) return null;
			String keyword = "%" + search.trim().toLowerCase() + "%";
			return cb.like(cb.lower(root.get("productName")), keyword);
		};
	}
	
	public static Specification<Product> isPublished(Boolean isPublished) {
		return (root, query, cb) -> {
			if (isPublished == null) return null;
			return cb.equal(root.get("isPublished"), isPublished);
		};
	}
	
	public static Specification<Product> hasCategoryId(String categoryId) {
		return (root, query, cb) -> {
			if (categoryId == null || categoryId.trim().isEmpty()) return null;
			return cb.equal(root.get("category").get("id"), categoryId);
		};
	}
	
	public static Specification<Product> hasBrandId(String brandId) {
		return (root, query, cb) -> {
			if (brandId == null || brandId.trim().isEmpty()) return null;
			return cb.equal(root.get("brand").get("id"), brandId);
		};
	}
	
	public static Specification<Product> isNotDeleted() {
		return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
	}
}
