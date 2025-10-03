package com.ec.news.dto;

import com.ec.news.entity.News;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewsListDTOForAdmin {
	
	private String id;
	private String title;
	private String thumbnail;
	private Boolean highlight;
	private Boolean isPublished;
	private LocalDateTime createdAt;
	
	public static NewsListDTOForAdmin toListDTO(News news) {
		return NewsListDTOForAdmin.builder()
		    .id(news.getId())
		    .title(news.getTitle())
		    .thumbnail(news.getThumbnail())
		    .highlight(news.getHighlight())
		    .createdAt(news.getCreatedAt())
		    .isPublished(news.getIsPublished())
		    .build();
	}
	
}
