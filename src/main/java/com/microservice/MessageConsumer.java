package com.microservice;

import com.microservice.entity.MessageValueObject;
import com.microservice.util.MessageUtil;
import com.microservice.util.TimeUtil;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class MessageConsumer implements Runnable {
    private final BlockingQueue<MessageValueObject> messagePipeline;

    private List<MessageValueObject> secondaryMessageQueue;

    private List<String> idsBeingProcessed;



    public MessageConsumer(BlockingQueue<MessageValueObject> messagePipeline, List<String> idsBeingProcessed, List<MessageValueObject> secondaryMessageQueue) {
        this.messagePipeline   = messagePipeline;
        this.idsBeingProcessed = idsBeingProcessed;
        this.secondaryMessageQueue = secondaryMessageQueue;

    }

    /**
     * this function recursively finds an available message from the main stack that is ready for consumption, while moving the messages that need to be waited on to the secondaryMessageQueue
     * @throws InterruptedException
     */
    private void processMessagePipeline() throws InterruptedException {

        if ( messagePipeline.size() > 0) {

            MessageValueObject message = messagePipeline.take();

            // check if the message can be processed at this time
            //
            if (canBeProcessed(message)) {
                idsBeingProcessed.add(message.getId());
                consumeMessage( message);

            } else {
                processMessagePipeline();
            }
        }
    }

    private void consumeMessage( MessageValueObject message){

        try {

            // time the start and the end
            //
            String startTime = TimeUtil.getCurrentTimeString();
            Thread.sleep(message.getProcessingTime().intValue());
            String endTime = TimeUtil.getCurrentTimeString();

            System.out.println( "PID:"
                    + Thread.currentThread().getId()
                    + ";\t\t"
                    + message.getAllFields()
                    + ";\t\tThread: "
                    + Thread.currentThread().getName()
                    + ";\t\tStart: "
                    + startTime
                    + ";\t\tEnd: "
                    + endTime );


            // check if there is a message in secondaryMessageQueue with the same ID that is waiting to be processed
            // if there is, it has priority over primaryMessageQueue
            if( MessageUtil.containsMessageById( secondaryMessageQueue, message ) ){

                MessageValueObject newMessage = MessageUtil.takeFirstMessageById( secondaryMessageQueue, message );

                // no need to remove from idsBeingProcessed here,
                // a message with the same id is about to start being processed
                consumeMessage( newMessage );

            }else{
                // remove from idsBeingProcessed
                idsBeingProcessed.remove( message.getId() );
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * checks if another message with the same ID is already being processed
     * if it is, parameter @message gets added to the secondaryQueue
     * @param message
     * @return
     */
    private boolean canBeProcessed(MessageValueObject message) {

        // check if another message with the same ID is already being processed
        //
        if(idsBeingProcessed.contains(message.getId())){

            // push the message to a secondaryMessageQueue for later processing
            secondaryMessageQueue.add(message);
            return false;
        }

        return true;

    }

    @Override
    public void run() {

        // loop keeps running indefinitely, since the consumer has no knowledge of when the producer will stop producing
        // The Consumer is meant to be terminated when this thread is terminated by the parent thread.
        //
        //
        // this loop could be achieved recursively within processPrimaryMessageQueue by recursively calling itself at the end of the method,
        // but for the sake of keeping the stack clean it's with a while
        //
        while(true){

            try {

                processMessagePipeline();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
