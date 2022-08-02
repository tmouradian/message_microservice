package com.microservice;

import com.microservice.entity.MessageValueObject;
import com.microservice.util.NamedThreadFactory;
import com.microservice.util.TimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;


@Service("MessageService")
public class MessageService {


    /**
     * Passes the message for processing
     * @param messageFile
     * @param maxConsumers
     * @return boolean
     */
    public boolean processMessage(MultipartFile messageFile, Long maxConsumers) {

        start(maxConsumers.intValue(), messageFile);

        return true;

    }



    public void start(int maxConsumers, MultipartFile messageFile) {

        // The main message pipeline where producer outputs the messages and the consumers consume
        //
        BlockingQueue<MessageValueObject> messagePipeline = new LinkedBlockingQueue<MessageValueObject>();


        // Global snapshot of the ids being currently consumed between threads
        //
        List<String> idsBeingProcessed = Collections.synchronizedList( new ArrayList<>());

        // Per consumer thread processing queue (not synchronized)
        //
        List<MessageValueObject> consumerMessageQueue = new ArrayList<>();


        // The one and only messageProducer
        //
        MessageProducer messageProducer = new MessageProducer(messagePipeline, messageFile );


        // create the message producing pipeline - there is only one producer but one or more consumers
        //
        Thread producerThread = new Thread(messageProducer);

        NamedThreadFactory threadFactory = new NamedThreadFactory( "MessageConsumer");
        ExecutorService messageConsumerPool = Executors.newFixedThreadPool(maxConsumers, threadFactory);

        System.out.printf("PID: %2d;  START: %s;  Consumers: %2d;  File: %s\n", Thread.currentThread().getId(), TimeUtil.getCurrentTimeString(), maxConsumers, messageFile.getOriginalFilename());


        // start single producer thread
        //
        producerThread.start();

        // start consumer threads (number specified by maxConsumers)
        //
        for(int i=0; i < maxConsumers; i++){
            messageConsumerPool.submit( new MessageConsumer(messagePipeline, idsBeingProcessed, consumerMessageQueue) );
        }



        // this loop needs to keep running until the producer and consumers are all finished
        //
        while(true){

            // check if producer is done
            if( !producerThread.isAlive() ){

                // Shutdown all consumers when Producer is done; main pipelines is empty; no items are being processed
                //
                if( messagePipeline.size() == 0 && consumerMessageQueue.size() == 0 && idsBeingProcessed.size() == 0 ) {

                    messageConsumerPool.shutdown();

                    try{
                        // wait for safe shutdown, maximum wait of 3000 millis
                        //
                        messageConsumerPool.awaitTermination(3000, TimeUnit.MILLISECONDS);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }
    }
}
