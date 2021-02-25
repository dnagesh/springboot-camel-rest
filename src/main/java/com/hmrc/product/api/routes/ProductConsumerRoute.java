package com.hmrc.product.api.routes;

import com.hmrc.product.api.mapper.ProductMapper;
import com.hmrc.product.api.model.Product;
import org.apache.camel.BeanInject;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A ProductConsumerRoute which exposed Rest API
 *
 */
@Component
public class ProductConsumerRoute extends RouteBuilder {

    @BeanInject("productMapper")
    ProductMapper productMapper;

    @Value("${server.port}")
    private String serverPort;
    @Override
    public void configure() throws Exception {

        restConfiguration()
                .component("jetty")
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .port(serverPort);

        rest().post("/products")
                .clientRequestValidation(true)
                .bindingMode(RestBindingMode.json)
                .consumes("application/json")
                .produces("application/json")
                .route()
                .to("direct:validate_ui_json")
                .to("direct:transform_json")
                .to("direct:validate_api_json")
                .endRest();

        onException(JsonValidationException.class)
                .handled(true)
                .log(LoggingLevel.ERROR,"product request: ${in.body} \n message: ${exception.message} ")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .setBody().constant("Invalid product request data");

        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .log("message: ${exception.message},\nexception: ${exception.stacktrace} ")
                .setBody(simple("Internal Server Error"));

        from("direct:validate_ui_json")
                .log(LoggingLevel.INFO, "product frontend json: ${in.body}")
                .marshal().json(JsonLibrary.Jackson, true)
                .to("json-validator:schema/product.json")
                .log(LoggingLevel.INFO, "The validation of product ui json is successful");

        from("direct:validate_api_json")
                .log(LoggingLevel.INFO, "product backend json: ${in.body}")
                .to("json-validator:schema/product_api.json")
                .unmarshal().json(JsonLibrary.Jackson, Product.class)
                .log(LoggingLevel.INFO, "The validation of product backend json is successful")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));

     from("direct:transform_json")
                .bean(method("productMapper","transform"));

    }


}
