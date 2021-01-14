package com.saith.workflow.processor;

import com.saith.workflow.service.DataSet;
import com.saith.workflow.service.ProcessContext;
import com.saith.workflow.service.Row;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @author zhangjiawen
 * @version 1.0
 * @date 2021/1/11 10:57 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultiPlyProcessor implements Processor {

    private int id = 0;
    private int opCnt = 0;
    private String processKey;

    @Override
    public void init() {
        processKey = UUID.randomUUID().toString();
    }

    @Override
    public DataSet<Row> process(DataSet<Row> input, Map<String, DataSet<Row>> otherInput, ProcessContext processContext) {
        System.out.println(Thread.currentThread() + String.valueOf(id) + " MutilPlyProcessor processor start opt=" + opCnt);
        int counter = 0;
        if (otherInput != null) {
            for (Map.Entry<String, DataSet<Row>> entry : otherInput.entrySet()) {
                List<Row> otherRowList = entry.getValue().getData();
                if (otherRowList != null && otherRowList.size() > 0) {
                    Row row = otherRowList.get(0);
                    int dataCnt = (int) row.getData();
                    counter += dataCnt;
                    System.out.println(Thread.currentThread() + String.valueOf(id) + " MutilPlyProcessor otherInput" + row);
                }
            }
        }
        int baseCnt = counter;
        System.out.println("base Cnt=" + baseCnt);
        if (input != null) {
            List<Row> rowList = input.getData();
            if (rowList == null) {
                rowList = new ArrayList<>();
                Row row = new Row();
                row.setId(String.valueOf(id));
                if (counter != 0) {
                    row.setData(counter * opCnt);
                } else {
                    row.setData(opCnt);
                }
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
                int multiResult = (result + counter) * opCnt;
                if (multiResult != 0) {
                    rowList.get(0).setData(multiResult);
                    rowList.get(0).setId(String.valueOf(id));
                }
                System.out.println(Thread.currentThread() + String.valueOf(id) + " result row=" + rowList.get(0));
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
