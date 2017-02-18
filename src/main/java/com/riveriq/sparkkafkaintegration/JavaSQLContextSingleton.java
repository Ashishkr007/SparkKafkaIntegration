/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riveriq.sparkkafkaintegration;

import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;

/**
 *
 * @author Ashish
 */
public class JavaSQLContextSingleton {

    public static transient SQLContext instance = null;

    public static SQLContext getInstance(SparkContext sparkContext) {
        if (instance == null) {
            instance = new SQLContext(sparkContext);
        }
        return instance;
    }
}