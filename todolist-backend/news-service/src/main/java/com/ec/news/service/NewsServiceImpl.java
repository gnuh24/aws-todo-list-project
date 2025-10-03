package com.ec.news.service;

import com.ec.news.dto.NewsCreateForm;
import com.ec.news.dto.NewsUpdateForm;
import com.ec.news.entity.News;
import com.ec.news.exceptions.business.NewsContentInvalidHtmlException;
import com.ec.news.exceptions.business.NewsNotFoundException;
import com.ec.news.exceptions.business.NewsTitleAlreadyExistsException;
import com.ec.news.repository.NewsRepository;
import com.ec.news.specification.NewsSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;


@Service
public class NewsServiceImpl implements NewsService {
	
	@Autowired
	private NewsRepository newsRepository;
	
	@Override
	public Page<News> getPublishedNews(Pageable pageable) {
		return newsRepository.findAllByIsDeletedFalseAndIsPublishedTrue(pageable);
	}
	
	@Override
	public List<News> getHighlightedNews() {
		return newsRepository .findAllByHighlightTrueAndIsDeletedFalseAndIsPublishedTrueOrderByCreatedAtDesc();
	}
	
	@Override
	public News getPublicNewsById(String id) {
		return newsRepository.findByIdAndIsDeletedFalseAndIsPublishedTrue(id)
		    .orElseThrow( () -> new NewsNotFoundException(id));
	}
	
	@Override
	public News getNewsById(String id) {
		return newsRepository.findByIdAndIsDeletedFalse(id)
		    .orElseThrow(() -> new NewsNotFoundException(id));
	}
	
	
	@Override
	public Page<News> filterNewsForAdmin(String keyword, Boolean highlight, Boolean isPublished, Pageable pageable) {
		Specification<News> spec = Specification
		    .where(NewsSpecification.isNotDeleted())
		    .and(NewsSpecification.hasHighlight(highlight))
		    .and(NewsSpecification.hasTitleLike(keyword))
		    .and(NewsSpecification.hasIsPublished(isPublished));
		
		return newsRepository.findAll(spec, pageable);
	}
	
	@Override
	public void deleteNewsById(String id) {
		News news = getNewsById(id);
		
		news.setIsDeleted(true);
		news.setDeletedAt(LocalDateTime.now());
		
		newsRepository.save(news);
	}
	
	@Override
	public News createNews(NewsCreateForm form, String accountId) {
		
		// 1. Kiểm tra trùng tiêu đề
		if (newsRepository.existsByTitleAndIsDeletedFalse(form.getTitle())) {
			throw new NewsTitleAlreadyExistsException(form.getTitle());
		}
		
		// 2. Làm sạch nội dung content
		String sanitizedContent = Jsoup.clean(
		    form.getContent(),
		    Safelist.relaxed().addAttributes("img", "src", "alt", "width", "height")
		);

		
		// 3. Kiểm tra nội dung có bị nghi ngờ chứa mã độc không
		if (sanitizedContent.length() < form.getContent().length() / 2) {
			throw new NewsContentInvalidHtmlException();
		}
		
//		// 4. Validate thumbnail (nếu có)
//		if (form.getThumbnail() != null && !form.getThumbnail().isBlank()) {
//			if (!isValidThumbnailUrl(form.getThumbnail())) {
//				throw new NewsInvalidThumbnailUrlException(form.getThumbnail());
//			}
//		}
		
		// 5. Tạo entity News
		News news = News.builder()
		    .title(form.getTitle().trim())
		    .content(sanitizedContent)
		    .thumbnail(form.getThumbnail())
		    .highlight(Boolean.TRUE.equals(form.getHighlight()))
		    .isPublished(Boolean.TRUE.equals(form.getIsPublished()))
		    .isDeleted(false)
		    .createdAt(LocalDateTime.now())
		    .accountId(accountId)
		    .build();
		
		
		// 6. Lưu xuống DB
		return newsRepository.save(news);
	}
	
	@Override
	public News updateNews(String newsId, NewsUpdateForm form) {
		// 1. Tìm bài viết
		News news = newsRepository.findById(newsId)
		    .orElseThrow(() -> new NewsNotFoundException(newsId));
		
		// 2. Kiểm tra trùng tiêu đề (bỏ qua chính nó)
		if (!news.getTitle().equalsIgnoreCase(form.getTitle()) &&
		    newsRepository.existsByTitleAndIsDeletedFalse(form.getTitle())) {
			throw new NewsTitleAlreadyExistsException(form.getTitle());
		}
		
		// 3. Làm sạch content
		String sanitizedContent = Jsoup.clean(
		    form.getContent(),
		    Safelist.relaxed().addAttributes("img", "src", "alt", "width", "height")
		);
		if (sanitizedContent.length() < form.getContent().length() / 2) {
			throw new NewsContentInvalidHtmlException();
		}
		
//		// 4. Validate thumbnail nếu có
//		if (form.getThumbnail() != null && !form.getThumbnail().isBlank()) {
//			if (!isValidThumbnailUrl(form.getThumbnail())) {
//				throw new NewsInvalidThumbnailUrlException(form.getThumbnail());
//			}
//		}
		
		// 5. Cập nhật fields
		news.setTitle(form.getTitle().trim());
		news.setContent(sanitizedContent);
		news.setThumbnail(form.getThumbnail());
		news.setHighlight(Boolean.TRUE.equals(form.getHighlight()));
		news.setIsPublished(Boolean.TRUE.equals(form.getIsPublished()));
		news.setUpdatedAt(LocalDateTime.now());
		
		return newsRepository.save(news);
	}
	
	
	
}
