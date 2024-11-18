package com.angubaidullin.service.kafka;

import com.angubaidullin.dto.KafkaDTO;
import com.angubaidullin.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaAccountConsumer {
    private final AccountService accountService;
    private final KafkaAccountProducer kafkaAccountProducer;

    /*
    @KafkaListener: Эта аннотация делает метод "слушателем" Kafka. Он будет реагировать на сообщения,
    которые приходят в топик account_requests

    topics = "account_requests": Указывает, что метод будет слушать сообщения из топика account_requests

    groupId = "app-group": Все потребители с этим groupId делят сообщения между собой
    (в данном случае только один потребитель обрабатывает сообщение).

    String message: Сообщение, полученное из Kafka.
     */
    @KafkaListener(topics = "account_requests", groupId = "app-group")
    public void listenAccountRequests(String message) {
        //Если сообщение равно "fetch_accounts", метод начнет обработку
        if ("fetch_accounts".equals(message)) {
            List<KafkaDTO> allForKafka = accountService.getAllForKafka();
            allForKafka.stream()
                    .forEach( kafkaDTO ->
                            kafkaAccountProducer.sendMessage("account_responses", kafkaDTO.toString()));
        }
    }
}
