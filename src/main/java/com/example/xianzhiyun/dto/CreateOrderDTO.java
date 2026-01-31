package com.example.xianzhiyun.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderDTO {
    private List<Item> items;
    private AddressDTO address;
    private String note;

    @Data
    public static class Item {
        private Long goodsId;
        private Double price;
        private Integer quantity;
    }

    @Data
    public static class AddressDTO {
        private String name;
        private String mobile;
        private String school;
        private List<String> region; // ["省","市","区"]
        private String detail;
    }
    private Boolean fromCart;

    public Boolean getFromCart() { return fromCart; }
    public void setFromCart(Boolean fromCart) { this.fromCart = fromCart; }
}