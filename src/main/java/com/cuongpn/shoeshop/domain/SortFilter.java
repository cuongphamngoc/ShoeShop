package com.cuongpn.shoeshop.domain;

import org.springframework.data.domain.Sort;

public class SortFilter {
    private String sortType;

    public SortFilter(String type) {
        this.sortType = type;
    }
    public static Sort getSort(String sortType){
        Sort sort;
        switch (sortType) {
            case "priceASC":
            {
                sort = Sort.by(Sort.Direction.ASC, "price");
                break;
            }
            case "priceDESC":{
                sort = Sort.by(Sort.Direction.DESC, "price");
                break;
            }
            case "alphaASC":{
                sort = Sort.by(Sort.Direction.ASC, "title");
                break;
            }
            case "alphaDESC":{
                sort = Sort.by(Sort.Direction.DESC, "title");
                break;
            }
            default:{
                sort = Sort.by(Sort.Direction.DESC,"id" );
                break;
            }
        }
        return sort;
    }
}
