package com.wofu.ecommerce.test;

import java.util.Hashtable;
import java.util.Iterator;

import com.wofu.common.json.JSONArray;
import com.wofu.common.json.JSONObject;
import com.wofu.ecommerce.alibaba.Children;
import com.wofu.ecommerce.alibaba.Goods;
import com.wofu.ecommerce.alibaba.GoodsSKU;
import com.wofu.ecommerce.alibaba.Offer;
import com.wofu.ecommerce.alibaba.Params;
import com.wofu.ecommerce.alibaba.ProductFeatureList;
import com.wofu.ecommerce.alibaba.api.ApiCallService;
import com.wofu.ecommerce.alibaba.util.CommonUtil;
/***
 * 测试：获取单个商品并修改JSON串中的SKU库存
 * @author Administrator
 *
 */
public class test4 {
	private static String lasttime;
	private static long daymillis=24*60*60*1000L;
	private static String access_token=null;
	private static String type="ALL";
	
	private static String returnFields="tradeType,isOfferSupportOnlineTrade,isPicAuthOffer,isPriceAuthOffer,isSkuOffer,isSupportMix,offerId,priceRanges,amountOnSale,productFeatureList,postCategryId,termOfferProcess,details,subject,imageList,freightType,sendGoodsId,freightTemplateId,productUnitWeight,skuArray";
	private static String lasttimeconfvalue=Params.username+"取订单最新时间";
	public static void main(String[] args)throws Exception {
		
//		Hashtable<String, String> params = new Hashtable<String, String>() ;
//		params.put("type", type) ;
//		params.put("returnFields", returnFields);
//		String urlPath=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.getAllOfferList",Params.version,Params.requestmodel,Params.appkey);
//		System.out.println(urlPath);
//	    params.put("access_token", "122e2443-9049-4dba-b8b2-63e80e50547f");
//		
//		String responseText = ApiCallService.callApiTest(Params.url, urlPath, Params.secretKey, params);
//		System.out.println("responseText："+responseText);
		
		Hashtable<String, String> params1 = new Hashtable<String, String>() ;
		params1.put("offerId","1257893833");
		params1.put("returnFields", "isPrivateOffer,isPriceAuthOffer,isPicAuthOffer,offerId,isPrivate,detailsUrl,type,tradeType,postCategryId,offerStatus,memberId,subject,details,qualityLevel,imageList,isOfferSupportOnlineTrade,tradingType,isSupportMix,unit,priceUnit,amount,amountOnSale,saledCount,retailPrice,unitPrice,priceRanges,termOfferProcess,freightTemplateId,sendGoodsId,productUnitWeight,freightType,isSkuOffer,isSkuTradeSupported,gmtCreate,gmtModified,gmtLastRepost,gmtApproved,gmtExpire,productFeatureList,skuArray,skuPics");
		//params1.put("returnFields",returnFields);
		
		String urlPath1=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.get",Params.version,Params.requestmodel,Params.appkey);
		String response =ApiCallService.callApiTest(Params.url, urlPath1, Params.secretKey, params1);
		//System.out.println("response"+response);
		JSONObject res=new JSONObject(response);
		
		JSONObject jo=res.getJSONObject("result").getJSONArray("toReturn").getJSONObject(0);
		System.out.println("前："+jo);
		Goods gds=new Goods();
		gds.setObjValue(gds,jo);
		
		System.out.println("gds:"+gds.toJSONObject());
		
		Offer offer=new Offer();
		offer.setBizType(Integer.parseInt(gds.getTradeType()));
		offer.setSupportOnlineTrade(gds.getIsOfferSupportOnlineTrade());
		offer.setPictureAuthOffer(gds.getIsPicAuthOffer());
		offer.setPriceAuthOffer(gds.getIsPriceAuthOffer());
		offer.setSkuTradeSupport(gds.getIsSkuTradeSupported());
		offer.setMixWholeSale(gds.getIsSupportMix());
		offer.setOfferId(gds.getOfferId());
		//获取价格区间
		String pricerange="";
		JSONArray jsa=new JSONArray(gds.getPriceRanges());
		for(int i=0;i<jsa.length();i++){
			JSONObject ob=jsa.getJSONObject(i);
			String range=ob.getString("range");
			if(range.contains("-")){
				range=range.split("-")[0];
			}
			if(range.contains("≥")){
				range=range.split("≥")[1];
			}
			range=range+":"+ob.getString("price");
			pricerange=pricerange+range;
			if(i<jsa.length()-1){
				pricerange+="`";
			}
		}
		System.out.println("价格区间:"+pricerange);
		offer.setPriceRanges(pricerange);
		offer.setAmountOnSale(gds.getAmountOnSale());
		String productfeature="";
		/*for(Iterator it=gds.getProductFeatureList().getRelationData().iterator();it.hasNext();){
			ProductFeatureList pfl=(ProductFeatureList)it.next();
			String a="\\\"";
			productfeature+=a+pfl.getFid()+a+":"+a+pfl.getValue()+a;
			if(it.hasNext()){
				productfeature+=",";
			}
		}*/
		productfeature+=",\\\"8021\\\":"+jo.getJSONObject("retailPrice").getInt("cent")/100;
		//设置属性列表
		System.out.println("属性列表："+"{"+productfeature+"}");
		offer.setProductFeatures("{"+productfeature+"}");
		
		offer.setCategoryID(gds.getPostCategryId());
		offer.setPeriodOfValidity(gds.getTermOfferProcess());
		offer.setOfferDetail(gds.getDetails().replace("\"", "\\\""));
		offer.setSubject(gds.getSubject());
		
		JSONArray piclist=jo.getJSONArray("imageList");
		String sss="";
		
		for(int i=0;i<piclist.length();i++){
			
			sss+="\\\""+piclist.getJSONObject(i).getString("originalImageURI").replace("\"", "\\\"")+"\\\"";
			if(i<piclist.length()-1){
				sss+=",";
			}
		}
		//图片列表
		//System.out.println("图片列表："+"["+sss+"]");
		offer.setImageUriList("["+sss+"]");
		offer.setFreightType(gds.getFreightType());
		offer.setSendGoodsAddressId(gds.getSendGoodsId());
		offer.setFreightTemplateId(gds.getFreightTemplateId());
		offer.setOfferWeight(gds.getProductUnitWeight());
		String skulist="[";
		for(Iterator it=gds.getSkuArray().getRelationData().iterator();it.hasNext();){
			GoodsSKU gsku=(GoodsSKU)it.next();
			
			if(it.hasNext()){
				skulist+=",";
			}
		}
		//System.out.println("SKU列表："+skulist+"]");
		offer.setSkuList(skulist+"]");
		
		System.out.println("retailPrice:"+jo.getJSONObject("retailPrice").toString().replace("\"", "\\\""));

		//offer.setRetailPrice(jo.getJSONObject("retailPrice").toString().replace("\"", "\\\""));
		
		 //String skupic=jo.getJSONObject("skuPics").toString().replace("\"", "\\\"");
		JSONArray skupic=jo.getJSONObject("skuPics").getJSONArray(jo.getJSONObject("skuPics").keys().next().toString());
		String pichost="http://img.china.alibaba.com/";
		for(int i=0;i<skupic.length();i++){
			String key=String.valueOf(skupic.getJSONObject(i).keys().next());
			//通过KEY修改值
			JSONObject yanse=skupic.getJSONObject(i);
			yanse.put(key, pichost+yanse.getString(key));
		}
		
		offer.setSkuPics(jo.getJSONObject("skuPics").toString().replace("\"", "\\\""));
		
		
		
		
		String s=offer.toJSONObject();
		System.out.println("S:"+s);
		JSONObject offerobject=new JSONObject(s);
		offerobject.put("productFeatures", new JSONObject(offerobject.optString("productFeatures")));
		offerobject.put("skuPics", new JSONObject(offerobject.optString("skuPics")));
		//offerobject.put("retailPrice", new JSONObject(offerobject.optString("retailPrice")));
		offerobject.put("skuList", new JSONArray(offerobject.optString("skuList")));
		offerobject.put("imageUriList", new JSONArray(offerobject.optString("imageUriList")));
		System.out.println("offerobject:"+offerobject);
		
		
		
		
		
//		System.out.println(java.net.URLEncoder.encode(jo.toString(),"GBK"));
//		Hashtable<String, String> params2 = new Hashtable<String, String>() ;
//		params2.put("offer",offerobject.toString());
//		params2.put("access_token", "04128b77-5282-4a61-b027-bcc7b5921786");
//		String urlPath2=CommonUtil.buildInvokeUrlPath(Params.namespace,"offer.modify",Params.version,Params.requestmodel,Params.appkey);
//		//执行修改
//		String response1 =ApiCallService.callApiTest(Params.url, urlPath2, Params.secretKey, params2);
//		JSONObject mojo=new JSONObject(response1);
//		System.out.println(mojo);
	}
}
