package com.saith.workflow.utils;

import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 常见工具
 *
 * @author saith
 * @date 2021/01/13
 */
public class CommonUtils {

    public static List<String> readList(String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            return stream.map(String::toString).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static <T extends Serializable> T cloneObject(T object) {
        return SerializationUtils.clone(object);
    }

}
