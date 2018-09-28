package com.flt.model;

/**
 * @author fenglingtong
 * @date 2017/12/5
 */
public class Person {

    /**
     * 名字
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    public Person(){
        super();
    }

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

}
