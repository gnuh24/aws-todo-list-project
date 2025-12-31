package aws.todolist.media.service;

import aws.todolist.media.exceptionHandler.exceptions.*;
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
import java.util.Set;
import java.util.UUID;

@Service
public class LocalMediaServiceImpl implements MediaService {
	
	/* =======================
	 * FILE SIZE CONFIG
	 * ======================= */
	private static final long IMAGE_MAX_SIZE = 4 * 1024 * 1024;   // 4MB
	private static final long FILE_MAX_SIZE  = 20 * 1024 * 1024;  // 20MB
	
	/* =======================
	 * IMAGE WHITELIST
	 * ======================= */
	private static final Set<String> IMAGE_CONTENT_TYPES = Set.of(
		"image/jpeg",
		"image/png",
		"image/webp"
	);
	
	private static final Set<String> IMAGE_EXTENSIONS = Set.of(
		"jpg", "jpeg", "png", "webp"
	);
	
	/* =======================
	 * BLACKLIST (SECURITY)
	 * ======================= */
	private static final Set<String> BLACKLIST_EXTENSIONS = Set.of(
		"exe", "bat", "cmd", "sh",
		"js", "php", "jsp", "py",
		"jar", "dll",
		"docm", "xlsm"
	);
	
	@Value("${app.image.storage.path}")
	private String imageStoragePath;
	
	/* =======================
	 * SAVE FILE
	 * ======================= */
	@Override
	public String saveImage(MultipartFile file) {
		
		// 1. Empty check
		if (file == null || file.isEmpty()) {
			throw new FileEmptyException(); // SYS-FILE-005
		}
		
		String extension = getFileExtension(file.getOriginalFilename());
		
		// 2. Blacklist extension (HIGH PRIORITY)
		if (BLACKLIST_EXTENSIONS.contains(extension)) {
			throw new FileUnsupportedTypeException(); // SYS-FILE-002
		}
		
		boolean isImage = isImage(file, extension);
		
		// 3. Size validation
		if (isImage && file.getSize() > IMAGE_MAX_SIZE) {
			throw new FileTooLargeException(); // SYS-FILE-001
		}
		
		if (!isImage && file.getSize() > FILE_MAX_SIZE) {
			throw new FileTooLargeException(); // SYS-FILE-001
		}
		
		try {
			// 4. Ensure folder exists
			Path uploadDir = Paths.get(
				new File(imageStoragePath).getAbsolutePath()
			);
			
			if (Files.notExists(uploadDir)) {
				Files.createDirectories(uploadDir);
			}
			
			// 5. Generate mediaId
			String mediaId = UUID.randomUUID() + "." + extension;
			Path uploadPath = uploadDir.resolve(mediaId);
			
			// 6. Save file
			Files.write(uploadPath, file.getBytes());
			
			return mediaId;
			
		} catch (IOException e) {
			throw new FileUploadFailedException(); // SYS-FILE-003
		}
	}
	
	/* =======================
	 * GET FILE
	 * ======================= */
	@Override
	public Resource getResourceByMediaId(String mediaId)
		throws MalformedURLException {
		
		Path filePath = Paths.get(
			new File(imageStoragePath).getAbsolutePath(),
			mediaId
		);
		
		if (Files.notExists(filePath)) {
			throw new FileNotFoundException(); // SYS-FILE-004
		}
		
		return new UrlResource(filePath.toUri());
	}
	
	/* =======================
	 * DELETE FILE
	 * ======================= */
	@Override
	public void deleteByMediaId(String mediaId) {
		
		Path filePath = Paths.get(
			new File(imageStoragePath).getAbsolutePath(),
			mediaId
		);
		
		try {
			if (Files.exists(filePath)) {
				Files.delete(filePath);
			} else {
				throw new FileNotFoundException(); // SYS-FILE-004
			}
		} catch (IOException e) {
			throw new FileUploadFailedException(); // SYS-FILE-003
		}
	}
	
	/* =======================
	 * HELPER METHODS
	 * ======================= */
	private boolean isImage(MultipartFile file, String extension) {
		return IMAGE_CONTENT_TYPES.contains(file.getContentType())
			&& IMAGE_EXTENSIONS.contains(extension);
	}
	
	private String getFileExtension(String fileName) {
		if (fileName == null) return "";
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex == -1) return "";
		return fileName.substring(dotIndex + 1).toLowerCase();
	}
}
