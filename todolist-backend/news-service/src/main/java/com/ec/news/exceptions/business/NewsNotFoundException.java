package com.ec.news.exceptions.business;

public class NewsNotFoundException extends NewsException {

    public NewsNotFoundException(String id) {
        super("Không tìm thấy bài viết với id = " + id);
    }
}
