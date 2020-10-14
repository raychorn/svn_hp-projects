package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;
import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.server.provider.data.ClusterDetailResult;
import com.hp.asi.hpic4vc.server.provider.data.Cpu;
import com.hp.asi.hpic4vc.server.provider.data.Event;
import com.hp.asi.hpic4vc.server.provider.data.Firmware;
import com.hp.asi.hpic4vc.server.provider.data.Host;
import com.hp.asi.hpic4vc.server.provider.data.IloPower;
import com.hp.asi.hpic4vc.server.provider.data.Ilolog;
import com.hp.asi.hpic4vc.server.provider.data.Iml;
import com.hp.asi.hpic4vc.server.provider.data.MemoryModules;
import com.hp.asi.hpic4vc.server.provider.data.Software;
import com.hp.asi.hpic4vc.server.provider.data.Status;
import com.hp.asi.hpic4vc.server.provider.model.ClusterDetailModel;
import com.hp.asi.hpic4vc.server.provider.model.ClusterHostDetail;
import com.hp.asi.hpic4vc.server.provider.model.ClusterModel;

public class ClusterDetailAdapter extends DataAdapter<ClusterDetailResult,ClusterDetailModel> {

     private static final String SERVICE_NAME = "services/host/clusterdetail";
	
	public ClusterDetailAdapter()
	{
		super(ClusterDetailResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}
	
	private TableModel formatImlLog(Iml imlLog){
		
		TableModel imlLogTableModel = new TableModel();
		
		List<Event> tempEntry = null;				
		tempEntry = imlLog.getEvent();
		if(tempEntry!= null){
			
			imlLogTableModel.columnNames.add("Severity");
			imlLogTableModel.columnNames.add("Class");
			imlLogTableModel.columnNames.add("Last Update");
			imlLogTableModel.columnNames.add("Initial Update");
			imlLogTableModel.columnNames.add("Count");
			imlLogTableModel.columnNames.add("Description");
			
		
			for (Event item : imlLog.getEvent())
			{
				List<String> row = new ArrayList<String>();
				row.add(item.getSeverity());
				row.add(item.getClassz());
				row.add(item.getLast_update());
				row.add(item.getInitial_update());
				row.add(item.getCount());
				row.add(item.getDescription());
				imlLogTableModel.rowFormattedData.add(row);
			}

		}
		
		
		return imlLogTableModel;
	}
	
	
	
	
	private TableModel formatIloLog(Ilolog iloLog){
		
		TableModel iloLogTableModel = new TableModel();
		List<Event> tempEntry = null;				
		tempEntry =  iloLog.getEvent();      
		
	     if(tempEntry!= null){
				
	    	 iloLogTableModel.columnNames.add("Severity");
	    	 iloLogTableModel.columnNames.add("Class");
	    	 iloLogTableModel.columnNames.add("Last Update");
	    	 iloLogTableModel.columnNames.add("Initial Update");
	    	 iloLogTableModel.columnNames.add("Count");
	    	 iloLogTableModel.columnNames.add("Description");
			
	    	 for (Event item : iloLog.getEvent())
				{
					List<String> row = new ArrayList<String>();
					row.add(item.getSeverity());
					row.add(item.getClassz());
					row.add(item.getLast_update());
					row.add(item.getInitial_update());
					row.add(item.getCount());
					row.add(item.getDescription());
					iloLogTableModel.rowFormattedData.add(row);
				}
	
			}
		
		return iloLogTableModel;
		
	 }
	
	private TableModel formatSoftwareInformation(List<Software> softwareList){

		TableModel softwareInfo = new TableModel();
		
		softwareInfo.columnNames.add("Name");
		softwareInfo.columnNames.add("Description");
		softwareInfo.columnNames.add("Version");	
		
		for (Software item : softwareList)
		{
			List<String> row = new ArrayList<String>();
			row.add(item.getName());			
			row.add(item.getDescription());
			row.add(item.getVersion());			
			softwareInfo.rowFormattedData.add(row);
		}
		
		return softwareInfo;
	}
	
	
	private TableModel formatFirmareInformation(List<Firmware> firmwareList){
		
		TableModel firmwareInfo = new TableModel();
		
		firmwareInfo.columnNames.add("Name");
		firmwareInfo.columnNames.add("Description");
		firmwareInfo.columnNames.add("Version");	
		
		for (Firmware item : firmwareList)
		{
			List<String> row = new ArrayList<String>();
			row.add(item.getName());			
			row.add(item.getDescription());
			row.add(item.getVersion());			
			firmwareInfo.rowFormattedData.add(row);
		}
		
		return firmwareInfo;
		
	}
	
	
	
	private TableModel formatCpuInformation(List<Cpu> cpuList){
		
		TableModel cpuInfo = new TableModel();
		if(cpuList.isEmpty())
		{
			return cpuInfo;
		}
		
		cpuInfo.columnNames.add("Name");
		cpuInfo.columnNames.add("Vendor");
		cpuInfo.columnNames.add("Description");
		cpuInfo.columnNames.add("Threads");
		cpuInfo.columnNames.add("Cores");
		cpuInfo.columnNames.add("Speed");
		
		for (Cpu cpuItem : cpuList)
		{
			List<String> row = new ArrayList<String>();
			if(cpuItem.getSpeed().equals("0 MHz"))
			{
				row.add(cpuItem.getName());
				row.add("Absent");
				row.add("");
				row.add("");
				row.add("");
				row.add("");

			}
			else
			{
				row.add(cpuItem.getName());
				row.add(cpuItem.getVendor());
				row.add(cpuItem.getDescription());
				row.add(cpuItem.getThreads());
				row.add(cpuItem.getCores());
				row.add(cpuItem.getSpeed());
			}
			cpuInfo.rowFormattedData.add(row);
		}
		
		return cpuInfo;
	}
	
	private TableModel formatMemoryModel(List<MemoryModules> memList){
		
		TableModel memoryModules = new TableModel();
		
		if(memList.isEmpty())
		{
			return memoryModules;
		}
		
		memoryModules.columnNames.add("Location");
		memoryModules.columnNames.add("Size");
		memoryModules.columnNames.add("Speed");
		
		for (MemoryModules memItem : memList)
		{
			List<String> row = new ArrayList<String>();
			row.add(memItem.getName());
			row.add(memItem.getSize());
			row.add(memItem.getSpeed());
			memoryModules.rowFormattedData.add(row);
		}
		
		
		return memoryModules;
	}
	
	private LabelValueListModel formatServerPower(IloPower iloPower){
		
		String presentPowerReading =null;;
		String averagePowerReading= null;
		String minimumPowerReading= null;
		String maximumPowerReading=  null;
		
		LabelValueListModel iPower = new LabelValueListModel();
		if(iloPower.getPresent_power_reading()!=null){
			 presentPowerReading  = iloPower.getPresent_power_reading().getValue()+" "+iloPower.getPresent_power_reading().getUnit().toString();

		}
		if(iloPower.getAverage_power_reading()!=null){
		     averagePowerReading =  iloPower.getAverage_power_reading().getValue()+" "+iloPower.getAverage_power_reading().getUnit().toString();
		}
		if(iloPower.getMinimum_power_reading()!=null){
			 minimumPowerReading  = iloPower.getMinimum_power_reading().getValue()+" "+iloPower.getMinimum_power_reading().getUnit().toString();

		}
		if(iloPower.getMaximum_power_reading()!=null){
			 maximumPowerReading  = iloPower.getMaximum_power_reading().getValue()+" "+iloPower.getMaximum_power_reading().getUnit().toString();
		}
		
		
		iPower.addLabelValuePair("Present Power Reading",presentPowerReading);
		iPower.addLabelValuePair("Average Power Reading",averagePowerReading);
		iPower.addLabelValuePair("Minimum Power Reading",minimumPowerReading);
		iPower.addLabelValuePair("Maximum Power Reading",maximumPowerReading);
		
		return iPower;
	}
	
	// This method returns the ServerStatusInformation for the Host in the Cluster
		private LabelValueListModel setServerStatusToNull(Status status){
			
			LabelValueListModel serverStatusInfo  = new LabelValueListModel();
			serverStatusInfo.addLabelValuePair("Overall","");
			serverStatusInfo.addLabelValuePair("CPU","");
			serverStatusInfo.addLabelValuePair("Memory","");
			serverStatusInfo.addLabelValuePair("Temperature","");
    		serverStatusInfo.addLabelValuePair("Fan","");
			serverStatusInfo.addLabelValuePair("Network","");
			serverStatusInfo.addLabelValuePair("iLo","");
			serverStatusInfo.addLabelValuePair("IML","");
			serverStatusInfo.addLabelValuePair("ASR","");
			serverStatusInfo.addLabelValuePair("PS","");
			return serverStatusInfo;
		}
	
	
	// This method returns the ServerStatusInformation for the Host in the Cluster
	private LabelValueListModel formatServerStatus(Status status){
		
		LabelValueListModel serverStatusInfo  = new LabelValueListModel();
		if(status.getOverall()!=null){
			serverStatusInfo.addLabelValuePair("Overall",status.getOverall());
		}else {
			serverStatusInfo.addLabelValuePair("Overall","");
		}
		
		if(status.getCpu()!=null){
			serverStatusInfo.addLabelValuePair("CPU",status.getCpu());
		}else {
			serverStatusInfo.addLabelValuePair("CPU","");
		}
		
		if(status.getMemory()!=null){
			serverStatusInfo.addLabelValuePair("Memory",status.getMemory());
		}else {
			serverStatusInfo.addLabelValuePair("Memory","");
		}
		
		if(status.getTemp_sensor()!=null){
			serverStatusInfo.addLabelValuePair("Temperature",status.getTemp_sensor());
		}else {
			serverStatusInfo.addLabelValuePair("Temperature","");
		}
		
		if(status.getFan()!=null){
			serverStatusInfo.addLabelValuePair("Fan",status.getFan());
		}else {
			serverStatusInfo.addLabelValuePair("Fan","");
		}
		
		if(status.getNetwork()!=null){
			serverStatusInfo.addLabelValuePair("Network",status.getNetwork());
		}else {
			serverStatusInfo.addLabelValuePair("Network","");
		}
		
		if(status.getIlo()!=null){
			serverStatusInfo.addLabelValuePair("iLO",status.getIlo());
		}else {
			serverStatusInfo.addLabelValuePair("iLO","");
		}
		
		if(status.getIml()!=null){
			serverStatusInfo.addLabelValuePair("IML",status.getIml());
		}else {
			serverStatusInfo.addLabelValuePair("IML","");
		}
		
		if(status.getAsr()!=null){
			serverStatusInfo.addLabelValuePair("ASR",status.getAsr());
		}else {
			serverStatusInfo.addLabelValuePair("ASR","");
		}
		
		if(status.getPs()!=null){
			serverStatusInfo.addLabelValuePair("PS",status.getPs());
		}else {
			serverStatusInfo.addLabelValuePair("PS","");
		}
		
		
		return serverStatusInfo;
	}
	
	
	// This method returns HostInformation of the Host in the Cluster
	private LabelValueListModel formatHostInfoData(Host host){
	
	     
		LabelValueListModel hostInfoData = new LabelValueListModel();
		if(host.getIlo_server_name()!=null){
			hostInfoData.addLabelValuePair("Server Name",host.getIlo_server_name());
		}else {
			hostInfoData.addLabelValuePair("Server Name","");
		}
	   if(host.getVmware_name()!=null){
		   hostInfoData.addLabelValuePair("VMWare Host Name",host.getVmware_name()); 
	   }else {
		   hostInfoData.addLabelValuePair("VMWare Host Name","");
	   }
	   if(host.getProduct_name()!=null){
		   hostInfoData.addLabelValuePair("Product Name", host.getProduct_name());
	   }else{
		   hostInfoData.addLabelValuePair("Product Name", "");
	   }
	   
	   if(host.getUuid()!=null){
		   hostInfoData.addLabelValuePair("UUID", host.getUuid());
	   }else{
		   hostInfoData.addLabelValuePair("UUID", "");
	   }
	   
	   if(host.getIlo_address()!=null){
		   hostInfoData.addLabelValuePair("iLO Address ",host.getIlo_address()); 
	   }else {
		   hostInfoData.addLabelValuePair("iLO Address","");
	   }
	   
	   if(host.getIlo_license_type()!=null){
		   hostInfoData.addLabelValuePair("iLO License Type",host.getIlo_license_type()); 
	   }else {
		   hostInfoData.addLabelValuePair("iLO License Type","");
	   }
	   
	   if(host.getIlo_firmware_version()!=null){
	       hostInfoData.addLabelValuePair("iLO Firmware Version",host.getIlo_firmware_version());
	   }else {
			hostInfoData.addLabelValuePair("iLO Firmware Version","");
	   }
	   if(host.getUid_status()!=null){
		   hostInfoData.addLabelValuePair("UID Status",host.getUid_status());
	   }else {
		   hostInfoData.addLabelValuePair("UID Status","");
	   }
	   if(host.getPower_status()!=null){
		   hostInfoData.addLabelValuePair("Host Power Status",host.getPower_status());
	   }else {
		   hostInfoData.addLabelValuePair("Host Power Status","");
	   }
	   
	   if(host.getTotal_cpu()!=null){
		   hostInfoData.addLabelValuePair("Total CPU",host.getTotal_cpu());
	   }else {
		   hostInfoData.addLabelValuePair("Total CPU","");
	   }
		
	   if(host.getTotal_memory()!=null){
		   hostInfoData.addLabelValuePair("Total Memory",host.getTotal_memory());
	   }else {
		   hostInfoData.addLabelValuePair("Total Memory","");
	   }	
		
	   if(host.getTotal_nics()!=null){
		   hostInfoData.addLabelValuePair("Total NICs",host.getTotal_nics());
	   }else {
		   hostInfoData.addLabelValuePair("Total NICs","");
	   }	
	
	   if(host.getTotal_storage()!=null){
		   hostInfoData.addLabelValuePair("Total Smart Array",host.getTotal_storage());
	   }else {
		   hostInfoData.addLabelValuePair("Total Smart Array","");
	   }	 
	   
	   if(host.getRom()!=null){
		   hostInfoData.addLabelValuePair("System ROM",host.getRom());
	   }else {
		   hostInfoData.addLabelValuePair("System ROM","");
	   }
		
	   if(host.getBackup_rom()!=null){
		   hostInfoData.addLabelValuePair("Backup ROM",host.getBackup_rom());
	   }else {
		   hostInfoData.addLabelValuePair("Backup ROM","");
	   }
	   
		String bundleStatus = "";
		if(host.getHp_bundle_status() != null)
		{
			bundleStatus = "Installed";
		}else if(host.getHp_bundle_status() != null){
			bundleStatus = "Not Installed";  	
		}else {
		   bundleStatus = "";
		}	
		
		hostInfoData.addLabelValuePair("HP Insight Provider Bundle",bundleStatus);
	   
		
		return hostInfoData;
	}
	
	private ClusterHostDetail listOfLabelValueModels(Host host,ClusterHostDetail clusterHostDetail){
		
		
		clusterHostDetail.hostInfo = this.formatHostInfoData(host);
		if(host.getStatus()!=null){
			clusterHostDetail.serverStatus = this.formatServerStatus(host.getStatus());
		}else {
			clusterHostDetail.serverStatus = this.setServerStatusToNull(host.getStatus());
		}
	
		if(host.getIlo_power()!= null){
			clusterHostDetail.serverPower =  this.formatServerPower(host.getIlo_power());
		}
		// Commented out the code for showing ServerPower Staus Not Available in the Front end.
//		}else{
////			LabelValueListModel setIPoweNull = new LabelValueListModel();
////			setIPoweNull.addLabelValuePair("Present Power Reading","");
////			setIPoweNull.addLabelValuePair("Average Power Reading","");
////			setIPoweNull.addLabelValuePair("Minimum Power Reading","");
////			setIPoweNull.addLabelValuePair("Maximum Power Reading","");
////			clusterHostDetail.serverPower = setIPoweNull;
//			clusterHostDetail.ser
//		}
	
		return clusterHostDetail;
	}
	
	private ClusterHostDetail listOfTableModels(Host host,ClusterHostDetail clusterHostDetail){
		
		
		if(host.getMemory_modules()!=null){
			clusterHostDetail.memoryInfo = this.formatMemoryModel(host.getMemory_modules());
		}
		if(host.getCpus()!=null){
			clusterHostDetail.cpuInfo = this.formatCpuInformation(host.getCpus());
		}
		if(host.getFirmware()!=null){
			clusterHostDetail.firmwareInfo = this.formatFirmareInformation(host.getFirmware());	
		}
		
		if(host.getSoftware()!=null){
			clusterHostDetail.softwareInfo = this.formatSoftwareInformation(host.getSoftware());	
		}
		if(host.getIlolog()!=null){
			clusterHostDetail.iloLog = this.formatIloLog(host.getIlolog());	
		}
		if(host.getIml()!=null){
			clusterHostDetail.imlLog = this.formatImlLog(host.getIml());	
		}
		
		return clusterHostDetail;
	}
	
	
	// This method returns Cluster's Main Table
	private ClusterModel formatClusterDetailTable(Host host){
		
		ClusterModel clusterDetailTableModel = new ClusterModel();
        ClusterHostDetail clusterHostDetail = new ClusterHostDetail();
        
		clusterDetailTableModel.setVmware_name(host.getVmware_name());
		clusterDetailTableModel.setIlo_server_name(host.getIlo_server_name());
		clusterDetailTableModel.setProduct_name(host.getProduct_name());
		clusterDetailTableModel.setTotal_cpu(host.getTotal_cpu());
		clusterDetailTableModel.setTotal_memory(host.getTotal_memory());
		clusterDetailTableModel.setTotal_nics(host.getTotal_nics());
		clusterDetailTableModel.setTotal_storage(host.getTotal_storage());
		clusterDetailTableModel.setTotal_storage(host.getTotal_storage());
		if(host.getPower_cost_advantage()!=null){
		    clusterDetailTableModel.setPower_cost_advantage(host.getPower_cost_advantage());	
		}else {
			 clusterDetailTableModel.setPower_cost_advantage("");
		}
		
		clusterHostDetail = listOfLabelValueModels(host,clusterHostDetail);
		clusterHostDetail = listOfTableModels(host,clusterHostDetail);
				
		clusterDetailTableModel.children.add(clusterHostDetail);
		
		return clusterDetailTableModel;
	}
	
	@Override
	public ClusterDetailModel formatData(ClusterDetailResult rawData)
	{
        log.debug("ClusterDetailAdapter beginning" );
        
        ClusterDetailModel model = new ClusterDetailModel();
		
		if (rawData.hasError()) {
            log.debug("ClusterDetailResults had an error message.  " +
                    "Returning a ClusterDetailModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		List<Host> hostList = rawData.getHosts();
		
		for(Host host : hostList){
			model.hosts.add(this.formatClusterDetailTable(host));
		}
		
		
        log.debug("ClusterDetailAdapter the end" );
		log.debug(model.toString());
		return model;
	}

	@Override
	public ClusterDetailModel getEmptyModel()
	{
		return new ClusterDetailModel();
	}
}
