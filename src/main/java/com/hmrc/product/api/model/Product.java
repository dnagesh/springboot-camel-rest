package com.hmrc.product.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by ND on 23/02/21.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Integer productId;
    private String productName;
    private Person customer;
    private BigDecimal price;

}
