package com.saith.workflow.service;

import com.saith.workflow.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author saith
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DataSet<T> implements Serializable {

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

    public DataSet<Row> from(String id) {
        Row row = new Row();
        row.setId(id);
        row.setData(id);
        List<Row> rowList = new ArrayList<>();
        rowList.add(row);
        return new DataSet<>(rowList);
    }

    @Override
    public DataSet<T> clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return SerializationUtils.clone(this);
    }

}
