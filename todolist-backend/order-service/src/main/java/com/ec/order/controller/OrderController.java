package com.ec.order.controller;

import com.ec.order.api.ApiResponse;
import com.ec.order.dto.order.*;
import com.ec.order.entity.*;
import com.ec.order.integration.vnpay.VNPAYService;
import com.ec.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order", description = "Quản lý đơn hàng người dùng")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private VNPAYService vnpayService;
	
	@GetMapping("/my-orders")
	public ResponseEntity<ApiResponse<Page<MyOrderResponseDTO>>> getMyOrders(Pageable pageable) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) auth.getPrincipal();
		
		Page<Order> orderPage = orderService.getMyOrders(account.getId(), pageable);
		
		List<MyOrderResponseDTO> dtoList = orderPage.getContent().stream().map(order -> {
			OrderStatus.OrderStatusEnum latestStatus = order.getStatuses().stream()
			    .sorted((a, b) -> b.getUpdateTime().compareTo(a.getUpdateTime()))
			    .findFirst()
			    .map(OrderStatus::getStatus)
			    .orElse(null);
			
			List<OrderDetailDTO> orderDetails = order.getOrderDetails().stream()
			    .map(detail -> OrderDetailDTO.builder()
				.productVariantId(detail.getProductVariantId())
				.productName(detail.getProductName())
				.productVolume(detail.getProductVolume())
				.productThumbnail(detail.getProductThumbnail())
				.unitPrice(detail.getUnitPrice())
				.quantity(detail.getQuantity())
				.build())
			    .collect(Collectors.toList());
			
			return MyOrderResponseDTO.builder()
			    .id(order.getId())
			    .totalAmount(order.getTotalAmount())
			    .orderTime(order.getOrderTime())
			    .latestStatus(latestStatus)
			    .orderDetails(orderDetails)
			    .build();
		}).collect(Collectors.toList());
		
		Page<MyOrderResponseDTO> resultPage = new PageImpl<>(
		    dtoList,
		    pageable,
		    orderPage.getTotalElements()
		);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách đơn hàng thành công", resultPage));
	}
	
	@GetMapping("/my-orders/{orderId}")
	public ResponseEntity<ApiResponse<OrderDetailResponseDTO>> getOrderDetail(@PathVariable String orderId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) auth.getPrincipal();
		
		Order order = orderService.getOrderDetailById(orderId, account.getId());
		
		// Convert OrderDetails
		List<OrderDetailDTO> orderDetails = order.getOrderDetails().stream()
		    .map(detail -> OrderDetailDTO.builder()
			.productVariantId(detail.getProductVariantId())
			.productName(detail.getProductName())
			.productThumbnail(detail.getProductThumbnail())
			.productVolume(detail.getProductVolume())
			.unitPrice(detail.getUnitPrice())
			.quantity(detail.getQuantity())
			.totalPrice(detail.getTotalPrice())
			.build())
		    .toList();
		
		// Convert Order Status History
		List<OrderStatusDTO> statusHistory = order.getStatuses().stream()
		    .map(status -> OrderStatusDTO.builder()
			.status(status.getStatus())
			.updateTime(status.getUpdateTime())
			.build())
		    .toList();
		
		// Convert Payment (chỉ lấy 1 payment duy nhất, giả định có 1 payment cho 1 order)
		Payment payment = order.getPayments() != null && !order.getPayments().isEmpty()
		    ? order.getPayments().get(0)
		    : null;
		
		PaymentDTO paymentDTO = null;
		if (payment != null) {
			paymentDTO = PaymentDTO.builder()
			    .id(payment.getId())
			    .paymentStatus(payment.getPaymentStatus())
			    .paymentMethod(payment.getPaymentMethod())
			    .build();
		}
		
		// Build final DTO
		OrderDetailResponseDTO responseDTO = OrderDetailResponseDTO.builder()
		    .id(order.getId())
		    .totalAmount(order.getTotalAmount())
		    .note(order.getNote())
		    .orderTime(order.getOrderTime())
		    .receiverName(order.getReceiverName())
		    .receiverPhone(order.getReceiverPhone())
		    .receiverAddress(order.getReceiverAddress())
		    .orderDetails(orderDetails)
		    .statusHistory(statusHistory)
		    .payment(paymentDTO)
		    .build();
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy chi tiết đơn hàng thành công", responseDTO));
	}
	
	@PostMapping("/check-out/cod")
	public ResponseEntity<ApiResponse<String>> checkoutCOD(@RequestBody CheckoutRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		String orderId = orderService.createOrderWithCOD(account.getId(), request);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Đặt hàng thành công (COD)", orderId));
	}
	
	@PostMapping("/check-out/vnpay")
	public ResponseEntity<ApiResponse<OrderCreateResponseDTO>> createOrderWithVnPay(
	    @RequestBody CheckoutRequest request,
	    HttpServletRequest servletRequest
	) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
		Order order = orderService.createOrderAndGenerateVnPayUrl(account.getId(), request);
		
		// 4. Tạo URL thanh toán qua VNPAY
		String baseUrl = servletRequest.getScheme() + "://" + servletRequest.getServerName() + ":" + servletRequest.getServerPort();
		String vnpUrl = vnpayService.createOrder(servletRequest, order.getTotalAmount(), order.getId(), baseUrl);
		
		// 5. Trả về DTO
		OrderCreateResponseDTO response = new OrderCreateResponseDTO();
		response.setId(order.getId());
		response.setTotalAmount(order.getTotalAmount());
		response.setVnpUrl(vnpUrl);
		response.setReceiverName(request.getReceiverName());
		response.setReceiverPhone(request.getReceiverPhone());
		response.setReceiverAddress(request.getReceiverAddress());
		
		return ResponseEntity.status(HttpStatus.CREATED)
		    .body(new ApiResponse<>(201, "Tạo đơn hàng thành công", response));
	}
	
	@GetMapping("/vnpay-payment-return")
	public ResponseEntity<ApiResponse<OrderCreateResponseDTO>> vnpayReturn(HttpServletRequest request) {
		System.err.println("===== [VNPAY RETURN] Tham số trả về =====");
		request.getParameterMap().forEach((key, value) ->
		    System.err.println(key + " = " + String.join(", ", value))
		);
		
		String orderId = request.getParameter("vnp_OrderInfo");
		String transactionId = request.getParameter("vnp_TransactionNo");
		String paymentTime = request.getParameter("vnp_PayDate");
		String responseCode = request.getParameter("vnp_ResponseCode");
		String transactionStatusCode = request.getParameter("vnp_TransactionStatus");
		String secureHash = request.getParameter("vnp_SecureHash");
		String bankCode = request.getParameter("vnp_BankCode");
		String cardType = request.getParameter("vnp_CardType");
		
		// Parse Enums
		VnPayResponseCode responseEnum = VnPayResponseCode.fromCode(responseCode);
		VnPayTransactionStatusCode transactionEnum = VnPayTransactionStatusCode.fromCode(transactionStatusCode);

		
		// Parse time
		LocalDateTime parsedPaymentTime = LocalDateTime.parse(paymentTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		
		// Get Status Enum (map về PaymentStatus)
		Payment.PaymentStatus paymentStatus = vnpayService.getPaymentStatus(responseCode);
		
		// Create form
		VnPayPaymentCreateForm form = VnPayPaymentCreateForm.builder()
		    .orderId(orderId)
		    .transactionId(transactionId)
		    .paymentTime(parsedPaymentTime)
		    .vnpSecureHash(secureHash)
		    .bankCode(bankCode)
		    .cardType(cardType)
		    .vnpResponseCode(responseEnum)
		    .vnpTransactionStatusCode(transactionEnum)
		    .vnpResponseStatus(responseEnum.getDescription())
		    .vnpTransactionStatus(transactionEnum.getDescription())
		    .paymentStatus(paymentStatus)
		    .build();
		
		Order order = orderService.processVnPayReturn(form);
		OrderCreateResponseDTO dto = modelMapper.map(order, OrderCreateResponseDTO.class);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Xử lý thanh toán VNPAY thành công", dto));
	}
	
	
	
}
