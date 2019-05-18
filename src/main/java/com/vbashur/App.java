package com.vbashur;

import org.apache.commons.collections4.ListUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class App {



    private static final Logger LOG = Logger.getLogger(App.class.getName());


    public static void main(String[] args) throws IOException {

        if (args == null || args.length != 1) {
            StringBuilder errorMessage = new StringBuilder("Incorrect number of arguments. You must specify a property file");
            errorMessage.append("\n");
            errorMessage.append("Property file must contain following keys");
            errorMessage.append("\n");
            errorMessage.append(Config.TARGET_URL_KEY + "=");
            errorMessage.append("\n");
            errorMessage.append(Config.TARGET_INPUT_FILE_KEY + "=");
            errorMessage.append("\n");
            errorMessage.append(Config.TARGET_OUTPUT_FILE_KEY + "=");
            errorMessage.append("\n");
            errorMessage.append(Config.TARGET_THREADS_NUM_KEY + "=");
            errorMessage.append("\n");
            errorMessage.append(Config.TARGET_ITEMS_NUM_KEY + "=");
            errorMessage.append("\n");
            errorMessage.append(Config.TARGET_OUTPUT_LINE_EMPTY_KEY + "=");
            errorMessage.append("\n");
            errorMessage.append(Config.TARGET_REQUEST_HEADER_1 + "=");
            errorMessage.append("\n");
            errorMessage.append(Config.TARGET_REQUEST_HEADER_2 + "=");
            errorMessage.append("\n");
            errorMessage.append(Config.TARGET_REQUEST_HEADER_3 + "=");
            errorMessage.append("\n");
            errorMessage.append(Config.TARGET_REQUEST_HEADER_4 + "=");
            errorMessage.append("\n");

            LOG.log(Level.SEVERE, errorMessage.toString());
            return;
        }
        LOG.log(Level.INFO, "Started");
        App app = new App();
        Instant start = Instant.now();
        try {

            app.doRun(args[0]);

        } catch (ExecutionException | InterruptedException e) {
            LOG.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
        Instant end = Instant.now();
        LOG.log(Level.INFO, "Done");
        LOG.log(Level.INFO, "Execution took: " + Duration.between(start, end).getSeconds() + " milliseconds");
    }

    public void doRun(String pathToConfig) throws IOException, ExecutionException, InterruptedException {
        Config cfg = readConfig(pathToConfig);
        List<String> queryParams = getQueryParametersFromFile(cfg.getInputFile());
        if (cfg.getItemsNum() != null && cfg.getItemsNum() > 0 ) {
            queryParams = queryParams.subList(0, cfg.getItemsNum());
        }

        Collection<Callable<String>> tasks = new LinkedList<>();
        for (String queryParam : queryParams) {
            tasks.add(new GetRequestWork(cfg.getUrl(), queryParam, cfg.getRequestHeaders(), cfg.getOutputEmptyPlaceholder()));
        }

        int threadsNum = Runtime.getRuntime().availableProcessors();
        if (cfg.getThreadsNum() != null && cfg.getThreadsNum() < threadsNum && cfg.getThreadsNum() > 0) {
            threadsNum = cfg.getThreadsNum();
        }
        ForkJoinPool pool = new ForkJoinPool(threadsNum);
        List<Future<String>> results = pool.invokeAll(tasks);
        List<String> responses = new LinkedList<>();
        for(Future<String> response : results) {
            responses.add(response.get());
        }
        writeToFile(cfg.getOutputFile(), responses);
    }

    public List<List<String>> splitToChunks(List<String> items, int chunksNum) {
        int chunkSize = items.size() / chunksNum;
        if (items.size() % chunksNum != 0 ) ++chunkSize;
        return ListUtils.partition(items, chunkSize);
    }

    private String executeRequest(String url, String requestParameter, Map<String, String> headers, String outputEmptyPlaceholder) throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build()) {
            HttpGet request = new HttpGet(url + requestParameter);
            for (String headerKey : headers.keySet()) {
                request.addHeader(headerKey, headers.get(headerKey));
            }
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            StringBuilder responseContent = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                responseContent.append(line);
            }
            return responseContent.length() > 0 ? responseContent.toString() : outputEmptyPlaceholder + requestParameter;
        }
    }

    private Config readConfig(String pathToConfig) throws IOException {
        LOG.log(Level.INFO, "Reading config from " + pathToConfig);
        try (InputStream input = new FileInputStream(pathToConfig)) {
            Properties prop = new Properties();
            prop.load(input);
            return new Config(prop);
        }
    }

    private List<String> getQueryParametersFromFile(String file) throws IOException {
        return Files.readAllLines(Paths.get(file));
    }

    private void writeToFile(String filePath, List<String> content) throws IOException {
        Path file = Paths.get(filePath);
        Files.write(file, content, Charset.forName("UTF-8"));
    }

    //Callable representing actual HTTP GET request
    class GetRequestWork implements Callable<String> {
        private final String urlPath;
        private final String requestParameter;
        private final Map<String, String> headers;
        private final String outputEmptyPlaceholder;

        public GetRequestWork(String urlPath, String requestParameter, Map<String, String> headers, String outputEmptyPlaceholder) {
            this.urlPath = urlPath;
            this.requestParameter = requestParameter;
            this.headers = headers;
            this.outputEmptyPlaceholder = outputEmptyPlaceholder;
        }

        public String call() throws Exception {
            return executeRequest(this.urlPath, this.requestParameter, headers, outputEmptyPlaceholder);
        }
    }
}
