package com.saith.workflow;

public abstract class Operation<Input, Output> extends DAGNode {

    public abstract Output expand(Input input);

}
