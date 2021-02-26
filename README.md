### Spring Boot Camel Rest

This is a sample application where the purpose is to develop
Rest API using Apache Camel and SpringBoot and deploy in docker container

The Rest API accesed using the URI http://localhost:8080/products and
this will accept and produce the messages of format application/json. 

It will validae the input the JSON message, transforms to another JSON message and 
finally validates the response.

### Create a Docker image and run

mvn clean install

docker build -t "product-app" .

docker run -p 8080:8080 product-app 

### JSON Schema and sample requests

The request and response schemas can be found in main/resources/schema
requests can be found in the directory test/resources

### Testing

The API can be tested using a CURL or POSTMAN.
