package com.saith.workflow.processor;

import com.saith.workflow.service.DataSet;
import com.saith.workflow.service.ProcessContext;
import com.saith.workflow.service.Row;

/**
 * @author zhangjiawen
 * @version 1.0
 * @date 2021/1/11 10:57 上午
 */
public class TestProcessor implements Processor {

    @Override
    public void init() {
    }

    @Override
    public DataSet<Row> process(DataSet<Row> input, ProcessContext processContext) {
        System.out.println("begin test processor");
        if (input != null) {
            System.out.println(input);
        }
        return null;
    }

    @Override
    public String getProcessorKey() {
        return "test";
    }
}
