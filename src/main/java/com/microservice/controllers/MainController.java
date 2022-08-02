package com.microservice.controllers;

import com.microservice.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@Controller
@RequestMapping("/interview")
public class MainController {

    @Resource(name = "MessageService")
    private MessageService messageService;


    /**
     * Post request send message
     * @param maxConsumers
     * @return
     */
    @PostMapping("/process-file/{maxConsumers}")
    @ResponseStatus(code = HttpStatus.OK, reason = "OK")
    public void postMessage(@RequestParam("file") MultipartFile messageFile, @PathVariable Long maxConsumers) {

        messageService.processMessage(messageFile, maxConsumers);
    }


}
