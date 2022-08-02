package com.microservice;

import com.microservice.entity.MessageValueObject;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import static java.lang.Thread.currentThread;


/**
 *
 *
 *
 */
public class MessageProducer implements Runnable {

    private final BlockingQueue<MessageValueObject> messagePipeline;
    private final MultipartFile messageFile;



    /**
     * Message Producer is a singular thread producer that parses out data file and populates global message pipeline
     *
     *
     *
     * @param messagePipeline
     * @param messageFile
     */
    public MessageProducer( BlockingQueue<MessageValueObject> messagePipeline, MultipartFile messageFile ) {
        this.messagePipeline = messagePipeline;
        this.messageFile = messageFile;
    }


    /**
     *
     * Main method of the thread:
     *
     * The logic is as follows: for every line of incoming data file we try to produce a MessageValueObject
     * Each line of the file is treated as a separate line with no knowledge when the file ends
     *
     * The pipeline is seeded with 2 potential Value Objects
     *
     * 1) Valid Message
     *
     * If MessageValueObject is present with all values then it is in a valid state and
     * we place it into the message pipeline for further processing
     *
     * 2) Invalid/Poisonous Message
     *
     * If Value Object is missing both ID and Payload then we stop processing for the specified timeout period
     *
     */
    public void run() {

        try {

            Scanner scanner = new Scanner( new String( messageFile.getBytes(), StandardCharsets.UTF_8));

            // Process incoming data line by line
            //
            while (scanner.hasNextLine()) {

                String line = scanner.nextLine();

                MessageValueObject message = new MessageValueObject( line );

                if (message.isInvalid()){

                    // is Message is missing an ID then the producing thread will sleep for the specified time
                    //
                    currentThread().sleep( message.getProcessingTime() );
                }
                else {
                    messagePipeline.put(message);
                }
            }

            scanner.close();


        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();  // set interrupt flag
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

}
