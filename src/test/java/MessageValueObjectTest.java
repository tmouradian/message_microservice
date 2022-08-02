import com.microservice.entity.MessageValueObject;
import org.junit.Test;
import org.springframework.web.multipart.MultipartFile;
import testutil.MockUtil;
import testutil.TestUtil;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class MessageValueObjectTest {


    private String messagesTestDataPath = "./testdata/messageData/";



    @Test
    public void MessageValueObjectTest(){

        String filename = "messages.txt";
        MultipartFile input = MockUtil.mockFile(messagesTestDataPath, filename);

        try {

            List<String> messageLines= TestUtil.getMessageLines(input);

            System.out.println("Parsing message lines...");
            for(int i=0; i<messageLines.size(); i++){

                MessageValueObject mvo = new MessageValueObject(messageLines.get(i));

                System.out.printf("Validating message {%s}\n", mvo.getAllFields());
                TestUtil.doMessageValidation( mvo );
                System.out.printf("Done Validating message {%s}\n\n", mvo.getAllFields());

            }
            System.out.println("All message Lines parsed successfully.\n");


        } catch (IOException e) {
            fail(e.getMessage());
        }

    }


    @Test
    public void parseMessageWithTimeoutTest() {

        String messageLine = "A|400|a-05";
        String messageLineTimeout = "|500|";

        try {

            MessageValueObject mvo = new MessageValueObject(messageLine);
            MessageValueObject mvoWithTimeout = new MessageValueObject(messageLineTimeout);

            //do basic content for both messages
            TestUtil.doMessageValidation(mvoWithTimeout);
            TestUtil.doMessageValidation(mvo);


            //isInvalid returns true if the message doesn't include the id (|500|)
            if( mvo.isInvalid() ){
                fail("A valid message was parsed as invalid in MessageValueObject");
            }
            if( !mvoWithTimeout.isInvalid() ){
                fail("An invalid message was parsed as valid in MessageValueObject");
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
