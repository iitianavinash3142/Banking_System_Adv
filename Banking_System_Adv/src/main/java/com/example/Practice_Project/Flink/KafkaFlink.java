package com.example.Practice_Project.Flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;


import java.util.List;
import java.util.Properties;


public class KafkaFlink {


    public static FlinkKafkaConsumer011<String> createStringConsumerForTopic(
            String topic, String kafkaAddress, String kafkaGroup ) {

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", kafkaAddress);
        props.setProperty("group.id",kafkaGroup);
        FlinkKafkaConsumer011<String> consumer = new FlinkKafkaConsumer011<>(
                topic, new SimpleStringSchema(), props);

        System.out.println("Reached here 1");
        return consumer;
    }

    public static void getMessage() {
        String inputTopic = "transaction";
        String address = "localhost:9092";
        String consumerGroup = "group_id";
        StreamExecutionEnvironment environment = StreamExecutionEnvironment
                .getExecutionEnvironment();
        FlinkKafkaConsumer011<String> flinkKafkaConsumer = createStringConsumerForTopic(
                inputTopic, address, consumerGroup);
        DataStream<String> stringInputStream = environment
                .addSource(flinkKafkaConsumer);

        System.out.println("Reached here 2");
        stringInputStream.map (s -> s + " Kafka and Flink ").print();
        System.out.println("Reached here 3");
    }

}
