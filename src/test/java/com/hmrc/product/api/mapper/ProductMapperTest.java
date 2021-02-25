package com.hmrc.product.api.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class ProductMapperTest {

    private ProductMapper productMapper;

    @Before
    public void setup() {
        productMapper = new ProductMapper();
    }

    @Test
    public void tranformWhenRequestIsNull() throws JsonProcessingException {
        String product = productMapper.transform(null);
        assertThat(product).isNull();
    }

    @Test(expected = JsonProcessingException.class)
    public void tranformWhenRequestIsInvalidJSON() throws JsonProcessingException {
       productMapper.transform("Test");
    }

    @Test
    public void tranformWhenRequestIsValidJSON() throws JsonProcessingException {

        String expectedJson =   "{\"productId\":2,\"productName\":\"An ice sculpture\",\"customer\":{\"firstName\":\"Tom\",\"lastName\":\"Freddy\"},\"price\":12.5}";
        String actualJson =  productMapper.transform("{\"id\":2,\"name\":\"An ice sculpture\",\"price\":12.5}");
        assertThat(actualJson).isNotNull();
        assertThat(actualJson).isEqualTo(expectedJson);
    }


}