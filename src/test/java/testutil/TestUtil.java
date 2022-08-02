package testutil;

import com.microservice.entity.MessageValueObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.fail;

public class TestUtil {



    //checks if the message was parsed correctly
    public static void doMessageValidation(MessageValueObject msg){
        if(msg.getProcessingTime() == null){
            fail(String.format("Processing time in message {%s} is null, it should never be null", msg.getAllFields()));
        }
        if(msg.getId() == null && msg.getPayload() != null){
            fail(String.format("Id is null and payload isn't in message {%s}. If one is null the other should also be null", msg.getAllFields()));
        }
        if(msg.getPayload() == null && msg.getId() != null){
            fail(String.format("Payload is null and id isn't in message {%s}. If one is null the other should also be null", msg.getAllFields()));
        }
    }


    public static int getValidMessageCount(MultipartFile file) throws IOException {

        int count = 0;
        Scanner sc = new Scanner(new String( file.getBytes(), StandardCharsets.UTF_8));

        while(sc.hasNextLine()){

            String line = sc.nextLine();
            MessageValueObject mvo = new MessageValueObject(line);
            if( !mvo.isInvalid() ){
                count++;
            }
        }
        // close scanner
        sc.close();


        return count;
    }


    public static List<String> getMessageLines(MultipartFile file) throws IOException {
        List<String> lines = new ArrayList<>();

        Scanner sc = new Scanner(new String( file.getBytes(), StandardCharsets.UTF_8));

        while(sc.hasNextLine()){
            lines.add(sc.nextLine());
        }
        // close scanner
        sc.close();

        return lines;
    }


}
