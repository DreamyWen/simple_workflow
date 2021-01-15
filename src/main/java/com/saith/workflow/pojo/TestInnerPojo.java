package com.saith.workflow.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 人
 *
 * @author saith
 * @date 2021/01/15
 */
@Data
public class TestInnerPojo implements Serializable {//实体
 
    /**
     * 
     */
    private static final long serialVersionUID = 3193754045080382621L;
 
    private String            name;
    private Integer           sex;
    private Integer           age;
    private String            school;
    private String[]          hobby;
    private List<String> place;
 
}