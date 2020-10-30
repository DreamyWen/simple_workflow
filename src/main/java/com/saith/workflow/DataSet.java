package com.saith.workflow;

import lombok.Data;

@Data
public class DataSet {
    public Object data;

    public static DataSet of(Object obj) {
        DataSet dataSet = new DataSet();
        dataSet.setData(obj);
        return dataSet;
    }
}
