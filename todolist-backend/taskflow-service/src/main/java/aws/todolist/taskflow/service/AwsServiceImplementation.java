package aws.todolist.taskflow.service;

import aws.todolist.taskflow.enums.FileType;
import aws.todolist.taskflow.service.ServiceInterface.AwsService;
import aws.todolist.taskflow.utils.S3Utils;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.UUID;

@Service
public class AwsServiceImplementation implements AwsService {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Utils s3Utils;


    @Value("${aws.s3.bucket-name}")
    private String defaultBucketName;

    @Override
    public String uploadFile(String originalFileName, Long contentLength, String contentType, InputStream data) {
        try {

            String tempKey = "temp/" + UUID.randomUUID() + "-" + originalFileName;
            MediaType mediaType = FileType.fromFilename(originalFileName);

//            TODO: Test logic trước nào deploy thì mở ra
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(defaultBucketName)
                    .key(tempKey)
                    .contentLength(contentLength)
                    .contentType(mediaType.toString())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(data, contentLength));


            System.out.printf("File uploaded to bucket(%s): %s%n", defaultBucketName, tempKey);

            // Tạo URL trả về (S3 URL chuẩn)

            return "https://" + defaultBucketName + ".s3.amazonaws.com/" + tempKey;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    @Override
    public String moveFileToAttach(String tempUrl) throws URISyntaxException {
        // 1. Lấy tempKey từ URL tạm
        String tempKey = s3Utils.getKeyFromUrl(tempUrl); // vd: temp/abc123-photo.png

        // 2. Tạo attachKey bằng cách thay prefix temp/ -> attach/
        String attachKey = tempKey.replace("temp/", "attach/");

        // TODO: Để test logic trước

        // 3. Copy object trong S3
        s3Client.copyObject(builder -> builder
                .sourceBucket(defaultBucketName)
                .sourceKey(tempKey)
                .destinationBucket(defaultBucketName)
                .destinationKey(attachKey)
                .build());

//        // 4. Xóa file temp
        deleteFile(tempUrl);

        System.out.printf("File moved from %s to %s in bucket %s%n", tempKey, attachKey, defaultBucketName);

        return "https://" + defaultBucketName + ".s3.amazonaws.com/" + attachKey;
    }


    @Override
    public void deleteFile(String fileUrl) {

        try {
            // Lấy key từ URL
            String key = s3Utils.getKeyFromUrl(fileUrl);

            // Xóa object trong S3 (bucket mặc định)
            s3Client.deleteObject(builder -> builder
                    .bucket(defaultBucketName) // bucket mặc định
                    .key(key)
                    .build());

            System.out.printf("File deleted from bucket(%s): %s%n", defaultBucketName, key);
        } catch (Exception e) {
            System.err.printf("Failed to delete file from bucket(%s): %s, error: %s%n", defaultBucketName, fileUrl, e.getMessage());
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    public ByteArrayResource downloadFile(String url) throws URISyntaxException {


        String key = s3Utils.getKeyFromUrl(url);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(defaultBucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);

        byte[] bytes;
        try {
            bytes = s3Object.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error reading S3 object");
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayResource(bytes);
    }

    private String extractFileName(String key) {
        return key.substring(key.lastIndexOf("/") + 1);
    }


}
