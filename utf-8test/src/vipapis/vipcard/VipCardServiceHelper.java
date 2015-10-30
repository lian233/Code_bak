package vipapis.vipcard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vip.osp.sdk.base.OspRestStub;
import com.vip.osp.sdk.exception.OspException;
import com.vip.osp.sdk.protocol.Protocol;

public class VipCardServiceHelper {
	
	
	
	
	public static class cancelSoldCard_args {
		
		/**
		* 店铺名称
		*/
		
		private String shop_name;
		
		/**
		* 店铺地址
		*/
		
		private String shop_area;
		
		/**
		* 请求client_id
		*/
		
		private int client_id;
		
		/**
		* 商家卡类型编号
		*/
		
		private int type;
		
		/**
		* 唯品卡卡号
		*/
		
		private String card_code;
		
		/**
		* 自增流水号，如果小于等于系统记录的最后处理流水号忽略处理
		*/
		
		private int trans_id;
		
		/**
		* 售出唯品卡使用的流水号，该售出流水号一旦冲正成功即无法再次使用
		*/
		
		private int sale_trans_id;
		
		public String getShop_name(){
			return this.shop_name;
		}
		
		public void setShop_name(String value){
			this.shop_name = value;
		}
		public String getShop_area(){
			return this.shop_area;
		}
		
		public void setShop_area(String value){
			this.shop_area = value;
		}
		public int getClient_id(){
			return this.client_id;
		}
		
		public void setClient_id(int value){
			this.client_id = value;
		}
		public int getType(){
			return this.type;
		}
		
		public void setType(int value){
			this.type = value;
		}
		public String getCard_code(){
			return this.card_code;
		}
		
		public void setCard_code(String value){
			this.card_code = value;
		}
		public int getTrans_id(){
			return this.trans_id;
		}
		
		public void setTrans_id(int value){
			this.trans_id = value;
		}
		public int getSale_trans_id(){
			return this.sale_trans_id;
		}
		
		public void setSale_trans_id(int value){
			this.sale_trans_id = value;
		}
		
	}
	
	
	
	
	public static class getCardStatus_args {
		
		/**
		* 店铺名称
		*/
		
		private String shop_name;
		
		/**
		* 店铺地址
		*/
		
		private String shop_area;
		
		/**
		* 请求clientId
		*/
		
		private int client_id;
		
		/**
		* 商家卡类型编号
		*/
		
		private int type;
		
		/**
		* 唯品卡号，支持批量
		*/
		
		private List<String> card_code;
		
		public String getShop_name(){
			return this.shop_name;
		}
		
		public void setShop_name(String value){
			this.shop_name = value;
		}
		public String getShop_area(){
			return this.shop_area;
		}
		
		public void setShop_area(String value){
			this.shop_area = value;
		}
		public int getClient_id(){
			return this.client_id;
		}
		
		public void setClient_id(int value){
			this.client_id = value;
		}
		public int getType(){
			return this.type;
		}
		
		public void setType(int value){
			this.type = value;
		}
		public List<String> getCard_code(){
			return this.card_code;
		}
		
		public void setCard_code(List<String> value){
			this.card_code = value;
		}
		
	}
	
	
	
	
	public static class sellCard_args {
		
		/**
		* 店铺名称
		*/
		
		private String shop_name;
		
		/**
		* 店铺地址
		*/
		
		private String shop_area;
		
		/**
		* 请求client_id
		*/
		
		private int client_id;
		
		/**
		* 商家卡类型编号
		*/
		
		private int type;
		
		/**
		* 唯品卡卡号
		*/
		
		private String card_code;
		
		/**
		* 自增流水号，如果小于等于系统记录的最后处理流水号忽略处理
		*/
		
		private int trans_id;
		
		public String getShop_name(){
			return this.shop_name;
		}
		
		public void setShop_name(String value){
			this.shop_name = value;
		}
		public String getShop_area(){
			return this.shop_area;
		}
		
		public void setShop_area(String value){
			this.shop_area = value;
		}
		public int getClient_id(){
			return this.client_id;
		}
		
		public void setClient_id(int value){
			this.client_id = value;
		}
		public int getType(){
			return this.type;
		}
		
		public void setType(int value){
			this.type = value;
		}
		public String getCard_code(){
			return this.card_code;
		}
		
		public void setCard_code(String value){
			this.card_code = value;
		}
		public int getTrans_id(){
			return this.trans_id;
		}
		
		public void setTrans_id(int value){
			this.trans_id = value;
		}
		
	}
	
	
	
	
	public static class cancelSoldCard_result {
		
		/**
		* 是否取消售出成功，true为成功，false为失败
		*/
		
		private Boolean success;
		
		public Boolean getSuccess(){
			return this.success;
		}
		
		public void setSuccess(Boolean value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class getCardStatus_result {
		
		/**
		*/
		
		private List<vipapis.vipcard.VipCard> success;
		
		public List<vipapis.vipcard.VipCard> getSuccess(){
			return this.success;
		}
		
		public void setSuccess(List<vipapis.vipcard.VipCard> value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class sellCard_result {
		
		/**
		* 是否售出成功，true为成功，false为失败
		*/
		
		private Boolean success;
		
		public Boolean getSuccess(){
			return this.success;
		}
		
		public void setSuccess(Boolean value){
			this.success = value;
		}
		
	}
	
	
	
	
	public static class cancelSoldCard_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<cancelSoldCard_args>
	{
		
		public static final cancelSoldCard_argsHelper OBJ = new cancelSoldCard_argsHelper();
		
		public static cancelSoldCard_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(cancelSoldCard_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setShop_name(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setShop_area(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setClient_id(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setType(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setCard_code(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setTrans_id(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setSale_trans_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(cancelSoldCard_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("shop_name");
			oprot.writeString(struct.getShop_name());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("shop_area");
			oprot.writeString(struct.getShop_area());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("client_id");
			oprot.writeI32(struct.getClient_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("type");
			oprot.writeI32(struct.getType()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("card_code");
			oprot.writeString(struct.getCard_code());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("trans_id");
			oprot.writeI32(struct.getTrans_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("sale_trans_id");
			oprot.writeI32(struct.getSale_trans_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(cancelSoldCard_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCardStatus_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<getCardStatus_args>
	{
		
		public static final getCardStatus_argsHelper OBJ = new getCardStatus_argsHelper();
		
		public static getCardStatus_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCardStatus_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setShop_name(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setShop_area(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setClient_id(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setType(value);
			}
			
			
			
			
			
			if(true){
				
				List<String> value;
				
				value = new ArrayList<String>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						String elem0;
						elem0 = iprot.readString();
						
						value.add(elem0);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setCard_code(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getCardStatus_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getShop_name() != null) {
				
				oprot.writeFieldBegin("shop_name");
				oprot.writeString(struct.getShop_name());
				
				oprot.writeFieldEnd();
			}
			
			
			if(struct.getShop_area() != null) {
				
				oprot.writeFieldBegin("shop_area");
				oprot.writeString(struct.getShop_area());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldBegin("client_id");
			oprot.writeI32(struct.getClient_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("type");
			oprot.writeI32(struct.getType()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("card_code");
			
			oprot.writeListBegin();
			for(String _item0 : struct.getCard_code()){
				
				oprot.writeString(_item0);
				
			}
			
			oprot.writeListEnd();
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCardStatus_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class sellCard_argsHelper implements com.vip.osp.sdk.base.BeanSerializer<sellCard_args>
	{
		
		public static final sellCard_argsHelper OBJ = new sellCard_argsHelper();
		
		public static sellCard_argsHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(sellCard_args struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setShop_name(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setShop_area(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setClient_id(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setType(value);
			}
			
			
			
			
			
			if(true){
				
				String value;
				value = iprot.readString();
				
				struct.setCard_code(value);
			}
			
			
			
			
			
			if(true){
				
				int value;
				value = iprot.readI32(); 
				
				struct.setTrans_id(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(sellCard_args struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			oprot.writeFieldBegin("shop_name");
			oprot.writeString(struct.getShop_name());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("shop_area");
			oprot.writeString(struct.getShop_area());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("client_id");
			oprot.writeI32(struct.getClient_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("type");
			oprot.writeI32(struct.getType()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("card_code");
			oprot.writeString(struct.getCard_code());
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldBegin("trans_id");
			oprot.writeI32(struct.getTrans_id()); 
			
			oprot.writeFieldEnd();
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(sellCard_args bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class cancelSoldCard_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<cancelSoldCard_result>
	{
		
		public static final cancelSoldCard_resultHelper OBJ = new cancelSoldCard_resultHelper();
		
		public static cancelSoldCard_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(cancelSoldCard_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				Boolean value;
				value = iprot.readBool();
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(cancelSoldCard_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				oprot.writeBool(struct.getSuccess());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(cancelSoldCard_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class getCardStatus_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<getCardStatus_result>
	{
		
		public static final getCardStatus_resultHelper OBJ = new getCardStatus_resultHelper();
		
		public static getCardStatus_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(getCardStatus_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				List<vipapis.vipcard.VipCard> value;
				
				value = new ArrayList<vipapis.vipcard.VipCard>();
				iprot.readListBegin();
				while(true){
					
					try{
						
						vipapis.vipcard.VipCard elem0;
						
						elem0 = new vipapis.vipcard.VipCard();
						vipapis.vipcard.VipCardHelper.getInstance().read(elem0, iprot);
						
						value.add(elem0);
					}
					catch(Exception e){
						
						break;
					}
				}
				
				iprot.readListEnd();
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(getCardStatus_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				
				oprot.writeListBegin();
				for(vipapis.vipcard.VipCard _item0 : struct.getSuccess()){
					
					
					vipapis.vipcard.VipCardHelper.getInstance().write(_item0, oprot);
					
				}
				
				oprot.writeListEnd();
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(getCardStatus_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class sellCard_resultHelper implements com.vip.osp.sdk.base.BeanSerializer<sellCard_result>
	{
		
		public static final sellCard_resultHelper OBJ = new sellCard_resultHelper();
		
		public static sellCard_resultHelper getInstance() {
			
			return OBJ;
		}
		
		
		public void read(sellCard_result struct, Protocol iprot) throws OspException {
			
			
			
			
			if(true){
				
				Boolean value;
				value = iprot.readBool();
				
				struct.setSuccess(value);
			}
			
			
			
			
			validate(struct);
			
		}
		
		
		public void write(sellCard_result struct, Protocol oprot) throws OspException {
			
			validate(struct);
			oprot.writeStructBegin();
			
			if(struct.getSuccess() != null) {
				
				oprot.writeFieldBegin("success");
				oprot.writeBool(struct.getSuccess());
				
				oprot.writeFieldEnd();
			}
			
			
			oprot.writeFieldStop();
			oprot.writeStructEnd();
		}
		
		
		public void validate(sellCard_result bean) throws OspException {
			
			
		}
		
		
	}
	
	
	
	
	public static class VipCardServiceClient extends OspRestStub implements VipCardService  {
		
		
		public VipCardServiceClient() {
			
			super("1.0.0", "vipapis.vipcard.VipCardService");
		}
		
		
		
		public Boolean cancelSoldCard(String shop_name,String shop_area,int client_id,int type,String card_code,int trans_id,int sale_trans_id) throws OspException {
			
			send_cancelSoldCard(shop_name,shop_area,client_id,type,card_code,trans_id,sale_trans_id);
			return recv_cancelSoldCard(); 
			
		}
		
		
		private void send_cancelSoldCard(String shop_name,String shop_area,int client_id,int type,String card_code,int trans_id,int sale_trans_id) throws OspException {
			
			initInvocation("cancelSoldCard");
			
			cancelSoldCard_args args = new cancelSoldCard_args();
			args.setShop_name(shop_name);
			args.setShop_area(shop_area);
			args.setClient_id(client_id);
			args.setType(type);
			args.setCard_code(card_code);
			args.setTrans_id(trans_id);
			args.setSale_trans_id(sale_trans_id);
			
			sendBase(args, cancelSoldCard_argsHelper.getInstance());
		}
		
		
		private Boolean recv_cancelSoldCard() throws OspException {
			
			cancelSoldCard_result result = new cancelSoldCard_result();
			receiveBase(result, cancelSoldCard_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public List<vipapis.vipcard.VipCard> getCardStatus(String shop_name,String shop_area,int client_id,int type,List<String> card_code) throws OspException {
			
			send_getCardStatus(shop_name,shop_area,client_id,type,card_code);
			return recv_getCardStatus(); 
			
		}
		
		
		private void send_getCardStatus(String shop_name,String shop_area,int client_id,int type,List<String> card_code) throws OspException {
			
			initInvocation("getCardStatus");
			
			getCardStatus_args args = new getCardStatus_args();
			args.setShop_name(shop_name);
			args.setShop_area(shop_area);
			args.setClient_id(client_id);
			args.setType(type);
			args.setCard_code(card_code);
			
			sendBase(args, getCardStatus_argsHelper.getInstance());
		}
		
		
		private List<vipapis.vipcard.VipCard> recv_getCardStatus() throws OspException {
			
			getCardStatus_result result = new getCardStatus_result();
			receiveBase(result, getCardStatus_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
		public Boolean sellCard(String shop_name,String shop_area,int client_id,int type,String card_code,int trans_id) throws OspException {
			
			send_sellCard(shop_name,shop_area,client_id,type,card_code,trans_id);
			return recv_sellCard(); 
			
		}
		
		
		private void send_sellCard(String shop_name,String shop_area,int client_id,int type,String card_code,int trans_id) throws OspException {
			
			initInvocation("sellCard");
			
			sellCard_args args = new sellCard_args();
			args.setShop_name(shop_name);
			args.setShop_area(shop_area);
			args.setClient_id(client_id);
			args.setType(type);
			args.setCard_code(card_code);
			args.setTrans_id(trans_id);
			
			sendBase(args, sellCard_argsHelper.getInstance());
		}
		
		
		private Boolean recv_sellCard() throws OspException {
			
			sellCard_result result = new sellCard_result();
			receiveBase(result, sellCard_resultHelper.getInstance());
			
			return result.getSuccess();
			
		}
		
		
	}
	
	
}