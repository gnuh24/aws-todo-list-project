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

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg",
        "image/png",
        "image/webp"
    );

    @Value("${app.image.storage.path}")
    private String imageStoragePath;

    @Override
    public String saveImage(MultipartFile image) {

        // 1. Validate
        if (image == null || image.isEmpty()) {
            throw new FileEmptyException(); // SYS-FILE-005
        }

        if (image.getSize() > MAX_FILE_SIZE) {
            throw new FileTooLargeException(); // SYS-FILE-001
        }

        if (!ALLOWED_TYPES.contains(image.getContentType())) {
            throw new FileUnsupportedTypeException(); // SYS-FILE-002
        }

        try {
            // 2. Ensure folder exists (absolute path, portable)
            Path uploadDir = Paths.get(
                new File(imageStoragePath).getAbsolutePath()
            );

            if (Files.notExists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 3. Generate mediaId = filename
            String extension = getFileExtension(image.getOriginalFilename());
            String mediaId = UUID.randomUUID() + extension;

            Path uploadPath = uploadDir.resolve(mediaId);

            // 4. Save file
            Files.write(uploadPath, image.getBytes());

            return mediaId;

        } catch (IOException e) {
            throw new FileUploadFailedException(); // SYS-FILE-003
        }
    }

    @Override
    public Resource getResourceByMediaId(String mediaId)
            throws MalformedURLException {

        Path imagePath = Paths.get(
            new File(imageStoragePath).getAbsolutePath(),
            mediaId
        );

        if (Files.notExists(imagePath)) {
            throw new FileNotFoundException(); // SYS-FILE-004
        }

        return new UrlResource(imagePath.toUri());
    }

    @Override
    public void deleteByMediaId(String mediaId) {

        Path imagePath = Paths.get(
            new File(imageStoragePath).getAbsolutePath(),
            mediaId
        );

        try {
            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
            } else {
                throw new FileNotFoundException();
            }
        } catch (IOException e) {
            throw new FileUploadFailedException();
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? "" : fileName.substring(dotIndex);
    }
}
