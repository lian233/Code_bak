package vipapis.category;

import java.util.Set;
import java.util.List;
import java.util.Map;
import com.vip.osp.sdk.exception.OspException;



public interface CategoryService {
	
	
	
	List<vipapis.category.Attribute> getCategoryAttributeListById( int category_id ) throws OspException;
	
	
	vipapis.category.Category getCategoryById( int category_id ) throws OspException;
	
	
	List<vipapis.category.Category> getCategoryListByName( String category_name,   int limit ) throws OspException;
	
	
	vipapis.category.Category getCategoryTreeById( int category_id ) throws OspException;
	
	
	vipapis.category.CategoryUpdates getUpdatedCategoryList( long since_updatetime,   int hierarchyId ) throws OspException;
	
}