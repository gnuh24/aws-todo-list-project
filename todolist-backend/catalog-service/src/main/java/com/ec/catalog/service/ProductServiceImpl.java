package com.ec.catalog.service;

import com.ec.catalog.dto.product.ProductCreateForm;
import com.ec.catalog.dto.product.ProductFilterForm;
import com.ec.catalog.dto.product.ProductUpdateForm;
import com.ec.catalog.dto.product.ProductVariantForm;
import com.ec.catalog.entity.*;
import com.ec.catalog.exceptions.business.brand.BrandNotFoundException;
import com.ec.catalog.exceptions.business.category.CategoryNotFoundException;
import com.ec.catalog.exceptions.business.product.ProductAlreadyExistsException;
import com.ec.catalog.exceptions.business.product.ProductNotFoundException;
import com.ec.catalog.exceptions.business.product_variant.DuplicateVariantVolumeException;
import com.ec.catalog.repository.*;
import com.ec.catalog.specification.ProductSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private BrandRepository brandRepository;
	
	@Autowired
	private ProductVariantRepository productVariantRepository;

	@Autowired
	private ProductImageRepository productImageRepository;
	
	@Override
	public Page<Product> getAllProduct(Pageable pageable, String search, ProductFilterForm form) {
		Specification<Product> where = ProductSpecification.buildWhere(search, form);
		return productRepository.findAll(where, pageable);
	}
	
	@Override
	public Page<Product> filterProductsForAdmin(String search, Boolean isPublished, String categoryId, String brandId, Pageable pageable) {
		Specification<Product> spec = ProductSpecification.buildSpecification(search, isPublished, categoryId, brandId);
		return productRepository.findAll(spec, pageable);
	}
	
	@Override
	@Transactional
	public Product createProduct(ProductCreateForm form) {
		
		// 1. Kiểm tra category/brand có tồn tại không
		Category category = categoryRepository.findByIdAndIsDeletedFalse(form.getCategoryId())
		    .orElseThrow(() -> new CategoryNotFoundException(form.getCategoryId()));
		
		Brand brand = brandRepository.findByIdAndIsDeletedFalse(form.getBrandId())
		    .orElseThrow(() -> new BrandNotFoundException(form.getBrandId()));
		
		// 2. Kiểm tra trùng productName
		if (productRepository.existsByProductNameIgnoreCaseAndIsDeletedFalse(form.getProductName())) {
			throw new ProductAlreadyExistsException(form.getProductName());
		}
		
		// 3. Kiểm tra trùng volume trong variant
		Set<Integer> volumeSet = new HashSet<>();
		for (ProductVariantForm variant : form.getVariants()) {
			if (!volumeSet.add(variant.getVolume())) {
				throw new DuplicateVariantVolumeException(variant.getVolume());
			}
		}
		
		// 4. Tạo Product
		Product product = Product.builder()
		    .productName(form.getProductName().trim())
		    .slug(toSlug(form.getProductName()))
		    .description(form.getDescription())
		    .vintage(form.getVintage())
		    .alcohol(form.getAlcohol())
		    .region(form.getRegion())
		    .isPublished(Boolean.TRUE.equals(form.getIsPublished()))
		    .isDeleted(false)
		    .category(category)
		    .brand(brand)
		    .build();
		
		// 5. Lưu Product
		productRepository.save(product);
		
		// 6. Tạo Variants
		List<ProductVariant> variants = form.getVariants().stream().map(v ->
		    ProductVariant.builder()
			.volume(v.getVolume())
			.price(v.getPrice())
			.quantity(v.getQuantity())
			.isPublished(Boolean.TRUE.equals(v.getIsPublished()))
			.isDeleted(false)
			.product(product)
			.build()
		).toList();
		productVariantRepository.saveAll(variants);
		
		// 7. Tạo Images
		List<ProductImage> images = form.getImages().stream().map(img ->
		    ProductImage.builder()
			.imageUrl(img.getImageUrl())
			.isThumbnail(Boolean.TRUE.equals(img.getIsThumbnail()))
			.isDeleted(false)
			.product(product)
			.build()
		).toList();
		productImageRepository.saveAll(images);
		
		// 8. Cập nhật productCount
		category.setProductCount(category.getProductCount() + 1);
		brand.setProductCount(brand.getProductCount() + 1);
		categoryRepository.save(category);
		brandRepository.save(brand);
		
		product.setVariants(variants);
		product.setImages(images);
		return product;
	}
	
	@Override
	@Transactional
	public Product updateProduct(String productId, ProductUpdateForm form) {
		Product product = productRepository.findById(productId)
		    .orElseThrow(() -> new ProductNotFoundException(productId));
		
		String newName = form.getProductName().trim();
		
		// Kiểm tra trùng tên (nếu đổi tên)
		if (!product.getProductName().equalsIgnoreCase(newName)
		    && productRepository.existsByProductNameIgnoreCaseAndIsDeletedFalse(newName)) {
			throw new ProductAlreadyExistsException(newName);
		}
		
		// Lấy brand/category hiện tại
		Category oldCategory = product.getCategory();
		Brand oldBrand = product.getBrand();
		
		// Lấy brand/category mới
		Category newCategory = categoryRepository.findByIdAndIsDeletedFalse(form.getCategoryId())
		    .orElseThrow(() -> new CategoryNotFoundException(form.getCategoryId()));
		
		Brand newBrand = brandRepository.findByIdAndIsDeletedFalse(form.getBrandId())
		    .orElseThrow(() -> new BrandNotFoundException(form.getBrandId()));
		
		// Nếu thay đổi category thì cập nhật productCount
		if (!oldCategory.getId().equals(newCategory.getId())) {
			oldCategory.setProductCount(Math.max(0, oldCategory.getProductCount() - 1));
			newCategory.setProductCount(newCategory.getProductCount() + 1);
			categoryRepository.save(oldCategory);
			categoryRepository.save(newCategory);
			product.setCategory(newCategory);
		}
		
		// Nếu thay đổi brand thì cập nhật productCount
		if (!oldBrand.getId().equals(newBrand.getId())) {
			oldBrand.setProductCount(Math.max(0, oldBrand.getProductCount() - 1));
			newBrand.setProductCount(newBrand.getProductCount() + 1);
			brandRepository.save(oldBrand);
			brandRepository.save(newBrand);
			product.setBrand(newBrand);
		}
		
		// Cập nhật các trường còn lại
		product.setProductName(newName);
		product.setSlug(toSlug(newName));
		product.setDescription(form.getDescription());
		product.setVintage(form.getVintage());
		product.setAlcohol(form.getAlcohol());
		product.setRegion(form.getRegion());
		product.setIsPublished(Boolean.TRUE.equals(form.getIsPublished()));
		
		return productRepository.save(product);
	}
	
	
	
	@Override
	public Product getProductById(String productId) {
		return productRepository.findById(productId)
		    .orElseThrow(() -> new EntityNotFoundException("Sản phẩm không tồn tại"));
	}
	
	@Override
	public Product getProductBySlug(String slug) {
		return productRepository.findBySlugAndIsDeletedFalseAndIsPublishedTrue(slug)
		    .orElseThrow(() -> new EntityNotFoundException("Sản phẩm không tồn tại"));
	}
	
	private  String toSlug(String input) {
		if (input == null) return null;
		
		// Loại bỏ dấu tiếng Việt
		String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
		String slug = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		
		// Chuyển về chữ thường, thay khoảng trắng bằng dấu gạch ngang
		slug = slug.toLowerCase()
		    .replaceAll("[^a-z0-9\\s-]", "") // Xoá ký tự đặc biệt
		    .replaceAll("\\s+", "-")         // Thay khoảng trắng bằng -
		    .replaceAll("-{2,}", "-")        // Gộp dấu - liên tiếp
		    .replaceAll("^-|-$", "");        // Bỏ - ở đầu/cuối (nếu có)
		
		return slug;
	}
	


//
//	@Override
//	public Product getProductById(Integer productId) {
//		return productRepository.findById(productId)
//		    .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
//	}
//
//	@Override
//	public List<Product> getVariantsByIds(List<Integer> ids) {
//		return productRepository.findAllById(ids);
//	}
	
	
//	@Override
//	public int updateDefaultBrandOfProduct(Integer brandId) {
//		return productRepository.updateBrandToDefault(brandId);
//	}
//
//	@Override
//	public int updateDefaultCategoryOfProduct(Integer categoryId) {
//		return productRepository.updateCategoryToDefault(categoryId);
//	}
//
//	@Override
//	@Transactional
//	public Product createProduct(ProductCreateForm form) {
//
//		Product entity = new Product();
//
//		entity.setProductName(form.getProductName());
//		entity.setAbv(form.getAbv());
//		entity.setOrigin(form.getOrigin());
//		entity.setCapacity(form.getCapacity());
//		entity.setDescription(form.getDescription());
//
//		Brand brand = brandService.getBrandById(form.getBrandId());
//		entity.setBrand(brand);
//
//		Category category = categoryService.getCategoryById(form.getCategoryId());
//		entity.setCategory(category);
//
//		entity.setImage(form.getImage());
//
//		entity = productRepository.save(entity);
//
//		StockLotCreateForm stockLotCreateForm = new StockLotCreateForm();
//		stockLotCreateForm.setUnitPrice(0);
//		stockLotCreateForm.setProductId(entity.getId());
//		stockLotCreateForm.setQuantity(0);
//		stockLotCreateForm.setMaxQuantity(0);
//
//		stockLotService.createStockLot(stockLotCreateForm);
//
//
//		return entity;
//	}
//
//	@Override
//	@Transactional
//	public Product updateProduct(Integer productId,
//				     ProductUpdateForm form) {
//		// Fetch the existing product from the repository
//		Product oldProduct = getProductById(productId);
//
//		// Update fields if they are not null
//		if (form.getStatus() != null) {
//			oldProduct.setStatus(form.getStatus());
//		}
//		if (form.getImage() != null && !form.getImage().isEmpty()) {
//			oldProduct.setImage(form.getImage());
//		}
//
//		if (form.getOrigin() != null) {
//			oldProduct.setOrigin(form.getOrigin());
//		}
//		if (form.getCapacity() != null) {
//			oldProduct.setCapacity(form.getCapacity());
//		}
//		if (form.getAbv() != null) {
//			oldProduct.setAbv(form.getAbv());
//		}
//		if (form.getDescription() != null) {
//			oldProduct.setDescription(form.getDescription());
//		}
//		if (form.getBrandId() != null) {
//			Brand newBrand = brandService.getBrandById(form.getBrandId());
//			oldProduct.setBrand(newBrand);
//		}
//		if (form.getCategoryId() != null) {
//			Category newCategory = categoryService.getCategoryById(form.getCategoryId());
//			oldProduct.setCategory(newCategory);
//		}
//
//		// Save the updated product
//		return productRepository.save(oldProduct);
//	}
//
	
}

