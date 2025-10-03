package com.ec.news.dto;

import com.ec.news.entity.News;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewsDetailDTOForAdmin {
	private String id;
	private String title;
	private String content;
	private String thumbnail;
	private Boolean highlight;
	private Boolean isPublished;
	private Boolean isDeleted;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	public static NewsDetailDTOForAdmin toDTO(News news) {
		return NewsDetailDTOForAdmin.builder()
			.id(news.getId())
			.title(news.getTitle())
			.content(news.getContent())
			.thumbnail(news.getThumbnail())
			.highlight(news.getHighlight())
			.isPublished(news.getIsPublished())
			.isDeleted(news.getIsDeleted())
			.createdAt(news.getCreatedAt())
			.updatedAt(news.getUpdatedAt())
			.deletedAt(news.getDeletedAt())
			.build();
	}
}
