package aws.todolist.media.service;

import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import org.springframework.core.io.Resource;

public interface MediaService {

    String saveImage(MultipartFile file);

    Resource getResourceByMediaId(String mediaId) throws MalformedURLException;

    void deleteByMediaId(String mediaId);
}
