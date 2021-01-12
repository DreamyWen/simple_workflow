package com.saith.workflow.service;

import lombok.Data;

@Data
public class DataObj {
    public Object data;

    public static DataObj of(Object obj) {
        DataObj dataObj = new DataObj();
        dataObj.setData(obj);
        return dataObj;
    }


}
