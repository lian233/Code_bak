package com.wofu.base.interfacemanager;


import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import com.wofu.common.tools.util.JException;
import com.wofu.common.tools.util.log.Log;
import com.wofu.base.job.timer.ECS_TimerPolicy;
import com.wofu.base.job.Executer;



public class InterfaceTimerExecuter extends Executer{
	
	private ECS_TimerPolicy job=null;
		
	public void run()  {

		job=(ECS_TimerPolicy) this.getExecuteobj();
		
		try
		{
			export();
			downnote();
			importing();
		}catch(Exception e)
		{
			
		}finally
		{
			try {
				this.getDao().freeConnection();
			} catch (Exception e) {
				Log.error("job", "�ر����ݿ�����ʧ��");
			}
		}
	}
	
	private void export() throws Exception
	{		
		String sql="select * from ecs_intfbuslist where executeflag=0";		
		Vector vt=this.getDao().multiRowSelect(sql);
		
		for(int i=0;i<vt.size();i++)
		{				
			Hashtable ht=(Hashtable) vt.get(i);
			
			ECS_IntfBusList intfbuslist=new ECS_IntfBusList();
			intfbuslist.getMapData(ht);
			
			int busid=intfbuslist.getBusid();
			int bustype=intfbuslist.getBustype();
			int interfaceid=intfbuslist.getInterfaceid();
			
			Map htinterfaceinfo=IntfHelper.getInterfaceInfo(this.getDao(), bustype, interfaceid);
			
			String exportclass=htinterfaceinfo.get("exportclass").toString();
			
			if (exportclass.equals("")) continue;
			
			System.out.println(exportclass);
						
			InterfaceProcessor interfaceprocessor=(InterfaceProcessor) Class.forName(exportclass).newInstance();
	
			interfaceprocessor.setBusid(String.valueOf(busid));
			interfaceprocessor.setDao(this.getDao());
			interfaceprocessor.setBustype(bustype);
			interfaceprocessor.setNoteid(Integer.valueOf(htinterfaceinfo.get("noteid").toString()).intValue());
			interfaceprocessor.setVertifycode(htinterfaceinfo.get("vertifycode").toString());
			interfaceprocessor.setExtinterfaceid(Integer.valueOf(htinterfaceinfo.get("extinterfaceid").toString()).intValue());
			interfaceprocessor.setOrgid(Integer.valueOf(htinterfaceinfo.get("orgid").toString()).intValue());
				

			try
			{
				interfaceprocessor.getExtDao().setTransation(false);
				this.getDao().setTransation(false);
	
				interfaceprocessor.execute();
			

				ECS_IntfBusListBak intfbuslistbak=new ECS_IntfBusListBak();
				intfbuslist.copyTo(intfbuslistbak);
				
				intfbuslistbak.setExecuteflag(1);
				intfbuslistbak.setStime(new java.sql.Timestamp(System.currentTimeMillis()));
				
				this.getDao().insert(intfbuslistbak);
			
				this.getDao().deleteByKeys(intfbuslist, "busid,sheettype,interfaceid");
				
				this.getDao().commit();
				this.getDao().setTransation(true); 
				
				interfaceprocessor.getExtDao().commit();
				interfaceprocessor.getExtDao().setTransation(true); 
				
				
				Log.info(job.getNotes(), "����ͻ�("+interfaceprocessor.getVertifycode()+")ҵ���:"+intfbuslist.getBusid()+",��������:"+intfbuslist.getBustype());
			}catch(Exception e)
			{
				if (!this.getDao().getConnection().getAutoCommit()
						||!interfaceprocessor.getExtDao().getConnection().getAutoCommit())
				{
					try
					{
						this.getDao().rollback();
						interfaceprocessor.getExtDao().rollback();
					}
					catch (Exception rex) 
					{ 
						throw new JException("�ع�����ʧ��",rex);
					}
					try
					{
						this.getDao().setTransation(true);
						interfaceprocessor.getExtDao().setTransation(true);
					}
					catch (Exception cex) 
					{ 
						throw new JException("�����Զ��ύ����ʧ��",cex);
					}
				}
				throw e;

			}
							
		}
			
	
	}
	
	private void importing() throws Exception
	{
		try
		{
			
			String sql="select * from ecs_upnote where flag=0";
			
			Vector vt=this.getDao().multiRowSelect(sql);
			
			for(int i=0;i<vt.size();i++)
			{
				this.getDao().setTransation(false);
				
				Hashtable ht=(Hashtable) vt.get(i);
				
				ECS_UpNote upnote=new ECS_UpNote();
				upnote.getMapData(ht);
								
				int bustype=upnote.getBustype();
				
				Map importinterfaceinfo=IntfHelper.getImportInterfaceInfo(this.getDao(), bustype, upnote.getOwner());
	
				if (importinterfaceinfo.get("upnoteclass").toString().equals("") || importinterfaceinfo.isEmpty()) continue;
				
				InterfaceProcessor upnoteprocessor=(InterfaceProcessor) Class.forName(importinterfaceinfo.get("upnoteclass").toString()).newInstance();
	
				upnoteprocessor.setBusid(upnote.getOwnerid());
				upnoteprocessor.setDao(this.getDao());
		
				upnoteprocessor.setBustype(bustype);
				upnoteprocessor.setNoteid(upnote.getNoteid());				
				upnoteprocessor.setVertifycode(upnote.getOwner());
				
				upnoteprocessor.setMerchantid(Integer.valueOf(importinterfaceinfo.get("merchantid").toString()).intValue());
						
			
				upnoteprocessor.execute();
			
				
				ECS_UpNoteBak upnotebak=new ECS_UpNoteBak();
				upnote.copyTo(upnotebak);
				
				upnotebak.setFlag(1);
				upnotebak.setHandletime(new Date());
				
				this.getDao().insert(upnotebak);
			
				this.getDao().delete(upnote);
				
				Log.info(job.getNotes(), "����ͻ�("+upnote.getOwner()+")�ӿڵ�:"+upnote.getOwnerid()+",��������:"+upnote.getBustype());
				
				this.getDao().commit();
				this.getDao().setTransation(true);
								
			}
		
		}catch(Exception e)
		{
			if (!this.getDao().getConnection().getAutoCommit())
			{
				try
				{
					this.getDao().rollback();
				}
				catch (Exception rollbackexception) 
				{ 
					throw new JException("�ع�����ʧ��",rollbackexception);
				}
				try
				{
					this.getDao().setTransation(true);
				}
				catch (Exception commitexception) 
				{ 
					throw new JException("�����Զ��ύ����ʧ��",commitexception);
				}
			}
			throw e;

		}
	}
	
	private void downnote() throws Exception
	{
		try
		{
			
			String sql="select * from ecs_downnote where flag=0";
			
			Vector vt=this.getDao().multiRowSelect(sql);
			
			for(int i=0;i<vt.size();i++)
			{
				this.getDao().setTransation(false);
				
				Hashtable ht=(Hashtable) vt.get(i);
				
				ECS_DownNote downnote=new ECS_DownNote();
				downnote.getMapData(ht);
								
				int bustype=downnote.getBustype();
				
				Map importinterfaceinfo=IntfHelper.getDownNoteInterfaceInfo(this.getDao(), bustype, downnote.getOwner());
				
				if (importinterfaceinfo.get("downnoteclass").toString().equals("") || importinterfaceinfo.isEmpty()) continue;
				
				
				InterfaceProcessor downnoteprocessor=(InterfaceProcessor) Class.forName(importinterfaceinfo.get("downnoteclass").toString()).newInstance();
				
	
	
				downnoteprocessor.setBusid(downnote.getOwnerid());
				downnoteprocessor.setDao(this.getDao());
		
				downnoteprocessor.setBustype(bustype);
				downnoteprocessor.setNoteid(downnote.getNoteid());				
				downnoteprocessor.setVertifycode(downnote.getOwner());
		
				downnoteprocessor.setMerchantid(Integer.valueOf(importinterfaceinfo.get("merchantid").toString()).intValue());
		
			
				downnoteprocessor.execute();
				
			
				ECS_DownNoteBak downnotebak=new ECS_DownNoteBak();
				downnote.copyTo(downnotebak);
				
				downnotebak.setFlag(1);
				downnotebak.setHandletime(new Date());
				
				this.getDao().insert(downnotebak);
			
				this.getDao().delete(downnote);
				
				Log.info(job.getNotes(), "����ͻ�("+downnote.getOwner()+")�ӿڵ�:"+downnote.getOwnerid()+",��������:"+downnote.getBustype());
				
				this.getDao().commit();
				this.getDao().setTransation(true);
								
			}
		
		}catch(Exception e)
		{
			if (!this.getDao().getConnection().getAutoCommit())
			{
				try
				{
					this.getDao().rollback();
				}
				catch (Exception rollbackexception) 
				{ 
					throw new JException("�ع�����ʧ��",rollbackexception);
				}
				try
				{
					this.getDao().setTransation(true);
				}
				catch (Exception commitexception) 
				{ 
					throw new JException("�����Զ��ύ����ʧ��",commitexception);
				}
			}
			throw e;

		}
	}
	
	/*
	private void upnote() throws Exception
	{
		try
		{
			this.getDao().setTransation(false);
			
			String sql="select * from ecs_intfsheetlist where executeflag=0";
			
			Vector vt=this.getDao().multiRowSelect(sql);
			
			for(int i=0;i<vt.size();i++)
			{
				Hashtable ht=(Hashtable) vt.get(i);
				
				ECS_IntfSheetList intfsheetlist=new ECS_IntfSheetList();
				intfsheetlist.getMapData(ht);
				
				int busid=intfsheetlist.getBusid();
				int sheettype=intfsheetlist.getSheettype();
				int interfaceid=intfsheetlist.getInterfaceid();
				
				Hashtable htinterfaceinfo=IntfHelper.getInterfaceInfo(this.getDao(), sheettype, interfaceid);
				
				String exportclass=htinterfaceinfo.get("exportclass").toString();
				
				InterfaceProcessor interfaceprocessor=(InterfaceProcessor) Class.forName(exportclass).newInstance();
				
	
				interfaceprocessor.setBusid(busid);
				interfaceprocessor.setDao(this.getDao());
				interfaceprocessor.setSheettype(sheettype);
				interfaceprocessor.setNoteid(Integer.valueOf(htinterfaceinfo.get("noteid").toString()).intValue());
				interfaceprocessor.setInterfacecode(htinterfaceinfo.get("interfacecode").toString());
				interfaceprocessor.setVertifycode(htinterfaceinfo.get("vertifycode").toString());
				interfaceprocessor.setExtinterfaceid(Integer.valueOf(htinterfaceinfo.get("extinterfaceid").toString()).intValue());
				interfaceprocessor.setOrgid(Integer.valueOf(htinterfaceinfo.get("orgid").toString()).intValue());
		
				interfaceprocessor.execute();
			
				ECS_IntfSheetListBak intfsheetlistbak=new ECS_IntfSheetListBak();
				intfsheetlist.copyTo(intfsheetlistbak);
				
				intfsheetlistbak.setExecuteflag(1);
				intfsheetlistbak.setStime(new java.sql.Timestamp(System.currentTimeMillis()));
				
				this.getDao().insert(intfsheetlistbak);
			
				this.getDao().deleteByKeys(intfsheetlist, "busid,sheettype,interfaceid");
								
			}
			this.getDao().commit();
			this.getDao().setTransation(true);
		}catch(Exception e)
		{
			if (!this.getDao().getConnection().getAutoCommit())
			{
				try
				{
					this.getDao().rollback();
				}
				catch (Exception rollbackexception) 
				{ 
					throw new JException("�ع�����ʧ��",rollbackexception);
				}
				try
				{
					this.getDao().setTransation(true);
				}
				catch (Exception commitexception) 
				{ 
					throw new JException("�����Զ��ύ����ʧ��",commitexception);
				}
			}
			throw e;

		}finally
		{
			try {
				this.getDao().freeConnection();
			} catch (Exception e) {
				Log.error("job", "�ر����ݿ�����ʧ��");
			}
		}
	}
	*/
}
