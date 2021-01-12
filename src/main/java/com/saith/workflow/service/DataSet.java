package com.saith.workflow.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author saith
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DataSet<T> {

    List<T> data;

    public DataSet<T> merge(DataSet<T> target) {
        List<T> targetList = target.getData();
        List<T> newList = new ArrayList<>();
        if (data != null) {
            newList.addAll(data);
        }
        newList.addAll(targetList);
        return new DataSet<>(newList);
    }


}
