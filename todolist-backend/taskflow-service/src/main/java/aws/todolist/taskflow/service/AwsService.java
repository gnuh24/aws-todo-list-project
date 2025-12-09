package aws.todolist.taskflow.service;

import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;
import java.net.URISyntaxException;

public interface AwsService {

    // Method to upload a file to an S3 bucket
    String uploadFile(String originalFileName, Long contentLength, String contentType, InputStream data);


    // Method to delete a file from an S3 bucket
    void deleteFile(String url);

    // Chuyển file từ temp sang attach
    String moveFileToAttach(String tempUrl) throws URISyntaxException;

    ByteArrayResource downloadFile(String url) throws URISyntaxException;
}
