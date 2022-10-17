package org.acme;

import javax.json.bind.annotation.JsonbProperty;

public class RequestEntity {

 @JsonbProperty("name")
 private String name;
 @JsonbProperty("age")
 private String age;
 @JsonbProperty("prefWeapon")
 private String prefWeapon;
 @JsonbProperty("specialAttack")
 private String specialAttack;

 public RequestEntity(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPrefWeapon() {
        return prefWeapon;
    }

    public void setPrefWeapon(String prefWeapon) {
        this.prefWeapon = prefWeapon;
    }

    public String getSpecialAttack() {
        return specialAttack;
    }

    public void setSpecialAttack(String specialAttack) {
        this.specialAttack = specialAttack;
    }
}