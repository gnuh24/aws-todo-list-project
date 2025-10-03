package com.ec.news.controller;

import com.ec.news.api.ApiResponse;
import com.ec.news.dto.*;
import com.ec.news.entity.Account;
import com.ec.news.entity.News;
import com.ec.news.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {
	
	@Autowired
	private NewsService newsService;
	
	@GetMapping("/home")
	public ResponseEntity<ApiResponse<Page<NewsListDTO>>> getPublishedNews(Pageable pageable) {
		Page<News> entities = newsService.getPublishedNews(pageable);
		Page<NewsListDTO> result = entities.map(NewsListDTO::toListDTO);
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách bài viết thành công", result));
	}
	
	@GetMapping("/highlight")
	public ResponseEntity<ApiResponse<List<NewsListDTO>>> getHighlightedNews() {
		List<News> entities = newsService.getHighlightedNews();
		List<NewsListDTO> result = entities.stream().map(NewsListDTO::toListDTO).toList();
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy danh sách bài viết nổi bật thành công", result));
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<NewsDetailDTO>> getNewsDetail(@PathVariable String id) {
		News entity = newsService.getPublicNewsById(id);
		NewsDetailDTO result = NewsDetailDTO.toDetailDTO(entity);
		return ResponseEntity.ok(new ApiResponse<>(200, "Lấy chi tiết bài viết thành công", result));
	}
	
	
	
	
	
}
