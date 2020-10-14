package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.server.provider.data.ClusterInfrastructureDetailResult;
import com.hp.asi.hpic4vc.server.provider.data.OaClusterInfraDetail;
import com.hp.asi.hpic4vc.server.provider.data.OaFanInfo;
import com.hp.asi.hpic4vc.server.provider.data.OaInfoStatus;
import com.hp.asi.hpic4vc.server.provider.data.OaInterconnects;
import com.hp.asi.hpic4vc.server.provider.data.OaPowerSupplyInfo;
import com.hp.asi.hpic4vc.server.provider.model.ClusterInfrastructureDetailModel;
import com.hp.asi.hpic4vc.server.provider.model.ClusterInfrastructureModel;
import com.hp.asi.hpic4vc.server.provider.model.InfrastructureDetailModel;

public class ClusterInfrastructureDetailAdapter extends DataAdapter<ClusterInfrastructureDetailResult, ClusterInfrastructureModel>{
	
	private static final String SERVICE_NAME =  "orservices/host/swd/clusterinfradetail";

	public ClusterInfrastructureDetailAdapter()
	{
					super(ClusterInfrastructureDetailResult.class);
		
	}
	
	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}
	
	@Override
	public ClusterInfrastructureModel formatData(ClusterInfrastructureDetailResult rawData)
	{
		log.debug("ClusterInfrastructureDetailAdapter beginning" );
        
		ClusterInfrastructureDetailModel clusterInfrastructureDetailModel ;
		ClusterInfrastructureModel clusterInfrastructureModel = new ClusterInfrastructureModel();
		
		InfrastructureDetailModel infraStructureDetailModel;
        
        if (rawData.hasError()) 
		{
            log.debug("ClusterInfrastructureDetailAdapter had an error message.  " +
                    "Returning a ClusterInfrastructureDetailModel with the error message");
            clusterInfrastructureModel.errorMessage = rawData.getErrorMessage();
            return clusterInfrastructureModel;
        }   
        
        for (OaClusterInfraDetail oa : rawData.getOas())
		{
			if (null == oa)
			{
				clusterInfrastructureModel.errorMessage = "Not Available";
				
			}
			else
			{
				clusterInfrastructureDetailModel = new ClusterInfrastructureDetailModel();
				infraStructureDetailModel = new InfrastructureDetailModel();
				
				clusterInfrastructureDetailModel.setEnclosureName(oa.getEnclosure_info().getEnclosureName());
				clusterInfrastructureDetailModel.setRackName(oa.getEnclosure_info().getRackName());
				clusterInfrastructureDetailModel.setPowerConsumed(oa.getPower().getPowerConsumed());
				clusterInfrastructureDetailModel.setPowerRedundancy(oa.getPower().getRedundancy());
				clusterInfrastructureDetailModel.setThermalRedundancy(oa.getThermal().getRedundancy());
				clusterInfrastructureDetailModel.setFanBays(oa.getEnclosure_info().getFanBays());
				clusterInfrastructureDetailModel.setFansPresent(oa.getEnclosure_info().getFansPresent());
				clusterInfrastructureDetailModel.setPowerSupplyBays(oa.getEnclosure_info().getPowerSupplyBays());
				clusterInfrastructureDetailModel.setPowerSuppliesPresent(oa.getEnclosure_info().getPowerSuppliesPresent());
				
				infraStructureDetailModel = this.formatEnclosureInfoData(oa, infraStructureDetailModel);
				infraStructureDetailModel = this.formatPowerInfoData(oa, infraStructureDetailModel);
				infraStructureDetailModel = this.formatThermalInfoData(oa, infraStructureDetailModel);
				infraStructureDetailModel = this.formatFansInfoData(oa, infraStructureDetailModel);
				infraStructureDetailModel = this.formatPowerSupplies(oa, infraStructureDetailModel);
				infraStructureDetailModel = this.formatInterconnects(oa, infraStructureDetailModel);
				infraStructureDetailModel = this.formatOaModules(oa, infraStructureDetailModel);
				infraStructureDetailModel = this.formatOaSysLog(oa, infraStructureDetailModel);
				
				clusterInfrastructureDetailModel.getClusterInfrastructureDetails().add(infraStructureDetailModel);
				clusterInfrastructureModel.getClusterInfrastructure().add(clusterInfrastructureDetailModel);
				
				
				
				
				
			}
		}
        

		return clusterInfrastructureModel;
	}
	
	@Override
	public ClusterInfrastructureModel getEmptyModel()
	{
		return new ClusterInfrastructureModel();
	}
	
	private InfrastructureDetailModel formatEnclosureInfoData(OaClusterInfraDetail oa, InfrastructureDetailModel model)
	{
		
		model.enclosure.addLabelValuePair("Enclosure", oa.getEnclosure_info().getEnclosureName());
		model.enclosure.addLabelValuePair("Product Name", oa.getEnclosure_info().getName());
		model.enclosure.addLabelValuePair("Rack", oa.getEnclosure_info().getRackName());
		model.enclosure.addLabelValuePair("Blade Bays", oa.getEnclosure_info().getBladeBays());
		model.enclosure.addLabelValuePair("Fan Bays", oa.getEnclosure_info().getFanBays());
		model.enclosure.addLabelValuePair("Power Supply Bays", oa.getEnclosure_info().getPowerSupplyBays());
		model.enclosure.addLabelValuePair("Interconnect Tray Bays", oa.getEnclosure_info().getInterconnectTrayBays());
		model.enclosure.addLabelValuePair("Serial Number", oa.getEnclosure_info().getSerialNumber());
		model.enclosure.addLabelValuePair("UUID", oa.getEnclosure_info().getUuid());
		model.enclosure.addLabelValuePair("Part Number", oa.getEnclosure_info().getPartNumber());
		model.enclosure.addLabelValuePair("Midplane Spare Part Number", oa.getEnclosure_info().getChassisSparePartNumber());
		return model;
	}
	private InfrastructureDetailModel formatPowerInfoData(OaClusterInfraDetail oa, InfrastructureDetailModel model)
	{
		model.power.addLabelValuePair("Redundancy", oa.getPower().getRedundancy());
		model.power.addLabelValuePair("Capacity", oa.getPower().getCapacity() + " W");
		model.power.addLabelValuePair("Redundant Capacity", oa.getPower().getRedundantCapacity() + " W");
		model.power.addLabelValuePair("Output Power", oa.getPower().getOutputPower() + " W");
		model.power.addLabelValuePair("Power Consumed", oa.getPower().getPowerConsumed() + " W");
		model.power.addLabelValuePair("Good Power Supplies", oa.getPower().getGoodPowerSupplies());
		model.power.addLabelValuePair("Wanted Power Supplies", oa.getPower().getWantedPowerSupplies());
		model.power.addLabelValuePair("Needed Power Supplies", oa.getPower().getNeededPowerSupplies());
		return model;
	}
	private InfrastructureDetailModel formatThermalInfoData(OaClusterInfraDetail oa, InfrastructureDetailModel model)
	{		
		model.thermal.addLabelValuePair("Redundancy", oa.getThermal().getRedundancy());
		model.thermal.addLabelValuePair("Good Fans", oa.getThermal().getGoodFans());
		model.thermal.addLabelValuePair("Wanted Fans", oa.getThermal().getWantedFans());
		model.thermal.addLabelValuePair("Needed Fans", oa.getThermal().getNeededFans());
		return model;
	}
	
	private InfrastructureDetailModel formatFansInfoData(OaClusterInfraDetail oa, InfrastructureDetailModel model)
	{
		List<OaFanInfo> fanList = oa.getFan_info().getFanInfo();
		model.fans.columnNames.add("Bay");
		model.fans.columnNames.add("Product Name");
		model.fans.columnNames.add("Fan Speed (rpm)");
		model.fans.columnNames.add("Max Fan Speed (rpm)");
		model.fans.columnNames.add("Power Consumed (W)");
		model.fans.columnNames.add("Part Number");
		model.fans.columnNames.add("Spare Part Number");
		
		for(OaFanInfo fanItem:fanList)
		{
			List<String> row = new ArrayList<String>();
			if(fanItem.getPresence() != null && fanItem.getPresence().equals("PRESENT"))
			{
				row.add(String.valueOf(fanItem.getBayNumber()));
				row.add(fanItem.getName());
				row.add(String.valueOf(fanItem.getFanSpeed()));
				row.add(String.valueOf(fanItem.getMaxFanSpeed()));
				row.add(String.valueOf(fanItem.getPowerConsumed()));
				row.add(String.valueOf(fanItem.getPartNumber()));
				row.add(fanItem.getSparePartNumber());
			}
			else
			{
				row.add(String.valueOf(fanItem.getBayNumber()));
				row.add("Absent");
				row.add("");
				row.add("");
				row.add("");
				row.add("");
				row.add("");
			}
			model.fans.rowFormattedData.add(row);
		}
		return model;
	}
	
	private InfrastructureDetailModel formatPowerSupplies(OaClusterInfraDetail oa, InfrastructureDetailModel model)
	{
		List<OaPowerSupplyInfo> list = oa.getPs_info().getPowerSupplyInfo();
		
		model.powerSupplies.columnNames.add("Bay");
		model.powerSupplies.columnNames.add("Product Name");
		model.powerSupplies.columnNames.add("Capacity (W)");
		model.powerSupplies.columnNames.add("Output (W)");
		model.powerSupplies.columnNames.add("Serial Number");
		model.powerSupplies.columnNames.add("Part Number");
		model.powerSupplies.columnNames.add("Spare Part Number");
		
		for(OaPowerSupplyInfo item:list)
		{
			List<String> row = new ArrayList<String>();
			if(item.getProductName() == null || item.getProductName().isEmpty())
			{
				row.add(String.valueOf(item.getBayNumber()));
				row.add("Absent");
				row.add("");
				row.add("");
				row.add("");
				row.add("");
				row.add("");
			}
			else
			{
				row.add(String.valueOf(item.getBayNumber()));
				row.add(item.getProductName());
				row.add(String.valueOf(item.getCapacity()));
				row.add(String.valueOf(item.getActualOutput()));
				row.add(String.valueOf(item.getSerialNumber()));
				row.add(item.getModelNumber());
				row.add(item.getSparePartNumber());
			}
			model.powerSupplies.rowFormattedData.add(row);
		}
		return model;
	}
	
	private InfrastructureDetailModel formatInterconnects(OaClusterInfraDetail oa, InfrastructureDetailModel model)
	{
		List<OaInterconnects> list = oa.getTray_info().getInterconnectTrayInfo();
		
		model.interconnects.columnNames.add("Bay");
		model.interconnects.columnNames.add("Product Name");		
		model.interconnects.columnNames.add("Serial Number");
		model.interconnects.columnNames.add("Part Number");
		model.interconnects.columnNames.add("Spare Part Number");
		
		for(OaInterconnects item:list)
		{
			List<String> row = new ArrayList<String>();
			if(item.getName().equals("[Unknown]") ||item.getName()==null|| item.getName().isEmpty())
			{
				row.add(String.valueOf(item.getBayNumber()));
				row.add("Absent");
				row.add("");
				row.add("");
				row.add("");
				row.add("");
			} else {
				row.add(String.valueOf(item.getBayNumber()));
				row.add(item.getName());			
				row.add(String.valueOf(item.getSerialNumber()));
				row.add(item.getPartNumber());
				row.add(item.getSparePartNumber());
			}
			model.interconnects.rowFormattedData.add(row);
		}
		return model;
	}
	private InfrastructureDetailModel formatOaModules(OaClusterInfraDetail oa, InfrastructureDetailModel model)
	{
		List<OaInfoStatus> list = oa.getOa_info_status();
		
		model.oaModules.columnNames.add("Bay");
		model.oaModules.columnNames.add("Product Name");
		model.oaModules.columnNames.add("Role");
		model.oaModules.columnNames.add("IP Address");
		model.oaModules.columnNames.add("Firmware Version");
		model.oaModules.columnNames.add("Serial Number");
		model.oaModules.columnNames.add("Part Number");
		model.oaModules.columnNames.add("Spare Part Number");
		
		for(OaInfoStatus item:list)
		{
			List<String> row = new ArrayList<String>();
			row.add(String.valueOf(item.getOa_info().getBayNumber()));
			row.add(item.getOa_info().getName());			
			row.add(item.getOa_status().getOaRole());
			row.add(item.getOa_network().getIpAddress());
			row.add(item.getOa_info().getFwVersion());
			row.add(item.getOa_info().getSerialNumber());
			row.add(item.getOa_info().getPartNumber());
			row.add(item.getOa_info().getSparePartNumber());
			model.oaModules.rowFormattedData.add(row);
		}
		return model;
	}
	
	private InfrastructureDetailModel formatOaSysLog(OaClusterInfraDetail oa, InfrastructureDetailModel model)
	{
		List<String> list = oa.getSyslog();
		
		for(String item:list)
		{
			model.syslog.add(item);
		}
		
		return model;
	}
	
	


}
