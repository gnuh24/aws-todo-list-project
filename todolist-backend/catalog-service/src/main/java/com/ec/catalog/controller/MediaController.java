package com.ec.catalog.controller;

import com.ec.catalog.api.ApiResponse;
import com.ec.catalog.exceptions.fileException.EmptyFileUploadException;
import com.ec.catalog.exceptions.fileException.InvalidFileTypeException;
import com.ec.catalog.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/media")
@CrossOrigin(origins = "*")

public class MediaController {
	
	@Autowired
	private MediaService mediaService;
	
	@GetMapping("/{mediaId}")
	public ResponseEntity<Resource> getMedia(@PathVariable String mediaId) throws MalformedURLException {
		// Logic for fetching media
		Resource resource = mediaService.getResouceByMediaId(mediaId);
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
	}
	
	@PostMapping("/upload")
	public ResponseEntity<ApiResponse<String>> uploadMedia(@RequestParam("file") MultipartFile file) throws IOException {
		
		if (file.isEmpty()) {
			throw new EmptyFileUploadException("File upload không được để trống");
		}
		
		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new InvalidFileTypeException("Chỉ cho phép upload các định dạng ảnh như jpg, png...");
		}
		
		long maxSize = 5 * 1024 * 1024; // 5MB
		if (file.getSize() > maxSize) {
			throw new MaxUploadSizeExceededException(maxSize);
		}

		
		
		String path = mediaService.saveImage(file);
		// Logic for uploading media
		return ResponseEntity.ok(new ApiResponse<>(200, "Media uploaded successfully", path));
	}
	
	
}
