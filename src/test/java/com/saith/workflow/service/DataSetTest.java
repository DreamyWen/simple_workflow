package com.saith.workflow.service;

import com.beust.jcommander.JCommander;
import com.saith.workflow.pojo.Person;
import com.saith.workflow.pojo.TestInnerPojo;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author zhangjiawen
 * @version 1.0
 * @date 2021/1/15 11:09 上午
 */
public class DataSetTest {

    @Test
    public void testDataSet() {
        TestInnerPojo inner = new TestInnerPojo();
        inner.setName("inner");
        inner.setSex(1);
        inner.setAge(30);
        inner.setSchool(null);
        inner.setHobby(new String[] { "摄影1", "旅行1", "家居", "做饭" });
        inner.setPlace(Lists.newArrayList("北京1","深圳","广州","北海"));

        Person p = new Person();
        p.setName("Akili");
        p.setSex(1);
        p.setAge(24);
        p.setSchool(null);
        p.setHobby(new String[] { "摄影", "旅行", "家居", "做饭" });
        p.setPlace(Lists.newArrayList("北京","深圳","广州","北海"));
        p.setInnerPojo(inner);


        Object obj = p;
        Row row = new Row();
        row.setId("1");
        row.setData(obj);

        System.out.println(obj.getClass());

//        Object someObject = obj;
//        for (Field field : someObject.getClass().getDeclaredFields()) {
//            field.setAccessible(true); // You might want to set modifier to public first.
//            Object value = null;
//            try {
//                value = field.get(someObject);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            if (value != null) {
//                System.out.println(field.getName() + " " + value + " "+ field.getType());
//            }
//        }
        row.showDataType();
    }

}
