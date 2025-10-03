package com.ec.catalog.exceptions.errorCode;

public final class CatalogBusinessErrorCode {
	
	private CatalogBusinessErrorCode() {
	}
	
	// ======= PRODUCT =======
	public static final String CAT_PRODUCT_NOT_FOUND           = "CAT-PRODUCT-001"; // Không tìm thấy sản phẩm
	public static final String CAT_PRODUCT_INACTIVE            = "CAT-PRODUCT-002"; // Sản phẩm đã bị vô hiệu hóa
	public static final String CAT_PRODUCT_ALREADY_EXISTS      = "CAT-PRODUCT-003"; // Tên sản phẩm đã tồn tại
	
	// ======= PRODUCT VARIANT =======
	public static final String CAT_VARIANT_NOT_FOUND           = "CAT-VARIANT-001"; // Không tìm thấy phiên bản sản phẩm
	public static final String CAT_VARIANT_QUANTITY_NOT_ENOUGH = "CAT-VARIANT-002"; // Không đủ số lượng trong kho
	public static final String CAT_VARIANT_INVALID_REQUEST     = "CAT-VARIANT-003"; // Dữ liệu yêu cầu không hợp lệ
	public static final String CAT_VARIANT_ALREADY_DISABLED    = "CAT-VARIANT-004"; // Phiên bản đã ngưng kinh doanh
	public static final String CAT_VARIANT_DUPLICATE_VOLUME    = "CAT-VARIANT-005"; // Trùng dung tích các phiên bản
	
	public static final String CAT_IMAGE_NOT_FOUND = "CAT-IMAGE-001"; // Không tìm thấy ảnh
	public static final String CAT_IMAGE_ALREADY_DELETED = "CAT-IMAGE-002"; // Ảnh đã bị xóa
	
	
	// ======= CATEGORY =======
	public static final String CAT_CATEGORY_NOT_FOUND = "CAT-CATEGORY-001"; // Không tìm thấy danh mục
	public static final String CAT_CATEGORY_ALREADY_EXISTS = "CAT-CATEGORY-002"; // Tên danh mục đã tồn tại
	public static final String CAT_CATEGORY_INVALID_ID = "CAT-CATEGORY-003"; // ID danh mục không hợp lệ
	public static final String CAT_CATEGORY_ALREADY_DELETED = "CAT-CATEGORY-004"; // Danh mục đã bị xóa
	
	// ======= BRAND =======
	public static final String CAT_BRAND_NOT_FOUND           = "CAT-BRAND-001"; // Không tìm thấy thương hiệu
	public static final String CAT_BRAND_ALREADY_EXISTS      = "CAT-BRAND-002"; // Tên thương hiệu đã tồn tại
	public static final String CAT_BRAND_INVALID_ID          = "CAT-BRAND-003"; // ID thương hiệu không hợp lệ
	public static final String CAT_BRAND_ALREADY_DELETED     = "CAT-BRAND-004"; // Thương hiệu đã bị xóa

	
}
