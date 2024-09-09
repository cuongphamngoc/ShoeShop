package com.cuongpn.shoeshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ProductFilterForm {
    private List<String> size;
    private List<String> category;
    private List<String> brand;
    private Long priceLow;
    private Long priceHigh;
    private String sortType;
    private Integer pageNo;
    private Integer pageSize;
    private String searchKey;
    public ProductFilterForm(){
        size = new ArrayList<>();

        category = new ArrayList<>();

        brand= new ArrayList<>();


        pageNo = 0;
        pageSize = 10;
        sortType = "*";
        searchKey="";
    }
}
