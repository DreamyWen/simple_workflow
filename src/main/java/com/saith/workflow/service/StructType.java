package com.saith.workflow.service;

import lombok.Data;

@Data
public class StructType {

//    Schema schema;
//    name: String,
//    dataType: DataType,
//    nullable: Boolean = true,
//    metadata: Metadata = Metadata.empty

    String name;
    String dataType;
    Boolean nullable;

}
