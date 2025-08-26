package com.lm.es.vo;

import com.lm.es.domain.ESProduct;
import lombok.Data;

import java.util.List;

@Data
public class SearchResultVO {
    private List<ESProduct> list;
    private Long total;
    private Integer currentPage;
    private Integer totalPages;
    private Boolean hasMore;

    public static SearchResultVO of(List<ESProduct> list, Long total, Integer page, Integer size) {
        SearchResultVO result = new SearchResultVO();
        result.setList(list);
        result.setTotal(total);
        result.setCurrentPage(page);
        result.setTotalPages((int) Math.ceil((double) total / size));
        result.setHasMore(page * size < total);
        return result;
    }
}