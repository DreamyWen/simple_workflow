package com.saith.workflow.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author saith
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Row {

    StructType structType;
    String id;
    Object data;
}
