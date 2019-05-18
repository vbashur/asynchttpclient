package com.vbashur;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by vic on 5/17/19.
 */
public class Config {

    public static final String TARGET_URL_KEY = "input.url";
    public static final String TARGET_REQUEST_HEADER_1 = "input.request.header.1";
    public static final String TARGET_REQUEST_HEADER_2 = "input.request.header.2";
    public static final String TARGET_REQUEST_HEADER_3 = "input.request.header.3";
    public static final String TARGET_REQUEST_HEADER_4 = "input.request.header.4";
    public static final String TARGET_THREADS_NUM_KEY = "input.threads.num";
    public static final String TARGET_ITEMS_NUM_KEY = "input.items.num";
    public static final String TARGET_INPUT_FILE_KEY = "input.file";
    public static final String TARGET_OUTPUT_FILE_KEY = "output.file";
    public static final String TARGET_OUTPUT_LINE_EMPTY_KEY = "output.line.empty.placeholder";

    private String url;
    private String inputFile;
    private String outputFile;
    private Integer threadsNum;
    private Integer itemsNum;
    private String outputEmptyPlaceholder;
    private Map<String, String> requestHeaders;

    public Config(Properties props) {
        this.url = props.getProperty(TARGET_URL_KEY);
        this.inputFile = props.getProperty(TARGET_INPUT_FILE_KEY);
        this.outputFile = props.getProperty(TARGET_OUTPUT_FILE_KEY);
        this.itemsNum = Integer.valueOf(props.getProperty(TARGET_ITEMS_NUM_KEY));
        this.threadsNum = Integer.valueOf(props.getProperty(TARGET_THREADS_NUM_KEY));
        this.outputEmptyPlaceholder = props.getProperty(TARGET_OUTPUT_LINE_EMPTY_KEY);
        this.requestHeaders = initHeaderMap(props.getProperty(TARGET_REQUEST_HEADER_1),
                props.getProperty(TARGET_REQUEST_HEADER_2),
                props.getProperty(TARGET_REQUEST_HEADER_3),
                props.getProperty(TARGET_REQUEST_HEADER_4));
    }

    private Map<String,String> initHeaderMap(String... propertyPairs) {
        Map<String, String> headers = new HashMap<>();
        for (String prop : propertyPairs) {
            if (prop != null && !prop.isEmpty()) {
                String[] pair = prop.split(":");
                if (pair != null && pair.length == 2) {
                    headers.put(pair[0], pair[1]);
                }
            }
        }
        return headers;
    }

    public String getUrl() {
        return url;
    }

    public String getInputFile() {
        return inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public Integer getThreadsNum() {
        return threadsNum;
    }

    public Integer getItemsNum() {
        return itemsNum;
    }

    public Map<String, String> getRequestHeaders() {
        return this.requestHeaders;
    }

    public String getOutputEmptyPlaceholder() {
        return outputEmptyPlaceholder;
    }
}
