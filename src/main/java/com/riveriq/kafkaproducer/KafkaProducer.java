/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riveriq.kafkaproducer;

import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.JSONArray;
import org.json.JSONObject;

/**
* This producer will send a bunch of messages to topic "kafkatest" by reading it from JSON file. 
 * First it will read JSON file >
 * split it to JSON Elements >
 * put it to arraylist >
 * convert each element to JSON object.
 * it will send a message to "kafkatest".
 * 
 * This can send messages to multiple topics also. for that you need to pass topic name by "," separated.
 */

/**
 * Run Command :
 * java -cp /home/riveriq/Projects/SparkKafkaIntegration/SparkKafkaIntegration-jar-with-dependencies.jar:/etc/kafka_2.10-0.8.2.0/libs/* com.riveriq.kafkaproducer.KafkaProducer JSON
 * 
 */
public class KafkaProducer {

    private static Map<String, Object> config;
  
    public static void main(String[] args) throws Exception {
        String FileType = args[0].toString();
        if ("JSON".equals(FileType)) {
            SendFeedToKafka obj_kafka = new SendFeedToKafka();
            System.out.println("initializing kafka config....");
            obj_kafka.initialize();
            System.out.println("locading input file....");
            String jsons = obj_kafka.loadInputFile();
            System.out.println("Complete JSON input....");
            System.out.println(jsons);
            System.out.println("Splitting file to jsons....");
            List<String> splittedJsons = obj_kafka.split(jsons);
            System.out.println("converting to JsonDocuments....");
            int docCount = splittedJsons.size();
            System.out.println("number of documents is: " + docCount);
            System.out.println("sending msg to kafka....");
            obj_kafka.sendtotopic(splittedJsons);
           
        }
    }
}
