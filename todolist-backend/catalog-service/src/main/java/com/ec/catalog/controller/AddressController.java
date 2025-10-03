package com.ec.catalog.controller;

import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.dto.address.AddressCreateForm;
import com.ec.catalog.dto.address.AddressResponseDTO;
import com.ec.catalog.dto.address.AddressUpdateForm;
import com.ec.catalog.entity.Account;
import com.ec.catalog.entity.Address;
import com.ec.catalog.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@CrossOrigin(origins = "*")
@Tag(name = "Address", description = "Quản lý địa chỉ người dùng")
public class AddressController {
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	// UAD001 - GET: /user/addresses/me
	@Operation(summary = "Lấy danh sách địa chỉ", description = "Trả về tất cả các địa chỉ của người dùng hiện tại")
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<List<AddressResponseDTO>>> getMyAddresses() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) auth.getPrincipal();
		
		List<Address> addresses = addressService.getAddressByProfileId(account.getProfile().getId());
		
		List<AddressResponseDTO> dtos = addresses.stream()
		    .map(address -> modelMapper.map(address, AddressResponseDTO.class))
		    .toList();
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy địa chỉ thành công", dtos));
	}
	
	// UAD002 - POST: /user/addresses/me
	@Operation(summary = "Tạo địa chỉ mới", description = "Tạo địa chỉ mới cho người dùng")
	@PostMapping("/me")
	public ResponseEntity<ApiResponse<AddressResponseDTO>> createAddress(
	    @RequestBody @Valid AddressCreateForm form) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) auth.getPrincipal();
		
		Address created = addressService.createAddress(account.getProfile(), form);
		AddressResponseDTO dto = modelMapper.map(created, AddressResponseDTO.class);
		
		return ResponseEntity.ok(new ApiResponse<>(201, "Tạo địa chỉ thành công", dto));
	}
	
	// UAD003 - PATCH: /user/addresses/{addressId}
	@Operation(summary = "Cập nhật địa chỉ", description = "Cập nhật địa chỉ người dùng")
	@PatchMapping("/{addressId}")
	public ResponseEntity<ApiResponse<AddressResponseDTO>> updateAddress(
	    @PathVariable String addressId,
	    @RequestBody @Valid AddressUpdateForm form) {
		
		Address updated = addressService.updateAddress(addressId, form);
		AddressResponseDTO dto = modelMapper.map(updated, AddressResponseDTO.class);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "UAD003 - Cập nhật địa chỉ thành công", dto));
	}
	
	// UAD004 - PATCH: /user/addresses/{addressId}/set-default
	@Operation(summary = "Đặt địa chỉ mặc định", description = "Cập nhật địa chỉ mặc định cho người dùng")
	@PatchMapping("/{addressId}/set-default")
	public ResponseEntity<ApiResponse<Void>> setDefaultAddress(@PathVariable String addressId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) auth.getPrincipal();
		
		addressService.setDefault(addressId, account.getProfile().getId());
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Đặt địa chỉ mặc định thành công", null));
	}
	
	// UAD005 - DELETE: /user/addresses/{addressId}
	@Operation(summary = "Xóa địa chỉ", description = "Xóa (mềm) một địa chỉ của người dùng")
	@DeleteMapping("/{addressId}")
	public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable String addressId) {
		addressService.deleteAddress(addressId);
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Xóa địa chỉ thành công", null));
	}
}

