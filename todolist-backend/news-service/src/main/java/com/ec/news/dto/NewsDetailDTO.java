package com.ec.news.dto;

import com.ec.news.entity.News;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class NewsDetailDTO {
	private String id;
	private String title;
	private String content;
	private String thumbnail;
	private Boolean highlight;
	private LocalDateTime createdAt;
	private List<String> images;

	public static NewsDetailDTO toDetailDTO(News news) {
		return NewsDetailDTO.builder()
				.id(news.getId())
				.title(news.getTitle())
				.content(news.getContent())
				.thumbnail(news.getThumbnail())
				.highlight(news.getHighlight())
				.createdAt(news.getCreatedAt())
				.build();
	}
}
