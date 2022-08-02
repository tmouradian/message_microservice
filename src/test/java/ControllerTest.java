import com.microservice.MessageMicroservice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.fail;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes=MessageMicroservice.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllerTest {


    @Autowired
    WebApplicationContext webContext;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String messagesTestDataPath = "testdata/messageData/";

    private int maxConsumers = 5;

    @Test
    public void postMessageFileTest() {

        postMessage("messages.txt", maxConsumers );

    }

    @Test
    public void postMessageFileSameIdTest() {

        postMessage("messages_sameid.txt", maxConsumers );
    }

    @Test
    public void postMessageFileComplexTest() {

        postMessage("messages_complex.txt", maxConsumers );

    }


    @Test
    public void postMessageFileWithTimeouts() {

        // this just has many timeouts
        postMessage("messages_withtimeout.txt", maxConsumers );

    }


    private void postMessage(String filename, int maxConsumers){

        //creating the endpoint url
        final String baseUrl = "http://localhost:" + port + "/interview/process-file/" + Integer.toString(maxConsumers);

        //creating the file to be posted
        FileSystemResource testFile = new FileSystemResource(messagesTestDataPath + filename);


        //request configuration
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("file", testFile);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);


        //making the request, it will return type void
        ResponseEntity<Void> response = restTemplate
                .postForEntity(baseUrl, requestEntity, Void.class);


        int statusCode = response.getStatusCode().value();


        if( statusCode != 200 ){
            fail( String.format("response with status code %2d", statusCode) );
        }else{
            System.out.println(String.format("Request successfully finished with status code %2d", statusCode));
        }
    }


}
