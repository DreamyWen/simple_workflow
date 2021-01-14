package com.saith.workflow.processor;

import com.saith.workflow.service.DataSet;
import com.saith.workflow.service.ProcessContext;
import com.saith.workflow.service.Row;

import java.util.Map;

/**
 * @author zhangjiawen
 * @version 1.0
 * @date 2021/1/11 10:57 上午
 */
public interface Processor {

    /**
     * 初始化
     */
    void init();

    /**
     * 过程
     *
     * @param input          主输入
     * @param otherInput     旁路输入
     * @param processContext 进程上下文
     * @return {@link DataSet<Row>}
     */
    DataSet<Row> process(final DataSet<Row> input, final Map<String, DataSet<Row>> otherInput, ProcessContext processContext);

    /**
     * 得到处理器Key
     *
     * @return {@link String}
     */
    String getProcessorKey();

}
