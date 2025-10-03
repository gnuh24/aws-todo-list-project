package com.ec.news.exceptions.business;

public class NewsContentInvalidHtmlException extends NewsException {
	public NewsContentInvalidHtmlException() {
		super("Nội dung bài viết chứa HTML không an toàn.");
	}
}
