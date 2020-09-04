package com.example.Practice_Project.Kafka;

import com.example.Practice_Project.Entity.TransactionInES;
import com.example.Practice_Project.Reposotory.TransactionRepositoryInES;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Service
public class Consumer {
    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    private TransactionRepositoryInES transactionRepositoryInES;

    @KafkaListener(topics = "transaction", groupId = "group_id")
    public void consume(String message){
        logger.info(String.format("$$ -> Consumed Message -> %s",message));

        String[] details = message.split(",");
        TransactionInES transactionInES = new TransactionInES(details[0],details[1],details[2],details[3],details[4]);

        transactionInES.setCreatedDate(new Date());
        transactionRepositoryInES.save(transactionInES);
    }

}
