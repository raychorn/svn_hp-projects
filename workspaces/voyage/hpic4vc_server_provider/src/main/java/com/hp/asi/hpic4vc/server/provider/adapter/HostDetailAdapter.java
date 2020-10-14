package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.server.provider.data.CpuInfo;
import com.hp.asi.hpic4vc.server.provider.data.HostDetailResult;
import com.hp.asi.hpic4vc.server.provider.data.ImlLog;
import com.hp.asi.hpic4vc.server.provider.data.ImlLogEntry;
import com.hp.asi.hpic4vc.server.provider.data.MemoryModule;
import com.hp.asi.hpic4vc.server.provider.data.SoftwareFirmware;
import com.hp.asi.hpic4vc.server.provider.model.HostDetailModel;
import com.hp.asi.hpic4vc.provider.model.LabelValueModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;


public class HostDetailAdapter extends DataAdapter<HostDetailResult, HostDetailModel>
{
	private static final String SERVICE_NAME = "services/host/hostdetail";
	
	public HostDetailAdapter()
	{
		super(HostDetailResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	private HostDetailModel formatHostInfoData(HostDetailResult rawData, HostDetailModel model)
	{	
		model.hostInfo.addLabelValuePair("Server Name", rawData.getIlo_server_name());
		model.hostInfo.addLabelValuePair("VMWare Host Name", rawData.getVmware_name());
		model.hostInfo.addLabelValuePair("Product Name", rawData.getProduct_name());
		model.hostInfo.addLabelValuePair("UUID", rawData.getUuid());
		
		model.hostInfo.addLabelValuePair("iLO Address", rawData.getIlo_address());
		model.hostInfo.addLabelValuePair("iLO License Type", rawData.getIlo_license_type());
		model.hostInfo.addLabelValuePair("iLO Firmware Version", rawData.getIlo_firmware_version());
		model.hostInfo.addLabelValuePair("UID Status", rawData.getUid_status());
		if (rawData.getStatus() != null){
			model.hostInfo.addLabelValuePair("Host Power Status", rawData.getPower_status());
		}
		model.hostInfo.addLabelValuePair("Total CPU", rawData.getTotal_cpu());
		model.hostInfo.addLabelValuePair("Total Memory", rawData.getTotal_memory());
		model.hostInfo.addLabelValuePair("Total NICs", Integer.toString(rawData.getTotal_nics()));
		model.hostInfo.addLabelValuePair("Total Smart Array", rawData.getTotal_storage());
		model.hostInfo.addLabelValuePair("System ROM", rawData.getRom());
		model.hostInfo.addLabelValuePair("Backup ROM", rawData.getBackup_rom());
		
		String bundleStatus = "";
		if(rawData.getHp_bundle_status() != null && rawData.getHp_bundle_status())
		{
			bundleStatus = "Installed";
		}else if(rawData.getHp_bundle_status() != null && !rawData.getHp_bundle_status()){
			bundleStatus = "Not Installed";  	
		}else {
		   bundleStatus = "";
		}	
		
		model.hostInfo.addLabelValuePair("HP Insight Provider Bundle", bundleStatus);
				
		return model;
	}
	
	private HostDetailModel formatServerStatusData(HostDetailResult rawData, HostDetailModel model)
	{
		if(rawData.getStatus() == null)
		{
			return model;
		}
		model.serverStatus.addLabelValuePair("Overall", rawData.getStatus().getOverall());
		model.serverStatus.addLabelValuePair("CPU", rawData.getStatus().getCpu());
		model.serverStatus.addLabelValuePair("Memory", rawData.getStatus().getMemory());
		model.serverStatus.addLabelValuePair("Temperature", rawData.getStatus().getTemp_sensor());
		model.serverStatus.addLabelValuePair("Fan", rawData.getStatus().getFan());
		model.serverStatus.addLabelValuePair("Network", rawData.getStatus().getNetwork());
		model.serverStatus.addLabelValuePair("Storage", rawData.getStatus().getStorage());
		model.serverStatus.addLabelValuePair("iLO", rawData.getStatus().getIlo());
		model.serverStatus.addLabelValuePair("IML", rawData.getStatus().getIml());
		model.serverStatus.addLabelValuePair("ASR", rawData.getStatus().getAsr());
		model.serverStatus.addLabelValuePair("PS", rawData.getStatus().getPs());
		
		return model;
	}
	
	private HostDetailModel formatServerPowerData(HostDetailResult rawData, HostDetailModel model)
	{
		if(rawData.getIlo_power() == null)
		{
			return model;
		}
		model.serverPower.addLabelValuePair("Present Power Reading", rawData.getIlo_power().getPresent_power_reading().toString());		
		model.serverPower.addLabelValuePair("Average Power Reading", rawData.getIlo_power().getAverage_power_reading().toString());
		model.serverPower.addLabelValuePair("Minimum Power Reading", rawData.getIlo_power().getMinimum_power_reading().toString());
		model.serverPower.addLabelValuePair("Maximum Power Reading", rawData.getIlo_power().getMaximum_power_reading().toString());
		
		return model;
	}
	
	private HostDetailModel formatMemoryInfo(HostDetailResult rawData, HostDetailModel model)
	{
		List<MemoryModule>memList = rawData.getMemory_modules(); 
		if(memList.isEmpty())
		{
			return model;
		}
		
		model.memoryInfo.columnNames.add("Location");
		model.memoryInfo.columnNames.add("Size");
		model.memoryInfo.columnNames.add("Speed");
		
		for (MemoryModule memItem : memList)
		{
			List<String> row = new ArrayList<String>();
			row.add(memItem.getName());
			row.add(memItem.getSize());
			row.add(memItem.getSpeed());
			model.memoryInfo.rowFormattedData.add(row);
		}
		
		return model;
	}
	
	private HostDetailModel formatCpuInfo(HostDetailResult rawData, HostDetailModel model)
	{
		List<CpuInfo>cpuList = rawData.getCpus(); 
		if(cpuList.isEmpty())
		{
			return model;
		}
		
		model.cpuInfo.columnNames.add("Name");
		model.cpuInfo.columnNames.add("Vendor");
		model.cpuInfo.columnNames.add("Description");
		model.cpuInfo.columnNames.add("Threads");
		model.cpuInfo.columnNames.add("Cores");
		model.cpuInfo.columnNames.add("Speed");
		
		for (CpuInfo cpuItem : cpuList)
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
			model.cpuInfo.rowFormattedData.add(row);
		}
		
		return model;
	}
	
	private HostDetailModel formatSoftwareFirmware(HostDetailResult rawData, HostDetailModel model)
	{
		List<SoftwareFirmware>softwareList = rawData.getSoftware();
		List<SoftwareFirmware>firmwareList = rawData.getFirmware();
		
		model.softwareInfo.columnNames.add("Name");
		model.softwareInfo.columnNames.add("Description");
		model.softwareInfo.columnNames.add("Version");		
		
		for (SoftwareFirmware item : softwareList)
		{
			List<String> row = new ArrayList<String>();
			row.add(item.getName());			
			row.add(item.getDescription());
			row.add(item.getVersion());			
			model.softwareInfo.rowFormattedData.add(row);
		}
		
		model.firmwareInfo.columnNames.add("Name");
		model.firmwareInfo.columnNames.add("Description");
		model.firmwareInfo.columnNames.add("Version");		
		
		for (SoftwareFirmware item : firmwareList)
		{
			List<String> row = new ArrayList<String>();
			row.add(item.getName());			
			row.add(item.getDescription());
			row.add(item.getVersion());			
			model.firmwareInfo.rowFormattedData.add(row);
		}
		
		return model;
	}
	
	private TableModel formatLog(ImlLog log, TableModel table)
	{
		List<ImlLogEntry> tempEntry = null;				
		tempEntry = log.getEvent();
		if(tempEntry!= null){
			
			table.columnNames.add("Severity");
			table.columnNames.add("Class");
			table.columnNames.add("Last Update");
			table.columnNames.add("Initial Update");
			table.columnNames.add("Count");
			table.columnNames.add("Description");
		
			for (ImlLogEntry item : log.getEvent())
			{
				List<String> row = new ArrayList<String>();
				row.add(item.getSeverity());
				row.add(item.getClass_type());
				row.add(item.getLast_update());
				row.add(item.getInitial_update());
				row.add(item.getCount());
				row.add(item.getDescription());
				table.rowFormattedData.add(row);
			}

		}
		
		return table;
		
	}
	
	@Override
	public HostDetailModel formatData(HostDetailResult rawData)
	{
        log.debug("HostDetailAdapter beginning" );
        
		HostDetailModel model = new HostDetailModel();
		
		if (rawData.hasError()) 
		{
            log.debug("HostDetailResults had an error message.  " +
                    "Returning a HostDetailModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		if(rawData.getVmware_name() == null) //Maybe not the best check
		{
			model.errorMessage = "No Data in Host Detail Result.";
			return model;
		}
		
		model.powerCostAdvantage = new LabelValueModel("Power Cost Advantage",rawData.getPower_cost_advantage());
		model = this.formatHostInfoData(rawData, model);
		model = this.formatServerStatusData(rawData, model);
		model = this.formatServerPowerData(rawData, model);
		model = this.formatMemoryInfo(rawData, model);
		model = this.formatCpuInfo(rawData, model);
		model = this.formatSoftwareFirmware(rawData, model);
		
		model.imlLog = this.formatLog(rawData.getIml(), model.imlLog);
		model.iloLog = this.formatLog(rawData.getIlolog(), model.iloLog);
		
        log.debug("HostDetailAdapter the end" );
		log.debug(model.toString());
		return model;
	}

	@Override
	public HostDetailModel getEmptyModel()
	{
		return new HostDetailModel();
	}

}
