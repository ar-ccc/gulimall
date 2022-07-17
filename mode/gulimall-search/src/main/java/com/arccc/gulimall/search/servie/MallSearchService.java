package com.arccc.gulimall.search.servie;

import com.arccc.gulimall.search.vo.SearchResponse;

public interface MallSearchService {
    /**
     *
     * @param param 检索的所有参数
     * @return 检索的所有结果
     */

    SearchResponse search(Long catalog3Id, String keyword);
}
