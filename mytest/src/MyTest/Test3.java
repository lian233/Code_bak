package MyTest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.wofu.common.tools.sql.SQLHelper;
import com.wofu.common.tools.util.log.Log;
/**
 * д��imgae�����ݿ�
 * @author Administrator
 *
 */
public class Test3 {

	//private static final String URL="jdbc:sqlserver://wolfdhc.eicp.net;DatabaseName=ErpTestDHBMStock";
	private static final String URL="jdbc:sqlserver://121.199.175.209:30003;DatabaseName=erprbbmstock";
	private static final String USER="wolf";//"login";wolf
	private static final String PASSWORD="disneyatyongjun";//"disneyatyongjun";  ASDF23wert12
	//private static final String PASSWORD="ASDF23wert12";
	private static final String DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String JS="{extend:'ECS.baseeditfrm',title:'���ͬ������',requestName:'ecsstockconfig',keyFieldName:'serialid',uniqueFields:'orgid,itemid',objName:'ECS.stockconfig',findLabelWidth:90,addLabelWidth:60,modifyLabelWidth:60,addWindowHeight:650,searchOrderFieldName:'orgid,itemcode',tbar:{layout:{type:'table'},items:[{xtype:'combobox',fieldLabel:'��������',displayField:'orgname',valueField:'orgid',store:{autoLoad:false,autoDestory:true,fields:[{name:'orgid',type:'int'},{name:'orgname',type:'string'}],queryMode:'local',listeners:{afterrender:{fn:function(){var re ;try{re = ECS.Remote.getInstance().loadData('getShop','','ecsorg',_local.user.name);}catch(e){alert('���ػ�����Ϣ����'+e);}this.store.loadData(re,false);}}}}}]},store:Ext.create('Ext.data.Store',{fields:[{name:'serialid',type:'int'},{name:'orgid',type:'int'},{name:'itemid',type:'string'},{name:'itemcode',type:'string'},{name:'title',type:'string'},{name:'alarmqty',type:'int'},{name:'alarmstyle',type:'int'},{name:'isneedsyn',type:'bool'},{name:'addstockqty',type:'number'},{name:'stockcount',type:'int'},{name:'errflag',type:'bool'},{name:'errmsg',type:'string'},{name:'creator',type:'string'},{name:'createtime',type:'date',dateFormat:'Y-m-d H:i:s.u'},{name:'updator',type:'string'},{name:'updatetime',type:'date',dateFormat:'Y-m-d H:i:s.u'}],autoDestroy:true,autoLoad:false}),columns:[{text:'��������',xtype:'gridcolumn',width:100,dataIndex:'orgid',renderer:function(h,f,d,e,k,j,g){var a=g.headerCt.getGridColumns()[k];for(var c=0;c<a.field.store.data.length;c++){var b=a.field.store.data.items[c];if(b.get('orgid')==h){return b.get('orgname');}}},field:{xtype:'combobox',typeAhead:true,fieldLabel:'',fieldname:'orgid',queryMode:'local',triggerAction:'all',selectOnTab:true,width:190,lazyRender:true,listClass:'x-combo-list-small',displayField:'orgname',valueField:'orgid',store:Ext.create('Ext.data.Store',{fields:[{name:'orgid',type:'int'},{name:'orgname',type:'string'}],autoDestroy:true,autoLoad:false})}},{text:'��ƷID',xtype:'gridcolumn',width:100,dataIndex:'itemid'},{text:'�̼ұ���',xtype:'gridcolumn',width:100,dataIndex:'itemcode'},{text:'��Ʒ����',xtype:'gridcolumn',width:200,dataIndex:'title'},{text:'������',xtype:'numbercolumn',width:60,format:'0',dataIndex:'alarmqty',field:{xtype:'numberfield',allowBlank:false,enableKeyEvents:true,value:0,minValue:0,fieldLabel:'',width:150,fieldname:'alarmqty'}},{text:'���䴦��ʽ',xtype:'gridcolumn',width:100,dataIndex:'alarmstyle',renderer:function(c,b,a,d){if(c===1){return'�������Ϊ��';}else{if(c==2){return'�ʼ�֪ͨ';}else{return'����֪ͨ';}}},field:{xtype:'combobox',typeAhead:true,fieldLabel:'',fieldname:'alarmstyle',queryMode:'local',triggerAction:'all',selectOnTab:true,width:190,lazyRender:true,listClass:'x-combo-list-small',displayField:'typename',valueField:'typeid',store:Ext.create('Ext.data.Store',{fields:[{name:'typeid',type:'int'},{name:'typename',type:'string'}],data:[{typeid:1,typename:'�������Ϊ��'},{typeid:2,typename:'�ʼ�֪ͨ'},{typeid:2,typename:'����֪ͨ'}],autoDestroy:true})}},{text:'ͬ��',xtype:'checkcolumn',width:50,dataIndex:'isneedsyn',field:{xtype:'checkbox',fieldLabel:'',width:140,fieldname:'isneedsyn',cls:'x-grid-checkheader-editor'}},{text:'���ӿ��',xtype:'numbercolumn',width:60,format:'0.00',dataIndex:'addstockqty',field:{xtype:'numberfield',allowBlank:false,enableKeyEvents:true,value:0,format:'0.00',width:150,fieldLabel:'',fieldname:'addstockqty'}},{text:'���',xtype:'numbercolumn',width:60,format:'0',dataIndex:'stockcount'},{text:'ͬ�����ʧ��',xtype:'checkcolumn',width:80,dataIndex:'errflag'},{text:'������Ϣ',xtype:'gridcolumn',width:100,dataIndex:'errmsg'},{xtype:'buttoncolumn',header:'���',width:100,items:[{type:'space'},{type:'space'},{text:'ͬ��',width:50,style:'text',handler:function(b,c,a){b.up('baseeditfrm').updateItemStock(b.getStore().getAt(c).get('orgid'),b.getStore().getAt(c).get('itemid'));}},{type:'space'},{type:'space'},{text:'�鿴',width:50,style:'text',handler:function(a,h,j){var f=a.getStore().getAt(h).get('itemid');var b=a.getStore().getAt(h).get('orgid');var g=new Properties();g.add('orgid',b)add('itemid',f);var c=null;try{c=ECS.Remote.getInstance().loadData('getSkus',g.toString(),'ecsstockconfig',_local.user.name);}catch(d){alert('ȡ�����ϸʧ��:'+d.message);return;}var i=Ext.create('Ext.Window',{width:600,height:400,hidden:false,maximizable:false,title:'�����ϸ',layout:'fit',modal:true,items:[{xtype:'gridpanel',title:'',autoScroll:true,columnLines:true,viewConfig:{stripeRows:true},showSummary:true,features:[{disabled:false,ftype:'summary',showSummaryRow:true}],store:Ext.create('Ext.data.Store',{fields:[{name:'orgid',type:'int'},{name:'itemid',type:'string'},{name:'skuid',type:'string'},{name:'sku',type:'string'},{name:'stockcount',type:'int'},{name:'errflag',type:'bool'},{name:'errmsg',type:'string'}],autoDestroy:true,autoLoad:false,data:c}),columns:[{xtype:'gridcolumn',header:'SKU',dataIndex:'sku',width:150,summaryType:'count',summaryRenderer:function(l,e,k){return'�ϼ�('+l+')';}},{xtype:'gridcolumn',header:'����',dataIndex:'stockcount',width:80,summaryType:'sum',summaryRenderer:Ext.util.Format.numberRenderer('0.00')},{text:'ͬ�����ʧ��',xtype:'checkcolumn',width:80,dataIndex:'errflag'},{xtype:'gridcolumn',header:'������Ϣ',dataIndex:'errmsg',width:250}]}]});i.show();}}]},{text:'��������',xtype:'gridcolumn',width:60,dataIndex:'updator'},{text:'����޸�ʱ��',xtype:'datecolumn',width:140,format:'Y-m-d H:i:s.u',dataIndex:'updatetime'}],updateItemStock:function(c,b){var a=Ext.create('Ext.Window',{width:250,height:300,hidden:false,maximizable:false,title:'ͬ�����',layout:'fit',modal:true,buttons:[{text:'����ͬ��',icon:'images/buttonsearch.gif',handler:function(g){var d=this.up('window').down('form');var l=0;var o='';var k='';var f=1;for(var j=0;j<d.items.items[0].items.length;j++){if(d.items.items[0].items.items[j].xtype=='checkboxfield'&&d.items.items[0].items.items[j].getValue()){l=l+1;o=d.items.items[0].items.items[j].inputValue;if(o=='2'){k=d.items.items[0].items.items[j].nextSibling().getValue();}}}if(!d.items.items[0].items.items[0].getValue()){f=0;}if(l>1){alert('ͬ��ѡ��ֻ��ѡ��һ��!');return;}if(l==0){alert('δѡ��ͬ����!');return;}var h=new Ext.LoadMask(d.getEl(),{msg:'����ͬ��,���Ժ�...'});h.show();var n=new Properties();n.add('orgid',c);n.add('itemid',b);n.add('synstyle',o);n.add('synvalue',k);n.add('isupdateconfig',f);try{ECS.Remote.getInstance().postData('synStock',n.toString(),'ecsitem',_local.user.name);}catch(m){alert('ͬ�����ʧ��:'+m.message);h.hide();return;}h.hide();alert('ͬ�����ɹ�');a.close();}},{text:'ȡ��',icon:'images/buttonCloseHover.gif',handler:function(d){a.close();}}],items:[{xtype:'form',title:'',layout:{type:'vbox',padding:'5',align:'stretch'},defaults:{margins:'0 0 5 0'},closable:false,border:false,frame:true,autoScroll:true,fieldDefaults:{labelAlign:'right',disabled:false,readOnly:false},items:[{xtype:'fieldset',title:'ͬ��',checkboxToggle:false,collapsed:false,layout:'absolute',height:120,items:[{xtype:'checkboxfield',boxLabel:'���ÿ��',name:'synvalue',inputValue:'1',x:5,y:0,anchor:'80%'},{xtype:'checkboxfield',boxLabel:'���ÿ��',name:'synvalue',inputValue:'2',x:5,y:30,anchor:'40%'},{xtype:'numberfield',fieldLabel:'+����',x:10,y:30,anchor:'90%'},{xtype:'checkboxfield',boxLabel:'������',name:'synvalue',inputValue:'3',x:5,y:60,anchor:'80%'}]},{xtype:'fieldset',title:'����',checkboxToggle:false,collapsed:false,layout:'absolute',height:60,items:[{xtype:'checkboxfield',boxLabel:'��������',name:'updateconfig',x:5,y:0,checked:true,anchor:'50%'}]}]}],listeners:{beforeshow:{fn:function(d){}}}});a.show();},canDelete:false,canInsert:false,canEdit:true,canExport:true,canImport:true,canPagination:true,addMode:2,editMo1,newRecordConfig:{serialid:0,orgid:0,itemid:'',itemcode:'',title:'',alarmqty:0,alarmstyle:1,isneedsyn:1,addstockqty:0,stockcount:0,errflag:0,errmsg:'',creator:_local.user.name,createtime:new Date(),updator:_local.user.name,updatetime:new Date()},afterFormCreate:function(){this.getColumnByFieldName('orgid').field.store.loadData(Retail.getShop(),false);},beforeDelete:function(){return true;},afterSave:function(){return true;},afterPage:function(){}}";
	public static void main(String[] args) {
		try{
			insertImagetoDb("d:/����ϵͳ/��Ȩ����.bak");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		

	}
	
	public static void insertImagetoDb(String fileName) throws Exception{
		try {
			Connection conn= getConnection();
			FileInputStream fis = new FileInputStream(fileName);
			PreparedStatement pers = conn.prepareStatement("update ecs_module set jscode=? where moduleid=20000");
			//pers.setCharacterStream(1, new BufferedReader(new InputStreamReader(new FileInputStream(fileName))));
			//pers.setBytes(1, JS.getBytes());
			pers.setBinaryStream(1, fis);
			pers.executeUpdate();
			pers.close();
			conn.close();
			Log.info("����image���");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws Exception{
		Connection conn=null;
		DriverManager.registerDriver((Driver)Class.forName(DRIVER).newInstance());
		conn = DriverManager.getConnection(URL, USER, PASSWORD);
		return conn;
	}
	


}
