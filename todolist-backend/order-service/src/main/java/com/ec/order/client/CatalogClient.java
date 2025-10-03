package com.ec.order.client;

import com.ec.order.api.ApiResponse;
import com.ec.order.dto.order.CheckoutItem;
import com.ec.order.dto.order.ProductVariantForOrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogClient {
	
	private final RestTemplate restTemplate;
	
	public List<ProductVariantForOrderDTO> getVariantDetails(List<CheckoutItem> rawItems) {
		List<String> ids = rawItems.stream()
		    .map(CheckoutItem::getProductVariantId)
		    .toList();
		
		String joinedIds = String.join(",", ids);
		
		String url = "http://localhost:8082/api/catalog/product-variants/full-info/by-ids?ids=" + joinedIds;
		
		ResponseEntity<ApiResponse<List<ProductVariantForOrderDTO>>> response = restTemplate.exchange(
		    url,
		    HttpMethod.GET,
		    null,
		    new ParameterizedTypeReference<ApiResponse<List<ProductVariantForOrderDTO>>>() {}
		);
		
		ApiResponse<List<ProductVariantForOrderDTO>> apiResponse = response.getBody();
		
		if (apiResponse == null || apiResponse.getData() == null || apiResponse.getData().isEmpty()) {
			throw new RuntimeException("Không lấy được thông tin variant từ Catalog Service");
		}
		
		return apiResponse.getData();
	}

	
	// ✅ API gọi giảm số lượng tồn kho
	public void reduceQuantities(List<CheckoutItem> items) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<List<CheckoutItem>> requestEntity = new HttpEntity<>(items, headers);
		
		ResponseEntity<Void> response = restTemplate.exchange(
		    "http://localhost:8082/api/catalog/product-variants/reduce-quantity", // endpoint giảm số lượng
		    HttpMethod.POST,
		    requestEntity,
		    Void.class
		);
		
		if (!response.getStatusCode().is2xxSuccessful()) {
			throw new RuntimeException("Gọi giảm số lượng tồn kho từ Catalog Service thất bại");
		}
	}
}
