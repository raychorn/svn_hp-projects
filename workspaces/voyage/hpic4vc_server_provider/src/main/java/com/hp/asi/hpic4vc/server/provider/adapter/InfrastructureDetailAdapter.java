package com.hp.asi.hpic4vc.server.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.server.provider.data.InfrastructureDetailResult;
import com.hp.asi.hpic4vc.server.provider.data.OaFanInfo;
import com.hp.asi.hpic4vc.server.provider.data.OaInfoStatus;
import com.hp.asi.hpic4vc.server.provider.data.OaInterconnects;
import com.hp.asi.hpic4vc.server.provider.data.OaPowerSupplyInfo;
import com.hp.asi.hpic4vc.server.provider.model.InfrastructureDetailModel;

import java.util.ArrayList;
import java.util.List;

public class InfrastructureDetailAdapter extends DataAdapter<InfrastructureDetailResult, InfrastructureDetailModel>
{
	private static final String SERVICE_NAME = "services/host/hostinfradetail";
	
	public InfrastructureDetailAdapter()
	{
		super(InfrastructureDetailResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}
	
	private InfrastructureDetailModel formatEnclosureInfoData(InfrastructureDetailResult rawData, InfrastructureDetailModel model)
	{
		
		model.enclosure.addLabelValuePair("Enclosure", rawData.getOa().getEnclosure_info().getEnclosureName());
		model.enclosure.addLabelValuePair("Product Name", rawData.getOa().getEnclosure_info().getName());
		model.enclosure.addLabelValuePair("Rack", rawData.getOa().getEnclosure_info().getRackName());
		model.enclosure.addLabelValuePair("Blade Bays", rawData.getOa().getEnclosure_info().getBladeBays());
		model.enclosure.addLabelValuePair("Fan Bays", rawData.getOa().getEnclosure_info().getFanBays());
		model.enclosure.addLabelValuePair("Power Supply Bays", rawData.getOa().getEnclosure_info().getPowerSupplyBays());
		model.enclosure.addLabelValuePair("Interconnect Tray Bays", rawData.getOa().getEnclosure_info().getInterconnectTrayBays());
		model.enclosure.addLabelValuePair("Serial Number", rawData.getOa().getEnclosure_info().getSerialNumber());
		model.enclosure.addLabelValuePair("UUID", rawData.getOa().getEnclosure_info().getUuid());
		model.enclosure.addLabelValuePair("Part", rawData.getOa().getEnclosure_info().getPartNumber());
		model.enclosure.addLabelValuePair("Midplane Spare Part Number", rawData.getOa().getEnclosure_info().getChassisSparePartNumber());
		return model;
	}
	
	private InfrastructureDetailModel formatPowerInfoData(InfrastructureDetailResult rawData, InfrastructureDetailModel model)
	{
		model.power.addLabelValuePair("Redundancy", rawData.getOa().getPower().getRedundancy());
		model.power.addLabelValuePair("Capacity", rawData.getOa().getPower().getCapacity() + " W");
		model.power.addLabelValuePair("Redundant Capacity", rawData.getOa().getPower().getRedundantCapacity() + " W");
		model.power.addLabelValuePair("Output Power", rawData.getOa().getPower().getOutputPower() + " W");
		model.power.addLabelValuePair("Power Consumed", rawData.getOa().getPower().getPowerConsumed() + " W");
		model.power.addLabelValuePair("Good Power Supplies", rawData.getOa().getPower().getGoodPowerSupplies());
		model.power.addLabelValuePair("Wanted Power Supplies", rawData.getOa().getPower().getWantedPowerSupplies());
		model.power.addLabelValuePair("Needed Power Supplies", rawData.getOa().getPower().getNeededPowerSupplies());
		return model;
	}
	
	private InfrastructureDetailModel formatThermalInfoData(InfrastructureDetailResult rawData, InfrastructureDetailModel model)
	{		
		model.thermal.addLabelValuePair("Redundancy", rawData.getOa().getThermal().getRedundancy());
		model.thermal.addLabelValuePair("Good Fans", rawData.getOa().getThermal().getGoodFans());
		model.thermal.addLabelValuePair("Wanted Fans", rawData.getOa().getThermal().getWantedFans());
		model.thermal.addLabelValuePair("Needed Fans", rawData.getOa().getThermal().getNeededFans());
		return model;
	}
	
	private InfrastructureDetailModel formatFansInfoData(InfrastructureDetailResult rawData, InfrastructureDetailModel model)
	{
		List<OaFanInfo> fanList = rawData.getOa().getFan_info().getFanInfo();
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
	
	private InfrastructureDetailModel formatPowerSupplies(InfrastructureDetailResult rawData, InfrastructureDetailModel model)
	{
		List<OaPowerSupplyInfo> list = rawData.getOa().getPs_info().getPowerSupplyInfo();
		
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
	
	private InfrastructureDetailModel formatInterconnects(InfrastructureDetailResult rawData, InfrastructureDetailModel model)
	{
		List<OaInterconnects> list = rawData.getOa().getTray_info().getInterconnectTrayInfo();
		
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
	
	private InfrastructureDetailModel formatOaModules(InfrastructureDetailResult rawData, InfrastructureDetailModel model)
	{
		List<OaInfoStatus> list = rawData.getOa().getOa_info_status();
		
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
	
	private InfrastructureDetailModel formatOaSysLog(InfrastructureDetailResult rawData, InfrastructureDetailModel model)
	{
		List<String> list = rawData.getOa().getSyslog();
		
		for(String item:list)
		{
			model.syslog.add(item);
		}
		
		return model;
	}
	
	@Override
	public InfrastructureDetailModel formatData(InfrastructureDetailResult rawData)
	{
        log.debug("InfrastructureDetailAdapter beginning" );
        
        InfrastructureDetailModel model = new InfrastructureDetailModel();
        
        if (rawData.hasError()) 
		{
            log.debug("InfrastructureDetailResults had an error message.  " +
                    "Returning a InfrastructuretDetailModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }        
        
        model = this.formatEnclosureInfoData(rawData, model);
        model = this.formatPowerInfoData(rawData, model);
        model = this.formatThermalInfoData(rawData, model);
        model = this.formatFansInfoData(rawData, model);
        model = this.formatPowerSupplies(rawData, model);
        model = this.formatInterconnects(rawData, model);
        model = this.formatOaModules(rawData, model);
        model = this.formatOaSysLog(rawData, model);
		
		return model;
	}

	@Override
	public InfrastructureDetailModel getEmptyModel()
	{
		return new InfrastructureDetailModel();
	}

}
