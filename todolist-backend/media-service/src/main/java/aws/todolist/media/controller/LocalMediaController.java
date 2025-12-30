package aws.todolist.media.controller;

import aws.todolist.media.api.ApiResponse;
import aws.todolist.media.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

@RestController
@RequestMapping("/v1/local")
@RequiredArgsConstructor
public class LocalMediaController {

    private final MediaService mediaService;
	
	/**
	 * Upload image/file to local storage
	 * POST /media/v1/local/uploads
	 */
	@PostMapping(
		value = "/uploads",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<ApiResponse<String>> upload(
		@RequestParam("file") MultipartFile file
	) {
		String mediaId = mediaService.saveImage(file);
		
		ApiResponse<String> response = new ApiResponse<>(
			HttpStatus.OK.value(),
			"Upload file successfully",
			mediaId
		);
		
		return ResponseEntity.ok(response);
	}
	
	
	/**
     * Get image/file by mediaId
     * GET /media/v1/local/{mediaId}
     */
    @GetMapping("/{mediaId}")
    public ResponseEntity<Resource> getByMediaId(
        @PathVariable String mediaId
    ) throws MalformedURLException {

        Resource resource = mediaService.getResourceByMediaId(mediaId);

        return ResponseEntity.ok()
            .contentType(resolveContentType(mediaId))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + mediaId + "\""
            )
            .body(resource);
    }

    /**
     * (Optional) Delete media
     * DELETE /media/v1/local/{mediaId}
     */
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> delete(
        @PathVariable String mediaId
    ) {
        mediaService.deleteByMediaId(mediaId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Resolve content-type from file extension
     */
    private MediaType resolveContentType(String fileName) {
        String lower = fileName.toLowerCase();

        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }
        if (lower.endsWith(".webp")) {
            return MediaType.valueOf("image/webp");
        }
        if (lower.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
