package com.angubaidullin.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAccountProducer {
    /*
    KafkaTemplate: Это основной компонент Spring Kafka для отправки сообщений в Kafka.
    Он похож на почтальона, который отправляет письма (сообщения) в почтовый ящик (топик).

    Spring Boot предоставляет автоматическую конфигурацию для KafkaTemplate на основе свойств,
    указанных в application.yml
     */
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
