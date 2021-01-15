package com.saith.workflow.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Field;


/**
 * @author saith
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Row implements Serializable {

//    TODO 在DAG的某一个节点，使用者应该了解其类型
//    StructType structType;

    String id;
    Object data;

    public <T> T getAsObject(final Class<T> clazz) {
        return clazz.cast(data);
    }

    public Integer getAsInt() {
        return (Integer) data;
    }

    public Boolean getAsBoolean() {
        return (Boolean) data;
    }

    public Byte getAsByte() {
        return (Byte) data;
    }

    public Character getAsChar() {
        return (Character) data;
    }

    public Short getAsShort() {
        return (Short) data;
    }

    public Double getAsDouble() {
        return (Double) data;
    }

    public Float getAsFloat() {
        return (Float) data;
    }

    public Long getAsLong() {
        return (Long) data;
    }

    public Long getDataType() {
        return (Long) data;
    }

    public String getAsString() {
        return (String) data;
    }

    public void showDataType() {
        if (data == null) {
            return;
        }
        Object someObject = data;
        for (Field field : someObject.getClass().getDeclaredFields()) {
            // You might want to set modifier to public first.
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(someObject);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value != null) {
                System.out.println(field.getName() + " " + value + " " + field.getType());
            }
        }
    }
}
