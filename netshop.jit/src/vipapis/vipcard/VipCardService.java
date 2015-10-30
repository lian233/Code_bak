package vipapis.vipcard;

import java.util.Set;
import java.util.List;
import java.util.Map;
import com.vip.osp.sdk.exception.OspException;



public interface VipCardService {
	
	
	
	Boolean cancelSoldCard( String shop_name,   String shop_area,   int client_id,   int type,   String card_code,   int trans_id,   int sale_trans_id ) throws OspException;
	
	
	List<vipapis.vipcard.VipCard> getCardStatus( String shop_name,   String shop_area,   int client_id,   int type,   List<String> card_code ) throws OspException;
	
	
	Boolean sellCard( String shop_name,   String shop_area,   int client_id,   int type,   String card_code,   int trans_id ) throws OspException;
	
}