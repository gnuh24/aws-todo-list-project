package com.ec.catalog.controller;

import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.dto.brand.BrandResponseDTO;
import com.ec.catalog.entity.Brand;
import com.ec.catalog.service.BrandService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
@CrossOrigin(origins = "*")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/no-paging")
    public ResponseEntity<ApiResponse<List<BrandResponseDTO>>> getAllBrandNoPaging() {
        List<Brand> list = brandService.getAllBrandNoPaging();
        List<BrandResponseDTO> dtos = modelMapper.map(list, new TypeToken<List<BrandResponseDTO>>() {}.getType());

        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Success", dtos));
    }

    // Các API CRUD có thể được thêm vào tương tự như Category
}
