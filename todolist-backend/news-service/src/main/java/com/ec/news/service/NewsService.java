package com.ec.news.service;

import com.ec.news.dto.NewsCreateForm;
import com.ec.news.dto.NewsUpdateForm;
import com.ec.news.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NewsService {
	Page<News> getPublishedNews(Pageable pageable);
	News getNewsById(String id);
	List<News> getHighlightedNews();
	News getPublicNewsById(String id);
	Page<News> filterNewsForAdmin(String keyword, Boolean highlight, Boolean isPublished, Pageable pageable);
	void deleteNewsById(String id);
	News createNews(NewsCreateForm form, String accountId);
	News updateNews(String newsId, NewsUpdateForm form);
	
}
