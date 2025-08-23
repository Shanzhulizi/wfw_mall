package com.lm.service;

import com.lm.es.ESProduct;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    void bulkInsert(List<ESProduct> products);
    List<ESProduct> search(String keyword, String brand, String category, Double minPrice, Double maxPrice, int page, int size) throws IOException;

}
