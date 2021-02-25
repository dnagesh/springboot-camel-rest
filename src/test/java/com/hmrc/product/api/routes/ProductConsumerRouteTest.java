package com.hmrc.product.api.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmrc.product.api.ProductApiApplication;
import com.hmrc.product.api.model.Product;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductApiApplication.class)
public class ProductConsumerRouteTest{


   @Value("classpath:test-valid-request.json")
   File validRequestFile;

    @Value("classpath:test-invalid-request.json")
    File inValidRequestFile;

   String restEndpointUri = "http://localhost:8080/products";

   HttpResponse postEndpoint(String uri, HashMap<String, String> httpHeaders, StringEntity fileEntity) throws IOException {

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(fileEntity);
        if (!httpHeaders.isEmpty()) {
            httpHeaders.forEach((key,value) -> {
                        httpPost.setHeader(key, value);
                    }
            );
        }
        return client.execute(httpPost);
    }

    @Test
    public void testHappyMessagePostToRestApi() throws IOException {


        String requestJsonAsString = new String(Files.readAllBytes(validRequestFile.toPath()));
        StringEntity requestJsonPayload = new StringEntity(requestJsonAsString);
        HashMap<String, String> httpHeaders = createHttpHeaders();

        // When
        HttpResponse httpResponse = postEndpoint(restEndpointUri, httpHeaders, requestJsonPayload);
        // And status code is as expected
        assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
        //Then
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(httpResponse.getEntity().getContent(),Product.class);
        assertEquals(2,product.getProductId());
        assertEquals("An ice sculpture",product.getProductName());

    }

    @Test
    public void testUnHappyMessagePostToRestApi() throws IOException {

        String requestJsonAsString = new String(Files.readAllBytes(inValidRequestFile.toPath()));
        StringEntity requestJsonPayload = new StringEntity(requestJsonAsString);
        HashMap<String, String> httpHeaders = createHttpHeaders();

        // When
        HttpResponse httpResponse = postEndpoint(restEndpointUri, httpHeaders, requestJsonPayload);
        // And status code is as expected
        assertEquals(HttpStatus.SC_BAD_REQUEST, httpResponse.getStatusLine().getStatusCode());

        // And response body is as expected
        String returnedResponse = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()))
                .lines().collect(Collectors.joining("\n"));
        assertEquals("Invalid product request data", returnedResponse);

    }


    // Creates mandatory header elements for JSON request;
    private HashMap<String, String> createHttpHeaders() {
        return new HashMap<String, String>() {{
            put("Content-Type", "application/json");
        }};
    }
}