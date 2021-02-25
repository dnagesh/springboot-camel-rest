package com.hmrc.product.api.mock;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.hmrc.product.api.model.Product;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
public class ProductMockServerTest {


     @Value("classpath:test-valid-request.json")
    File validRequestFile;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Before
    public void setup() {
        final String response = "{\"productId\":2,\"productName\":\"An ice sculpture\",\"customer\":{\"firstName\":\"Tom\",\"lastName\":\"Freddy\"},\"price\":12.5}\"";

        wireMockRule.stubFor(post(urlPathMatching("/products"))
                .withRequestBody(containing("{ \"id\": 2,\"name\": \"An ice sculpture\",\"price\": 12.50}"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));


    }
    @Test
    public void testHappyFlow() throws IOException {

        wireMockRule.start();

        HashMap<String, String> httpHeaders = createHttpHeaders();

        String requestJsonAsString = new String(Files.readAllBytes(validRequestFile.toPath()));
        StringEntity requestJsonPayload = new StringEntity(requestJsonAsString);

        HttpResponse httpResponse = postEndpoint("http://localhost:8089/products", httpHeaders, requestJsonPayload);
        //Then
        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(httpResponse.getEntity().getContent(),Product.class);
        assertEquals(2,product.getProductId());
        assertEquals("An ice sculpture",product.getProductName());

        wireMockRule.stop();
    }



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

    // Creates mandatory header elements for JSON request;
    private HashMap<String, String> createHttpHeaders() {
        return new HashMap<String, String>() {{
            put("Content-Type", "application/json");
        }};
    }
}