package com.saith.workflow.processor;

import com.saith.workflow.service.DataSet;
import com.saith.workflow.service.ProcessContext;
import com.saith.workflow.service.Row;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author zhangjiawen
 * @version 1.0
 * @date 2021/1/11 10:57 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MutilPlyProcessor implements Processor {

    private int id = 0;
    private int opCnt = 0;
    private String processKey;

    @Override
    public void init() {
        processKey = UUID.randomUUID().toString();
    }

    @Override
    public DataSet<Row> process(DataSet<Row> input, ProcessContext processContext) {
        System.out.println(Thread.currentThread() + String.valueOf(id) + " MutilPlyProcessor processor start opt=" + opCnt);
        if (input != null) {
            List<Row> rowList = input.getData();
            if (rowList == null) {
                rowList = new ArrayList<>();
                Row row = new Row();
                row.setId(String.valueOf(id));
                row.setData(opCnt);
                rowList.add(row);
                input.setData(rowList);
                System.out.println(Thread.currentThread() + " " + row);
            } else {
                System.out.println(Thread.currentThread() + String.valueOf(id) + " get input=" + rowList);
                int result = 0;
                for (Row row : rowList) {
                    if (row != null) {
                        Object obj = row.getData();
                        int cnt = (int) obj;
                        result += cnt;
                    }
                }
                int multiResult = opCnt * result;
                if (multiResult != 0) {
                    rowList.get(0).setData(multiResult);
                    rowList.get(0).setId(String.valueOf(id));
                }
                System.out.println(Thread.currentThread() + String.valueOf(id) + " result row=" +rowList.get(0));
            }
        }
        return input;
    }

    @Override
    public String getProcessorKey() {
        Class<?> enclosingClass = getClass().getEnclosingClass();
        String name = Objects.requireNonNullElseGet(enclosingClass, this::getClass).getName();
        return name + "-" + processKey;
    }
}
