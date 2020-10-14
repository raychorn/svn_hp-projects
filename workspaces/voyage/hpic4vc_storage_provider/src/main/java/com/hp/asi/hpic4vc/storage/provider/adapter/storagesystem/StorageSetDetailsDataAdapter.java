package com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem;

import java.util.ArrayList;

import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.storage.provider.dam.model.HierarchialData;
import com.hp.asi.hpic4vc.storage.provider.dam.model.HierarchialDataModel;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.StorageSetDetailsResult;
import com.hp.asi.ui.hpicsm.ws.data.StorageSetDetailsWSImpl;

public class StorageSetDetailsDataAdapter extends BaseStorageSystemDataAdapter<StorageSetDetailsResult, HierarchialDataModel>{
    /** This is the address of the service. **/
    private static final String SERVICE_NAME = "services/swd/storagesetdetails";

    public StorageSetDetailsDataAdapter (final String arrayUid) {
        super(StorageSetDetailsResult.class, arrayUid);
    }

    @Override
    public HierarchialDataModel formatData (StorageSetDetailsResult rawData) {
    	HierarchialDataModel model = new HierarchialDataModel();
    	
    	if (rawData.getErrorMessage() != null) {
    		log.info("StorageSetDetailsResult has an error message.  Returning " +
                    "a HierarchialDataModel with the error field set.");
    		model.errorMessage = rawData.getErrorMessage();
    		return model;
    	}
    	if (null == rawData.getResult()) {
            log.debug("StorageSetDetailsResult.getResult() is null. Returning " +
                    "a HierarchialDataModel with information message set.");
            model.informationMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Info_NotAvailable);
            return model;
        }
    	   	
        StorageTypeEnum typeEnum = 
                StorageTypeEnum.getStorageTypeEnum(rawData.getStorageSystemType()); 

        switch (typeEnum) {
            case HP_STOREONCE:
                model.rowFormattedData = setForBackUpSystem(rawData);
                break;
            case HP_3PAR:
                model.rowFormattedData = setFor3PAR(rawData);
                break;
            default:
                model.rowFormattedData = setForNon3PAR(rawData);
                break;
        }
    	return model;
    }

    /**
     * This is a method to create a new object for the model.
     */
    @Override
    public HierarchialDataModel getEmptyModel () {
        return new HierarchialDataModel();
    }

    /**
     * This is an accessor method for the name of the service.
     * @return - The name of the service.
     */
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }
    
    public ArrayList<HierarchialData> setForBackUpSystem(final StorageSetDetailsResult rawData) {
   
    	ArrayList<HierarchialData> hcDataObjects = new ArrayList<HierarchialData>();
    	for (StorageSetDetailsWSImpl ssDImpl : rawData.getResult()) {
    		
			//Capacity 
			ArrayList<HierarchialData> capacity = new ArrayList<HierarchialData>();
			capacity.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                    						(locale, I18NStorageProvider.DAM_SSD_TOTAL_CAPACITY), null, ssDImpl.getFormattedTotalCapacity(), null));
			capacity.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											(locale, I18NStorageProvider.DAM_SSD_FREE_SPACE), null, ssDImpl.getFormattedAvailableCapacity(), null));
			capacity.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											(locale, I18NStorageProvider.DAM_SSD_USER_DATA_STORED), null, ssDImpl.getFormattedUsedCapacity(), null));
			capacity.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											(locale, I18NStorageProvider.DAM_SSD_SIZE_ON_DISK), null, ssDImpl.getFormattedSizeOnDisk(), null));
			
			//Node Name 
			ArrayList<HierarchialData> node = new ArrayList<HierarchialData>();
			node.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
										(locale, I18NStorageProvider.DAM_SSetD_Main_Node), null, ssDImpl.getMainNode(), null));
			node.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
										(locale, I18NStorageProvider.DAM_SSetD_Failover_Node), null, ssDImpl.getFailoverNode(), null));
			
			//VTL Status
			ArrayList<HierarchialData> vtl = new ArrayList<HierarchialData>();
			vtl.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
									   (locale, I18NStorageProvider.DAM_SSetD_Libraries), null, ssDImpl.getFormattedVtlLibraries(), null));
			
			//NAS Status
			ArrayList<HierarchialData> nas = new ArrayList<HierarchialData>();
			nas.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
									   (locale, I18NStorageProvider.DAM_SSetD_Shares), null, ssDImpl.getFormattedNasShares(), null));
				
			// StoreOnce Catalyst Status
			ArrayList<HierarchialData> cat = new ArrayList<HierarchialData>();
			cat.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
									   (locale, I18NStorageProvider.DAM_SSetD_Stores), null, ssDImpl.getFormattedCatStores(), null));
						
			ArrayList<HierarchialData> serviceSet = new ArrayList<HierarchialData>();
			serviceSet.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
					(locale, I18NStorageProvider.DAM_SSetD_Service_Set_Health), null, ssDImpl.getFormattedHealthValue(), null));
			serviceSet.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											  (locale, I18NStorageProvider.DAM_SSetD_Serial_Number), null, ssDImpl.getSerialNumber(), null));
			serviceSet.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											  (locale, I18NStorageProvider.DAM_SSetD_Capacity), null, "", capacity));
			serviceSet.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											  (locale, I18NStorageProvider.DAM_SSetD_Node_Name), null, ssDImpl.getMainNode(), node));
			serviceSet.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											  (locale, I18NStorageProvider.DAM_SSetD_Service_Set_Status), null, ssDImpl.getFormattedStatus(), null));
			serviceSet.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											  (locale, I18NStorageProvider.DAM_SSetD_VTL_Status), null, ssDImpl.getFormattedVtlStatus(), vtl));
			serviceSet.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											  (locale, I18NStorageProvider.DAM_SSetD_NAS_Status), null, ssDImpl.getFormattedNasStatus(), nas));
			serviceSet.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											  (locale, I18NStorageProvider.DAM_SSetD_StoreOnce_Catalyst_Status), null, ssDImpl.getFormattedCatStatus(), cat));
			serviceSet.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											  (locale, I18NStorageProvider.DAM_SSetD_Replication_Status), null, ssDImpl.getFormattedRepStatus(), null));
			serviceSet.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											  (locale, I18NStorageProvider.DAM_SSetD_Deduplication_Ratio), null, ssDImpl.getDedupRatio(), null));
						
			hcDataObjects.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
												 (locale, I18NStorageProvider.DAM_SSetD_Service_Set_Name), null, ssDImpl.getName(), serviceSet));
    	}
    	
		return hcDataObjects;
    	
    }
    
    public ArrayList<HierarchialData> setFor3PAR(final StorageSetDetailsResult rawData) {
        int FC = 0;
        int NL = 0;
        int SSD = 0;
        int numOfCPGs = 0;
        
    	ArrayList<HierarchialData> hcDataObjects = new ArrayList<HierarchialData>();
    	ArrayList<HierarchialData> cpg = new ArrayList<HierarchialData>();
    	
    	cpg.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
								   (locale, I18NStorageProvider.DAM_SSetD_SSD), null, "", null));
    	cpg.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
								   (locale, I18NStorageProvider.DAM_SSetD_Fast_Class), null, "", null));
    	cpg.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
								   (locale, I18NStorageProvider.DAM_SSetD_Near_Line), null, "", null));
		
		hcDataObjects.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
											 (locale, I18NStorageProvider.DAM_SSetD_CPGs), null, "", cpg));
		
    	for (StorageSetDetailsWSImpl ssDImpl : rawData.getResult()) {
    		
    		if (ssDImpl.getPhysicalDeviceType().equals("FC")) {
                FC++;
    		}
    		else if (ssDImpl.getPhysicalDeviceType().equals("NL")) {
    			 NL++;
			}
    		else if (ssDImpl.getPhysicalDeviceType().equals("SSD")){
    			SSD++;
    		}
    		
    		ArrayList<HierarchialData> eachCPG = new ArrayList<HierarchialData>();
    		
    		eachCPG.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
										   (locale, I18NStorageProvider.DAM_SSetD_Access), null, ssDImpl.getFormattedAccessLevel(), null));
    		eachCPG.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
										   (locale, I18NStorageProvider.DAM_SSetD_Raid_Level), null, ssDImpl.getFormattedRaidLevel(), null));
    		eachCPG.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
									       (locale, I18NStorageProvider.DAM_SSetD_Device_Type), null, ssDImpl.getPhysicalDeviceType(), null));
    		eachCPG.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
										   (locale, I18NStorageProvider.DAM_SSD_TOTAL_CAPACITY), null, ssDImpl.getFormattedTotalCapacity(), null));
    		eachCPG.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
										   (locale, I18NStorageProvider.DAM_SSD_FREE_SPACE), null, ssDImpl.getFormattedAvailableCapacity(), null));
    		eachCPG.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
										   (locale, I18NStorageProvider.DAM_SSD_USER_DATA_STORED), null, ssDImpl.getFormattedUsedCapacity(), null));
    		
    		hcDataObjects.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
										   (locale, I18NStorageProvider.DAM_SSetD_CPG_Name), null, ssDImpl.getName(), eachCPG));
    		numOfCPGs++;
    	}
    	cpg.set(0, new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
				   					  (locale, I18NStorageProvider.DAM_SSetD_SSD), null, Integer.toString(SSD), null));
    	cpg.set(1, new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
				   					  (locale, I18NStorageProvider.DAM_SSetD_Fast_Class), null, Integer.toString(FC), null));
        cpg.set(2, new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
				   					  (locale, I18NStorageProvider.DAM_SSetD_Near_Line), null, Integer.toString(NL), null));
        
        hcDataObjects.set(0, new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
				   					  (locale, I18NStorageProvider.DAM_SSetD_CPGs), null, Integer.toString(numOfCPGs), cpg));

		return hcDataObjects;
    	
    }
    
    public ArrayList<HierarchialData> setForNon3PAR(final StorageSetDetailsResult rawData) {
    	
    	int numOfStoragePools = 0;
    	ArrayList<HierarchialData> hcDataObjects = new ArrayList<HierarchialData>();
    	
		hcDataObjects.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
				   							 (locale, I18NStorageProvider.DAM_SSetD_Storage_Pools), null, "", null));
		
		for (StorageSetDetailsWSImpl ssDImpl : rawData.getResult()) {
			
			ArrayList<HierarchialData> storagePool = new ArrayList<HierarchialData>();
			storagePool.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
					   						   (locale, I18NStorageProvider.DAM_SSetD_Access), null, ssDImpl.getFormattedAccessLevel(), null));
			storagePool.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
					   						   (locale, I18NStorageProvider.DAM_SSD_TOTAL_CAPACITY), null, ssDImpl.getFormattedTotalCapacity(), null));
			storagePool.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
					   						   (locale, I18NStorageProvider.DAM_SSD_FREE_SPACE), null, ssDImpl.getFormattedAvailableCapacity(), null));
			storagePool.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
					   						   (locale, I18NStorageProvider.DAM_SSD_USER_DATA_STORED), null, ssDImpl.getFormattedUsedCapacity(), null));
			
			hcDataObjects.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
					   							 (locale, I18NStorageProvider.DAM_SSetD_Storage_Pool_Name), null, ssDImpl.getName(), storagePool));
			numOfStoragePools++;
		}
		hcDataObjects.set(0, new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
				   (locale, I18NStorageProvider.DAM_SSetD_Storage_Pools), null, Integer.toString(numOfStoragePools), null));
		return hcDataObjects;
    	
    }
}
