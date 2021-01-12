package com.saith.workflow.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommonUtils {

    public static List<String> readList(String fileName) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            return stream.map(String::toString).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
