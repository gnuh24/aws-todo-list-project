package aws.todolist.taskflow.controller;

import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.service.AwsService;
import io.jsonwebtoken.io.IOException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/s3bucketstorage")
public class AwsController {

    @Autowired
    private AwsService service;


    /**
     * Upload file vào folder temp
     * Trả về URL file trên S3
     */
    @PostMapping("/uploadTemp")
    @SneakyThrows(IOException.class)
    public ResponseEntity<ApiResponse<String>> uploadTempFile(@RequestParam("file") MultipartFile[] files) throws java.io.IOException {
        if (files.length > 1) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Chỉ được upload 1 file", null));
        }

        MultipartFile file = files[0];

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.NOT_ACCEPTABLE.value(), "File bị rỗng", null));
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        long fileSize = file.getSize();
        String contentType = file.getContentType();
        InputStream inputStream = file.getInputStream();

        // Upload lên S3
        String url = service.uploadFile(originalFileName, fileSize, contentType, inputStream);


        return ResponseEntity.ok()
                .body(new ApiResponse<>(HttpStatus.OK.value(), "File được lưu trữ", url));


    }


    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        service.deleteFile(fileUrl);
        return ResponseEntity.ok(
                new ApiResponse<>(HttpStatus.OK.value(), "Xoá file thành công", fileUrl)
        );

    }
}
