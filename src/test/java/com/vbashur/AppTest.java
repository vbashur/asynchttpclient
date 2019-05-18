package com.vbashur;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.Assert.*;

/**
 * Created by vic on 5/17/19.
 */
public class AppTest {
    @Test
    public void splitToChunks() throws Exception {
        App app = new App();
        int size = 2;
        int chunksNum = 2;
        List<List<String>> chunks = app.splitToChunks(Arrays.asList(new String[size]), chunksNum);
        assertEquals(2, chunks.size());

        size = 3;
        chunksNum = 5;
        chunks = app.splitToChunks(Arrays.asList(new String[size]), chunksNum);
        assertEquals(3, chunks.size());

        size = 999;
        chunksNum = 1;
        chunks = app.splitToChunks(Arrays.asList(new String[size]), chunksNum);
        assertEquals(1, chunks.size());

        size = 10;
        chunksNum = 3;
        chunks = app.splitToChunks(Arrays.asList(new String[size]), chunksNum);
        assertEquals(3, chunks.size());

        size = 100;
        chunksNum = 50;
        chunks = app.splitToChunks(Arrays.asList(new String[size]), chunksNum);
        assertEquals(50, chunks.size());

        size = 11;
        chunksNum = 5;
        chunks = app.splitToChunks(Arrays.asList(new String[size]), chunksNum);
        assertEquals(4, chunks.size());

        size = 12;
        chunksNum = 4;
        chunks = app.splitToChunks(Arrays.asList(new String[size]), chunksNum);
        assertEquals(4, chunks.size());

        size = 13;
        chunksNum = 4;
        chunks = app.splitToChunks(Arrays.asList(new String[size]), chunksNum);
        assertEquals(4, chunks.size());

        size = 14;
        chunksNum = 4;
        chunks = app.splitToChunks(Arrays.asList(new String[size]), chunksNum);
        assertEquals(4, chunks.size());
    }

    @Test
    public void createTestData() throws IOException {
        List<String> ids = new LinkedList<>();
        int iter = 0;
        while (iter < 10000) {
            ids.add(String.valueOf(iter));
            ++iter;
        }
        writeToFile("/home/vic/VIEW_STORE/tmp/input.txt", ids);
    }

    private void writeToFile(String filePath, List<String> content) throws IOException {
        Path file = Paths.get(filePath);
        Files.write(file, content, Charset.forName("UTF-8"));
    }
}