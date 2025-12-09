package aws.todolist.taskflow.utils;

import org.springframework.stereotype.Component;

import java.net.URISyntaxException;


@Component
public class S3Utils {

    public String getKeyFromUrl(String fileUrl) throws URISyntaxException {
        return fileUrl.substring(fileUrl.indexOf(".com/") + 5);
    }
}
