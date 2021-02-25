package com.hmrc.product.api.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmrc.product.api.model.Person;
import com.hmrc.product.ui.model.Product;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductMapper {


    public String transform(String productUiJson) throws JsonProcessingException {

        if (StringUtils.isBlank(productUiJson)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        Product ui = mapper.readValue(productUiJson, Product.class);

        com.hmrc.product.api.model.Product product = new com.hmrc.product.api.model.Product();
        product.setProductId(ui.getId());
        product.setProductName(ui.getName());
        product.setPrice(new BigDecimal(ui.getPrice()));
        Person customer = new Person();
        customer.setFirstName("Tom");
        customer.setLastName("Freddy");
        product.setCustomer(customer);
  return mapper.writeValueAsString(product);

    }


}
