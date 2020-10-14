package com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.storage.provider.dam.model.HierarchialData;
import com.hp.asi.hpic4vc.storage.provider.dam.model.HierarchialDataModel;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.StorageSystemDetailsResult;
import com.hp.asi.ui.hpicsm.ws.data.StorageSystemDetailsWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.StorageSystemDetailsWSImpl.Node;

/**
 * Adapts the StorageSystemDetailsResult to the HierarchialDataModel
 * 
 * @author Andrew Khoury
 *
 */
public class StorageSystemDetailsDataAdapter
        extends
        BaseStorageSystemDataAdapter<StorageSystemDetailsResult, HierarchialDataModel> {
    /** This is the address of the service. **/
    private static final String SERVICE_NAME = "services/swd/storagesystemdetails";

    public StorageSystemDetailsDataAdapter (final String arrayUid) {
        super(StorageSystemDetailsResult.class, arrayUid);
    }

    @Override
    public HierarchialDataModel formatData (final StorageSystemDetailsResult rawData) {
        HierarchialDataModel model = new HierarchialDataModel();

        if (null != rawData.getErrorMessage() && !rawData.getErrorMessage().equals("")) {
            log.info("StorageSystemDetailsResult has an error message.  Returning " +
                    "a HierarchialDataModel with the error field set.");
            model.errorMessage = rawData.getErrorMessage();
            
        } else if (null == rawData.getResult()) {
            log.info("StorageSystemDetailsResult.getResult() is null.  Returning " +
                    "a HierarchialDataModel with information message set.");
            model.informationMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Info_NotAvailable);
            
        } else if (null == rawData.getResult().getStorageSystemType()){
            log.info("StorageSystemDetailsResult.getResult().getStorageSystemType() is null." +
                    "  Returning a HierarchialDataModel with information message set.");
            model.informationMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Info_NotAvailable);
            
        } else {
            StorageSystemDetailsWSImpl webService = rawData.getResult();
            StorageTypeEnum typeEnum  = StorageTypeEnum.getStorageTypeEnum
                    (webService.getStorageSystemType()); 
            
            if (hasMostlyInvalidData(typeEnum, webService)) {
                model.errorMessage = this.i18nProvider.getInternationalString
                        (locale, I18NProvider.Info_NotAvailable);
            } else {
            
                switch (typeEnum) {
                    case HP_3PAR:
                        model.rowFormattedData = get3parData(webService);
                        break;
                        
                    case HP_STOREONCE:
                        model.rowFormattedData = getBackupSystemData(webService);
                        break;
                        
                    case HP_LEFTHAND:
                        model.rowFormattedData = getLefthandData(typeEnum, webService);
                        break;
                    default:
                        model.rowFormattedData = getArrayData(typeEnum, webService);
                        break;
                }
            }
        }

        return model;
    }

    private boolean hasMostlyInvalidData (final StorageTypeEnum typeEnum,
                                          final StorageSystemDetailsWSImpl webService) {
        boolean mostlyInvalid = false;
        
        switch (typeEnum) {
            case HP_STOREONCE:
                if (!isValidData(webService.getSystemStatus()) && 
                        !isValidData(webService.getIpAddress()) &&
                        !isValidData(webService.getDedupeRatio())) {
                    mostlyInvalid = true;
                }
                break;
                
            default:
                if (!this.isValidData(webService.getTotalNodeSize()) || 
                        webService.getTotalNodeSize().equals("0")) {
                    mostlyInvalid = true;                    
                }
                break;
        }

        return mostlyInvalid;
    }

    private List<HierarchialData> getLefthandData(
    		final StorageTypeEnum enumType,
			StorageSystemDetailsWSImpl webService) {
    	
    	List<HierarchialData> systemSummary = new ArrayList<HierarchialData>();

        String controllerLabel = getControllerName(enumType);
		List<Node> controllerNodes = webService.getControllerNodes();
		if (null != controllerNodes) {
			String numberOfNodes = Integer.toString(controllerNodes.size());
	        
	        List<HierarchialData> nodeList = new ArrayList<HierarchialData>();
	        
	        for (Node node : controllerNodes){
	            nodeList.add(
	            		new HierarchialData(I18NStorageProvider.getInstance().getInternationalString(
	            								locale, I18NStorageProvider.DAM_SSD_NODE),
	                                        node.getNodeId(), 
	                                        node.getNodeId(), 
	                                        null));
	        }
	        
	        systemSummary.add(new HierarchialData(controllerLabel, 
	                          numberOfNodes, 
	                          numberOfNodes, 
	                          nodeList));           
			
        } else {
            systemSummary.add(new HierarchialData(controllerLabel, 
                                                  "0", 
                                                  "0", 
                                                  null));
        }

        systemSummary.add(getConfiguredUser(webService));
        
        return systemSummary;
	}

    /**
     * Returns the BackupSystem data as a List of HeirarchialdData objects
     * 
     * @param webService
     * @return
     */
    private List<HierarchialData> getBackupSystemData (final StorageSystemDetailsWSImpl webService) {
        List<HierarchialData> systemSummary = new ArrayList<HierarchialData>();
        
        String systemStatus = nullCheckData(webService.getSystemStatus());
        systemSummary.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                              (locale, I18NStorageProvider.DAM_SSD_SYSTEMSTATUS),
                                              systemStatus, 
                                              systemStatus,
                                              null));
        
        systemSummary.add(getCapacityData(webService));
        
        String ipAddress = nullCheckData(webService.getIpAddress());
        systemSummary.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                              (locale, I18NStorageProvider.DAM_SSD_IP_ADDRESS),
                                              ipAddress, 
                                              ipAddress, 
                                              null));
        
        String dedupRatio = nullCheckData(webService.getDedupeRatio());
        systemSummary.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                              (locale, I18NStorageProvider.DAM_SSD_DEDUP_RATIO),
                                              dedupRatio, 
                                              dedupRatio, 
                                              null));

        return systemSummary;
    }

    /**
     * Returns all capacity information for a storage system as a
     * HierarchialData
     * 
     * @param webService
     * @return
     */
    private HierarchialData getCapacityData (final StorageSystemDetailsWSImpl webService) {
        
        List<HierarchialData> capacityDataChildren = new ArrayList<HierarchialData>();
        
        String totalCapacityFormatted = nullCheckData(webService.getFormattedTotalCapacity());
        String totalCapacity = nullCheckData(webService.getTotalCapacity());
        capacityDataChildren.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                                     (locale, I18NStorageProvider.DAM_SSD_TOTAL_CAPACITY),
                                                     totalCapacity, 
                                                     totalCapacityFormatted, 
                                                     null));
        
        String freeSpaceFormatted = nullCheckData(webService.getFormattedAvailableCapacity());
        String freeSpace = nullCheckData(webService.getAvailableCapacity());
        capacityDataChildren.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                                     (locale, I18NStorageProvider.DAM_SSD_FREE_SPACE),
                                                     freeSpace, 
                                                     freeSpaceFormatted, 
                                                     null));
        
        
        String userDataStoredFormatted = nullCheckData(webService.getFormattedUsedCapacity());
        String userDataStored = nullCheckData(webService.getUsedCapacity());
        capacityDataChildren.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                                     (locale, I18NStorageProvider.DAM_SSD_USER_DATA_STORED),
                                                     userDataStored, 
                                                     userDataStoredFormatted, 
                                                     null));
        
        String sizeOnDiskFormatted = nullCheckData(webService.getFormattedSizeOnDisk());
        String sizeOnDisk = nullCheckData(webService.getSizeOnDisk());
        capacityDataChildren.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                                     (locale, I18NStorageProvider.DAM_SSD_SIZE_ON_DISK),
                                                     sizeOnDisk, 
                                                     sizeOnDiskFormatted, 
                                                     null));
        
        HierarchialData capacityData = new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                                           (locale, I18NStorageProvider.DAM_SSD_CAPACITY), 
                                                           "", 
                                                           "", 
                                                           capacityDataChildren);
        return capacityData;
    }

    /**
     * Returns general array data as a List of HeirarchialdData objects
     * 
     * @param webService
     * @return
     */
    private List<HierarchialData> getArrayData (final StorageTypeEnum enumType,
                                                final StorageSystemDetailsWSImpl webService) {
        List<HierarchialData> systemSummary = new ArrayList<HierarchialData>();

        if (null != webService.getControllerNodes()) {
            systemSummary.add(getNodeHierarchialData(getControllerName(enumType), 
                                                     webService.getControllerNodes()));
        } else {
            systemSummary.add(new HierarchialData(getControllerName(enumType), 
                                                  "0", 
                                                  "0", 
                                                  null));
        }

        if(StorageTypeEnum.HP_P9000.equals(enumType) || StorageTypeEnum.HP_XP.equals(enumType)){
        	
        	String ipAddress = nullCheckData(webService.getIpAddress());
            systemSummary.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                                  (locale, I18NStorageProvider.DAM_SSD_SVPIP), 
                                                  ipAddress, 
                                                  ipAddress, 
                                                  null));
        }
        systemSummary.add(getConfiguredUser(webService));
        
        return systemSummary;
    }

    /**
     * Creates HierarchialData based on a given controller name and node list
     * 
     * @param controllerName
     * @param controllerNodeNames
     * @return
     */
    private HierarchialData getNodeHierarchialData (final String controllerName,
                                                    final List<Node> controllerNodeNames) {
        String numberOfNodes = Integer.toString(controllerNodeNames.size());
        
        List<HierarchialData> nodeList = new ArrayList<HierarchialData>();
        
        for (Node node : controllerNodeNames){
            nodeList.add(new HierarchialData(node.getNodeName(), 
                                             node.getNodeId(), 
                                             getFormattedNodeIds(node.getNodeId()), 
                                             null));
        }
        
        HierarchialData nodeHeirarchy = new HierarchialData(controllerName, 
                                                            numberOfNodes, 
                                                            numberOfNodes, 
                                                            nodeList);
        return nodeHeirarchy;
    }
    
    private String getFormattedNodeIds(String nodeIds) {

    	String delimiter = "\n";
    	
    	// If the string contains commas, then append a newline char to the comma.
    	nodeIds = nodeIds.replace(", ", delimiter);
    	
    	return nodeIds;
    }

    /**
     * Returns the controller name for a given storage system type
     * 
     * @param storageSystemType
     * @return
     */
    protected String getControllerName (final StorageTypeEnum enumType) {
        if (enumType != null) {
            switch (enumType) {
                case HP_EVA:
                    return I18NStorageProvider
                            .getInstance()
                            .getInternationalString(locale,
                                                    I18NStorageProvider.DAM_SSD_CONTROLLERS);

                case HP_LEFTHAND:
                    return I18NStorageProvider
                            .getInstance()
                            .getInternationalString(locale,
                                                    I18NStorageProvider.DAM_SSD_NODES_STORAGE_SYSTEMS);

                case HP_MSA:
                case HP_P2000:
                case HP_XP:
                case HP_P9000:
                    return I18NStorageProvider
                            .getInstance()
                            .getInternationalString(locale,
                                                    I18NStorageProvider.DAM_SSD_CONTROLLER);

                default:
                    return I18NStorageProvider
                            .getInstance()
                            .getInternationalString(locale,
                                                    I18NStorageProvider.DAM_SSD_CONTROLLER);
            }
        }
        return I18NStorageProvider.getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.DAM_SSD_CONTROLLER);
    }

    /**
     * Returns the 3par data as a List of HeirarchialdData objects
     * 
     * @param webService
     * @return
     */
    private List<HierarchialData> get3parData (final StorageSystemDetailsWSImpl webService) {
        List<HierarchialData> systemSummary = new ArrayList<HierarchialData>();
        
        int numberOfNodes = 0;
        String nodesOnline = i18nProvider
                .getInternationalString(locale, I18NProvider.Info_NotAvailable);
        
        if (null != webService.getOnlineNodeNames()) {
            numberOfNodes  = webService.getOnlineNodeNames().size();
            nodesOnline    = printList(webService.getOnlineNodeNames());
        }

        List<HierarchialData> childNodes = new ArrayList<HierarchialData>();

        childNodes.add(new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                           (locale, I18NStorageProvider.DAM_SSD_NODES_ONLINE),
                                           nodesOnline,
                                           nodesOnline, null));
        HierarchialData controllerNodes = new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                                              (locale, I18NStorageProvider.DAM_SSD_CONTROLLER_NODES), 
                                                              Integer.toString(numberOfNodes),
                                                              Integer.toString(numberOfNodes),
                                                              childNodes);
        systemSummary.add(controllerNodes);
        systemSummary.add(getConfiguredUser(webService));

        return systemSummary;
    }

    /**
     * Returns the configured user key-value pair as a HeirarchialData
     * 
     * @param webService
     * @return
     */
    protected HierarchialData getConfiguredUser (final StorageSystemDetailsWSImpl webService) {
        String configuredUser = nullCheckData(webService.getConfiguredUser());
        return new HierarchialData(I18NStorageProvider.getInstance().getInternationalString
                                   (locale, I18NStorageProvider.DAM_SSD_USER),
                                   configuredUser,
                                   configuredUser,
                                   null);
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
     * 
     * @return - The name of the service.
     */
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }
}
