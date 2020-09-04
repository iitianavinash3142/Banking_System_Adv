package com.example.Practice_Project.Kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class ProducerWithAsync{
    private static final String TOPIC = "transaction";

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Async
    public void sendMessage(String message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, message);
        future.addCallback(new ListenableFutureCallback() {
            @Override
            public void onSuccess(Object result) {
                // left empty intentionally
            }
            @Override
            public void onFailure(final Throwable throwable) {
                // left empty intentionally

            }
        });
    }
}
