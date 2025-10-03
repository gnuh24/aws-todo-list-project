package com.ec.news.repository;

import com.ec.news.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, String>, JpaSpecificationExecutor<News> {
	
	Page<News> findAllByIsDeletedFalseAndIsPublishedTrue(Pageable pageable);
	
	List<News> findAllByHighlightTrueAndIsDeletedFalseAndIsPublishedTrueOrderByCreatedAtDesc();
	
	Optional<News> findByIdAndIsDeletedFalseAndIsPublishedTrue(String id);
	Optional<News> findByIdAndIsDeletedFalse(String id);
	boolean existsByTitleAndIsDeletedFalse(String title);
	
}
