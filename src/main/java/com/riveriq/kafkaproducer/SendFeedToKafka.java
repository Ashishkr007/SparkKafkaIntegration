/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riveriq.kafkaproducer;

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
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Ashish
 */
public class SendFeedToKafka {

    private static Map<String, Object> config;
    private static KafkaProducer<String, String> producer;
    //initial kafka producer
    public void initialize() {
        config = new HashMap<String, Object>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.1:6667");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    }

    //read JSON input file
    public String loadInputFile() throws IOException {
        producer = new KafkaProducer<String, String>(config);
        GetProducerConfig obj_Config = new GetProducerConfig();
        //reading property from kafkaProducer.properies file   
        String ProdecerInputFile = obj_Config.GetProducerConfig("ProdecerInputFile");
        File input = new File(ProdecerInputFile);
        byte[] encoded = Files.readAllBytes(Paths.get(input.getPath()));
        String jsons_string = new String(encoded, Charset.defaultCharset());
        return jsons_string;
    }

    //Split JSON string into JSON objects
    public static List<String> split(String jsonArray) throws Exception {
        List<String> splittedJsonElements = new ArrayList<String>();
        String sJSON = jsonArray;
        JSONArray jsonArray2 = new JSONArray(sJSON);
        for (int i = 0; i < jsonArray2.length(); i++) {
            JSONObject item = jsonArray2.getJSONObject(i);
            splittedJsonElements.add(item.toString());
            System.out.println(item.toString());
        }
        return splittedJsonElements;
    }

    //send each JSON object to kafka topic
    public void sendtotopic(List<String> splittedJsons) {
        int count = 0;
        for (String doc : splittedJsons) {
            System.out.println("sending msg...." + count);
            GetProducerConfig obj_Config = new GetProducerConfig();
            String KafkaTopic = obj_Config.GetProducerConfig("KafkaTopic");
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(KafkaTopic, doc);
            Future<RecordMetadata> meta = producer.send(record);
            System.out.println("Message sent successfully");
            System.out.println("msg sent...." + count);
            count++;
        }
        System.out.println("Total of " + count + " messages sent");

    }
}
