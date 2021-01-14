package com.saith.workflow.processor;

import com.saith.workflow.service.DataSet;
import com.saith.workflow.service.ProcessContext;
import com.saith.workflow.service.Row;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author zhangjiawen
 * @version 1.0
 * @date 2021/1/11 10:57 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeginProcessor implements Processor {

    private static final String KEY = UUID.randomUUID().toString();
    private Integer id;

    @Override
    public void init() {
    }

    @Override
    public DataSet<Row> process(DataSet<Row> input, ProcessContext processContext) {
//        System.out.println(Thread.currentThread() + " begin processor start");
        if (input != null) {
            List<Row> rowList = input.getData();
            if (rowList == null) {
                rowList = new ArrayList<>();
            }
            Row row = new Row();
            row.setData(id);
            rowList.add(row);
            input.setData(rowList);
            System.out.println(Thread.currentThread() + " " + row);
        }
//        System.out.println(Thread.currentThread() + " begin processor end ");
        return input;
    }

    @Override
    public String getProcessorKey() {
        return "begin_" + KEY;
    }
}
