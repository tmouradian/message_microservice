package testutil;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MockUtil {

    public static MultipartFile mockFile(String messagesTestDataPath, String filename){

        //this needs to me "file" just like the @RequestParam is in MainController.postMessage
        String mockName = "file";


        Path filepath = Paths.get(messagesTestDataPath + filename);
        String contentType = "text/plain";
        byte[] content = null;

        try {
            content = Files.readAllBytes(filepath);
        } catch (final IOException e) {
        }
        MultipartFile mockedFile = new MockMultipartFile(mockName,
                filename, contentType, content);

        return mockedFile;

    }
}
