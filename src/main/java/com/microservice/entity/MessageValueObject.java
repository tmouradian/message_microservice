package com.microservice.entity;

import com.microservice.util.MessageUtil;

import java.io.IOException;

public class MessageValueObject {

    //can't assume one character from A-Z
    private String id;
    //simulated time to process the payload
    private Long processingTime;
    //payload type will be String
    private String payload;



    public MessageValueObject(String messageLine) throws IOException {
        parseMessageFromString( messageLine );
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Long processingTime) {
        this.processingTime = processingTime;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }


    public String getAllFields(){
        return String.format(id + "|" + processingTime + "|" + payload);
    }


    private void parseMessageFromString(String messageLine) throws IOException {

        //the delimiter character
        //
        char delimiter = '|';

        String[] fields = messageLine.split( String.format("\\" + delimiter) );

        //clean the array, when only processing time is provided you the first element empty
        //
        fields = MessageUtil.removeEmpty(fields);

        if(fields.length == 3){

            // all three are fields are present
            //
            this.id = fields[0];
            this.processingTime = Long.parseLong(fields[1]);
            this.payload = fields[2];

        }else if(fields.length == 1){

            //only processing time is provided (producer needs to sleep for specified ms)
            //
            this.processingTime = Long.parseLong(fields[0]);

        }else{
            //this shouldn't happen/wasn't specified as a case by the instructions
            throw new IOException("Unspecified number of fields in message encountered");
        }
    }

    public boolean isInvalid(){

        return this.id == null;
    }
}
