package com.ec.user.controller;

import com.ec.user.api.ApiResponse;
import com.ec.user.dto.profile.ProfileDetailResponseDTO;
import com.ec.user.dto.profile.ProfileUpdateForm;
import com.ec.user.entity.Account;
import com.ec.user.entity.Profile;
import com.ec.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@CrossOrigin(origins = "*")
@Tag(name = "Profile", description = "Quản lý thông tin hồ sơ người dùng")
public class ProfileController {
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private ModelMapper modelMapper;
	

//	@Operation(summary = "Lấy danh sách profile", description = "Lấy tất cả các profile với phân trang, tìm kiếm và lọc")
//	@GetMapping
//	public ResponseEntity<ApiResponse<Page<ProfileDetailResponseDTO>>> getAllProfiles(
//	    Pageable pageable,
//	    @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(required = false) String search,
//	    ProfileFilterForm filterForm) {
//
//		Page<Profile> profiles = profileService.getAllProfile(pageable, search, filterForm);
//
//		List<ProfileDetailResponseDTO> dtos = modelMapper.map(
//		    profiles.getContent(),
//		    new TypeToken<List<ProfileDetailResponseDTO>>() {}.getType()
//		);
//
//		Page<ProfileDetailResponseDTO> profileDTOs = new PageImpl<>(dtos, pageable, profiles.getTotalElements());
//
//		return ResponseEntity.ok(new ApiResponse<>(200, "Danh sách profile lấy thành công", profileDTOs));
//	}
	
	@Operation(summary = "Lấy chi tiết profile theo ID", description = "Chỉ admin hoặc chính người dùng mới có thể xem thông tin profile")
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<ProfileDetailResponseDTO>> getProfileById() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();
		
//		if (account.getRole() == Account.Role.ADMIN) {
//			ProfileDetailResponseDTO profile = modelMapper.map(profileService.getProfileById(profileId), ProfileDetailResponseDTO.class);
//			return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin profile thành công", profile));
//		}
		
//		if (!account.getId().equals(profileId)) {
//			throw new AccessDeniedException("Bạn không có quyền truy cập thông tin người khác");
//		}
		
		ProfileDetailResponseDTO profile = modelMapper.map(account.getProfile(), ProfileDetailResponseDTO.class);
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin profile thành công", profile));
	}

//	@Operation(summary = "Tạo profile mới", description = "Tạo mới một hồ sơ người dùng")
//	@PostMapping
//	public ResponseEntity<ApiResponse<ProfileDetailResponseDTO>> createProfile(
//	    @RequestBody @Valid ProfileCreateForm form) {
//
//		Profile newProfile = profileService.createProfile(form);
//		ProfileDetailResponseDTO responseDTO = modelMapper.map(newProfile, ProfileDetailResponseDTO.class);
//
//		return ResponseEntity.status(HttpStatus.CREATED)
//		    .body(new ApiResponse<>(201, "Tạo Profile thành công", responseDTO));
//	}

	@Operation(summary = "Cập nhật profile cá nhân", description = "Cập nhật thông tin hồ sơ của người dùng đang đăng nhập")
	@PatchMapping("/me")
	public ResponseEntity<ApiResponse<ProfileDetailResponseDTO>> updateProfile(
	    	@RequestBody @Valid ProfileUpdateForm form) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Account account = (Account) authentication.getPrincipal();

		Profile updatedProfile = profileService.updateProfile(account.getProfile(), form);
		ProfileDetailResponseDTO responseDTO = modelMapper.map(updatedProfile, ProfileDetailResponseDTO.class);

		return ResponseEntity.ok(
		    new ApiResponse<>(200, "Cập nhật profile thành công", responseDTO)
		);
	}
}
