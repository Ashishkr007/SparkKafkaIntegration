/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.riveriq.sparkkafkaintegration;

/**
 *
 * @author Ashish
 * "id": "AK0001", "Name": "Ashish", "gender": "male", "age": 28, "address":
 * "590 Veterans Avenue, Darbydale, Hawaii, 910"
 */
public class DFSchemaJavaBeans {
 
    private String id;
    private String Name;
    private String gender;
    private String age;
    private String address;

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getgender() {
        return gender;
    }

    public void setgender(String gender) {
        this.gender = gender;
    }

    public String getage() {
        return age;
    }

    public void setage(String age) {
        this.age = age;
    }

    public String getaddress() {
        return address;
    }

    public void setaddress(String address) {
        this.address = address;
    }

}
