package com.ec.news.exceptions.business;

public class NewsTitleAlreadyExistsException extends NewsException {
	public NewsTitleAlreadyExistsException(String title) {
		super("Tiêu đề bài viết \"" + title + "\" đã tồn tại.");
	}
}
