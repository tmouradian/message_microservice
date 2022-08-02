import com.microservice.MessageProducer;
import com.microservice.entity.MessageValueObject;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;
import testutil.MockUtil;
import testutil.TestUtil;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.fail;



public class ProducerTest {

    private String messagesTestDataPath = "./testdata/messageData/";

    @Test
    public void producerTest(){

        String filename = "messages.txt";
        MultipartFile input = MockUtil.mockFile(messagesTestDataPath, filename);

        final BlockingQueue<MessageValueObject> messagePipeline = new LinkedBlockingQueue<>();


        MessageProducer messageProducer = new MessageProducer(messagePipeline, input );

        Thread producerThread = new Thread(messageProducer);
        System.out.println("Starting Producer run...");
        producerThread.run();

        while(producerThread.isAlive()){
            //keep the test running until producer is finished
        }

        try {
            if(messagePipeline.size() != TestUtil.getValidMessageCount(input)){
                fail("full file has not been parsed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.printf("Producer finished successfully; Messages produced: %2d\n", messagePipeline.size());

    }
}





