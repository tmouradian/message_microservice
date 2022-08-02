package com.microservice.util;

import com.microservice.entity.MessageValueObject;
import java.util.Arrays;
import java.util.List;

public class MessageUtil {

    /**
     * removes empty strings from a string array
     * @param array
     * @return
     */
    public synchronized static String[] removeEmpty(String[] array) {
        return Arrays.stream(array)
                .filter(value ->
                        value != null && value.length() > 0
                )
                .toArray( String[]::new );
    }



    public synchronized static MessageValueObject findFirstMessageById(List<MessageValueObject> list, MessageValueObject message){
        return list.stream()
                .filter(x -> x.getId().equals(message.getId()))
                .findFirst()
                .orElse(null);
    }


    public synchronized static boolean containsMessageById(List<MessageValueObject> list, MessageValueObject message){

        return findFirstMessageById(list, message) != null;
    }


    public synchronized static MessageValueObject takeFirstMessageById(List<MessageValueObject> list, MessageValueObject message){

        MessageValueObject msg = findFirstMessageById(list, message);

        if(msg != null){
            list.remove(msg);
        }

        return msg;
    }
}
