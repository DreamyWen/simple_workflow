package com.saith.workflow.service;

public class PBegin extends DAGNode {
    public PBegin() {
        this.id = 0;
        this.name = "start";
        this.type = "Operation";
    }
}
