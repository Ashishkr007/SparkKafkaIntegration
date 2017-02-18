/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riveriq.sparkkafkaintegration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

/**
 * Consumes messages from one or more topics in Kafka and create analytics over
 * that. Usage: SparkKafkaIntegration <brokers> <topics>
 * <brokers> is a list of one or more Kafka brokers
 * <topics> is a list of one or more kafka topics to consume from
 *
 * Syntax: $ bin/run-example
 * com.riveriq.sparkkafkaintegration.SparkKafkaIntegration
 * broker1-host:port,broker2-host:port \ 
 * topic1,topic2 \ 
 * kafka-consumer-group \ 
 * no of thread 
 * 
 * Example: $ bin/spark-submit --class
 * "com.riveriq.sparkkafkaintegration.SparkKafkaIntegration" \ 
 * --master local[2] \
 * /home/riveriq/Projects/SparkKafkaIntegration/SparkKafkaIntegration-jar-with-dependencies.jar \
 * localhost:2181 test-consumer-group kafkatest 1
 *
 */
public class SparkKafkaIntegration implements Serializable {

    private static JavaStreamingContext jssc = null;
    private static SparkKafkaIntegration obj = null;
    private static String sparkCheckPoint = null;

    public static void main(String[] args) throws Exception {
        System.out.println("Main Started... ");
        if (args.length < 4) {
            System.err.println("Usage: SparkKafkaConsumer <zkQuorum> <group> <topics> <numThreads>");
            System.exit(1);
        }
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);
        obj = new SparkKafkaIntegration();
        Map<String, Integer> topicMap = obj.createStreamingContext(args[2], args[3]);
        JavaDStream<String> lines_DStream = obj.createSparkDStream(args[0], args[1], topicMap);
        CreateDataFrame obj_DF = new CreateDataFrame();
        System.out.println("calling Create Dataframe... ");
        obj_DF.CreateDataFrame(lines_DStream);
        jssc.start();
        jssc.awaitTermination();
        jssc.close();
    }

    public JavaDStream<String> createSparkDStream(String zkQuorum, String kafka_cons_group, Map<String, Integer> topicMap) {
        System.out.println("KafkaUtils Streaming started... ");
        JavaPairReceiverInputDStream<String, String> messages
                = KafkaUtils.createStream(jssc, zkQuorum, kafka_cons_group, topicMap);
        System.out.println("Creating JavaDStream...");
        JavaDStream<String> lines_DStream = messages.map(new Function<Tuple2<String, String>, String>() {
            @Override
            public String call(Tuple2<String, String> tuple2) {
                return tuple2._2();
            }
        });
        return lines_DStream;
    }

    public Map<String, Integer> createStreamingContext(String str_topics, String Threads) {
        System.out.println("creating spark streaming contaxt... ");
        SparkConf sparkConf = new SparkConf()
                .setAppName("RiverIQ_SparkKafkaInegration");
        // Create the context with 4 seconds batch size
        jssc = new JavaStreamingContext(sparkConf, new Duration(40000));
        GetConsumerConfig obj_Config = new GetConsumerConfig();
        String SparkCheckpoint = obj_Config.GetConsumerConfig("SparkCheckpoint");
        jssc.checkpoint(SparkCheckpoint);
        int numThreads = Integer.parseInt(Threads);
        Map<String, Integer> topicMap = new HashMap<String, Integer>();
        String[] topics = str_topics.split(",");
        for (String topic : topics) {
            topicMap.put(topic, numThreads);
        }
        return topicMap;
    }
}
