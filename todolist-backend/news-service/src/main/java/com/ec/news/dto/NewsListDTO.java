package com.ec.news.dto;

import com.ec.news.entity.News;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewsListDTO {
	private String id;
	private String title;
	private String thumbnail;
	private Boolean highlight;
	private LocalDateTime createdAt;
	
	public static NewsListDTO toListDTO(News news) {
		return NewsListDTO.builder()
		    .id(news.getId())
		    .title(news.getTitle())
		    .thumbnail(news.getThumbnail())
		    .highlight(news.getHighlight())
		    .createdAt(news.getCreatedAt())
		    .build();
	}
	
}
