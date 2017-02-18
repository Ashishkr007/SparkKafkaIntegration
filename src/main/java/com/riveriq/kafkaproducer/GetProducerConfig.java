/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riveriq.kafkaproducer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Ashish
 */
public class GetProducerConfig {

    private static String ConfigPropertyValue = null;
    
    //Read kafkaProducer.properties file
    public String GetProducerConfig(String ConfigPropertyKey) {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("/home/riveriq/Projects/SparkKafkaIntegration/kafkaProducer.properties");
            //load a properties file
            prop.load(input);
            ConfigPropertyValue = prop.getProperty(ConfigPropertyKey);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ConfigPropertyValue;
    }
}
