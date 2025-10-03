package com.ec.news.specification;

import com.ec.news.entity.News;
import org.springframework.data.jpa.domain.Specification;

public class NewsSpecification {
	
	public static Specification<News> hasTitleLike(String keyword) {
		return (root, query, cb) ->
		    keyword == null || keyword.isBlank()
			? null
			: cb.like(cb.lower(root.get("title")), "%" + keyword.toLowerCase() + "%");
	}
	
	public static Specification<News> hasHighlight(Boolean highlight) {
		return (root, query, cb) ->
		    highlight == null
			? null
			: cb.equal(root.get("highlight"), highlight);
	}
	
	public static Specification<News> hasIsPublished(Boolean isPublished) {
		return (root, query, cb) ->
		    isPublished == null
			? null
			: cb.equal(root.get("isPublished"), isPublished);
	}
	
	
	public static Specification<News> isNotDeleted() {
		return (root, query, cb) -> cb.isFalse(root.get("isDeleted"));
	}
}
