package com.known.service;


import com.known.common.model.Category;
import com.known.manager.query.CategoryQuery;

import java.util.List;

public interface CategoryService {
	
	List<Category> findCategoryList(CategoryQuery categoryQuery);
	
	List<Category> findCategory4TopicCount(CategoryQuery categoryQuery);
	
	Category findCategoryBypCategoryId(Integer pCategoryId);
	
	Category findCategoryByCategoryId(Integer categoryId);
	
	Category findSingleCategoryByCategoryId(Integer category);
}
