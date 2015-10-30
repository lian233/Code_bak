package vipapis.schedule;

import java.util.Set;
import java.util.List;
import java.util.Map;
import com.vip.osp.sdk.exception.OspException;



public interface ScheduleService {
	
	
	
	List<vipapis.schedule.Schedule> getScheduleList( vipapis.common.Warehouse warehouse,   String start_date,   String end_date,   Integer page,   Integer limit ) throws OspException;
	
	
	vipapis.schedule.GetScheduleListResponse getSchedules( vipapis.common.Warehouse warehouse,   String start_date,   String end_date,   String schedule_id,   String channel_id,   Integer page,   Integer limit ) throws OspException;
	
}