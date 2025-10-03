package com.ec.news.exceptions.errorCode;

public final class NewsBusinessErrorCode {
	
	private NewsBusinessErrorCode() {}
	
	// ======= NEWS =======
	public static final String NEWS_NOT_FOUND             			= "NEWS-001"; // Không tìm thấy bài viết
	public static final String NEWS_ALREADY_DELETED       		= "NEWS-002"; // Bài viết đã bị xóa
	public static final String NEWS_INVALID_ID            			= "NEWS-003"; // ID bài viết không hợp lệ
	public static final String NEWS_TITLE_ALREADY_EXISTS  	= "NEWS-004"; // Tiêu đề bài viết đã tồn tại
	public static final String NEWS_UNAUTHORIZED_ACCESS   	= "NEWS-005"; // Không có quyền truy cập bài viết
	public static final String NEWS_CONTENT_INVALID_HTML 	= "NEWS-006"; // Nội dung chứa HTML nguy hiểm
	
}
