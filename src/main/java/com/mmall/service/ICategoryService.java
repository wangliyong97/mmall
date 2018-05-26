package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by wangliyong on 2018/5/26.
 */
public interface ICategoryService {
    ServerResponse<String> addCategory(String CategoryName, Integer parentId);

    ServerResponse updateCatagoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
