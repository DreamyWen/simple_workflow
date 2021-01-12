package com.saith.workflow.service;

public abstract class Operation<Input, Output> extends DAGNode {

    public abstract Output expand(Input input);

}
