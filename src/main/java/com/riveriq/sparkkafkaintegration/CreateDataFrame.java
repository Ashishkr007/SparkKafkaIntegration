/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riveriq.sparkkafkaintegration;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.DriverManager;
import java.util.Properties;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.DataFrameWriter;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.json.JSONObject;
import scala.collection.immutable.Map;

/**
 *
 * @author Ashish
 */
public class CreateDataFrame implements Serializable {

    public void CreateDataFrame(JavaDStream<String> lines_DStream) {
        lines_DStream.foreachRDD(new Function2<JavaRDD<String>, Time, Void>() {
            @Override
            public Void call(JavaRDD<String> rdd, Time time) {
                SQLContext sqlContext = JavaSQLContextSingleton.getInstance(rdd.context());
                CreateDataFrame obj_analyzer = new CreateDataFrame();
                System.out.println("Creating spark java bean...");
                JavaRDD<DFSchemaJavaBeans> rowRDD = obj_analyzer.createJavaBeansStockPrice(rdd);
                System.out.println("Creating dataframe from bean class...");
                DataFrame df_Bean = sqlContext.createDataFrame(rowRDD, DFSchemaJavaBeans.class);
                // Register as table
                df_Bean.show();
                System.out.println("registering dataframe as table...");
                df_Bean.registerTempTable("Employee");

                DataFrame df_FilterData
                        = sqlContext.sql("select id,name,gender,age,address from Employee");
                df_FilterData.show();
                //SaveDataFrame(df_FilterData);
                return null;
            }
        }
        );
    }

    public JavaRDD<DFSchemaJavaBeans> createJavaBeansStockPrice(JavaRDD<String> rdd) {
        JavaRDD<DFSchemaJavaBeans> rowRDD = rdd.map(new Function<String, DFSchemaJavaBeans>() {
            public DFSchemaJavaBeans call(String json_obj) {
                DFSchemaJavaBeans record = new DFSchemaJavaBeans();
                final JSONObject person = new JSONObject(json_obj);
                record.setid(person.getString("id"));
                record.setName(person.getString("Name"));
                record.setgender(person.getString("gender"));
                record.setage(person.getString("age"));
                record.setaddress(person.getString("address"));
                return record;
            }
        });
        return rowRDD;
    }
//  To Write Data frame to Mysql
    public void SaveDataFrame(DataFrame df_FilterData ) {
//        String MYSQL_USERNAME = "";
//        String MYSQL_PWD = "";
//        String MYSQL_CONNECTION_URL = "jdbc:mysql://192.168.1.1:3306/riveriq";
//        System.out.println(createRuleCSV());
//        DataFrame df_FilterData2
//                = sqlContext.sql(createRuleCSV());
//        Properties mysqlProp = new Properties();
//        mysqlProp.setProperty("user", "");
//        mysqlProp.setProperty("password", "");
//        mysqlProp.setProperty("driver", "com.mysql.jdbc.Driver");
//
//        df_FilterData2.write().mode("Append").jdbc(MYSQL_CONNECTION_URL, "tbl_riveriq", mysqlProp);
    }

}
