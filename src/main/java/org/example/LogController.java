package org.example;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private MessageRepository messageRepository;


    @PostMapping
    public ResponseEntity<List<Message>> saveMessage(@RequestBody String messageContent) {
        // Limpia el contenido del mensaje eliminando los saltos de línea y retorno de carro
        String cleanedMessageContent = messageContent.replaceAll("[\\r\\n]+", " ").trim();

        Message message = new Message();
        message.setContent(cleanedMessageContent);
        message.setDate(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        messageRepository.save(message);

        List<Message> messages = messageRepository.findTop10ByOrderByDateDesc();
        return ResponseEntity.ok(messages);
    }
}