package com.ec.catalog.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MediaServiceImpl implements MediaService {
	
	@Value("${app.image.storage.path}")
	private String imageStoragePath;
	
	@Override
	public Resource getResouceByMediaId(String mediaId) throws MalformedURLException {
		Path imagePath = Paths.get(imageStoragePath, mediaId);
		return new UrlResource(imagePath.toUri());
	}
	
	@Override
	public String saveImage(MultipartFile image) throws IOException {
		
		// Kỹ thuật lấy đường dẫn tuyệt đối (Sử dụng được đối với mọi thiết bị :3 )
		String uploadDir = new File(imageStoragePath).getAbsolutePath();
		
		Path uploadDirPath = Paths.get(uploadDir);
		
		try {
			// Tạo thư mục nếu chưa tồn tại
			if (Files.notExists(uploadDirPath)) {
				Files.createDirectories(uploadDirPath);
			}
			
			// Tạo tên file duy nhất
			String fileName = Math.random() + "." + System.currentTimeMillis() + getFileExtension(image.getOriginalFilename());
			Path uploadPath = uploadDirPath.resolve(fileName);
			
			// Ghi file
			Files.write(uploadPath, image.getBytes());
			
			return fileName;
			
		} catch (IOException e) {
			// Xử lý lỗi
			System.err.println("Lỗi khi lưu ảnh: " + e.getMessage());
			return null;
		}
	}

//    public static void deleteImage(String folderPath, String imageName) {
//        String uploadDir = new File(folderPath).getAbsolutePath();
//        Path imagePath = Paths.get(uploadDir, imageName);
//
//        try {
//            // Check if the file exists
//            if (Files.exists(imagePath)) {
//                Files.delete(imagePath);
//                System.out.println("Image deleted successfully: " + imageName);
//            } else {
//                System.out.println("Image not found: " + imageName);
//            }
//        } catch (IOException e) {
//            // Handle the exception, e.g., log the error, provide a fallback, etc.
//            System.err.println("Error deleting the image: " + e.getMessage());
//        }
//    }
	
	private static String getFileExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex == -1) {
			return "";
		}
		return fileName.substring(dotIndex);
	}
}