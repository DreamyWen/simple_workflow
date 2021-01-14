package com.saith.workflow.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author saith
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Row implements Serializable {

    StructType structType;
    String id;
    Object data;
}
