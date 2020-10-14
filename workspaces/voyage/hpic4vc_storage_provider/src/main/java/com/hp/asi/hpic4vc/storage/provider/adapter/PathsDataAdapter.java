package com.hp.asi.hpic4vc.storage.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.TableDataAdapter;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.model.LinkModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.PathResult;
import com.hp.asi.ui.hpicsm.ws.data.PathWSImpl;

public class PathsDataAdapter extends TableDataAdapter<PathResult> {
    private static final String SERVICE_NAME = "services/swd/paths";
    
    public PathsDataAdapter() {
        super(PathResult.class);
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public String getErrorMsg (PathResult rawData) {
        return rawData.getErrorMessage();
    }

    @Override
    public boolean isResultEmpty (PathResult rawData) {
        if (null == rawData.getResult()) {
            return true;
        }
        return false;
    }

    @Override
    public void setColumns (TableModel table) {
        String arrayDiskName        = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_ArrayDiskName);
        String arrayDiskNameHover   = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_TT_ArrayDiskName);
        String vmwareDiskId         = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_VMwareDiskId);
        String arrayName            = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_ArrayName);
        String arrayNameHover       = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_TT_ArrayName);
        String host                 = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_Host);
        String hostHover            = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_TT_Host);
        String hostHBAPortWWN       = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_HostHBAPortWWN);
        String arrayPortWWN         = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_ArrayPortWWN);
        String lun                  = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_LUN);
        String arrayType            = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_ArrayType);
        String arrayController      = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_ArrayController);
        String arrayControllerHover = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_TT_ArrayController);
        String arrayPort            = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_ArrayPort);
        String arrayPortHover       = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_TT_ArrayPort);
        String hostGroup            = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_HostGroup);
        String hostGroupHover       = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_TT_HostGroup);
        String hostAccess           = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_HostAccess);
        String hostAccessHover      = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_TT_HostAccess);
        String arrayPortSpeed       = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_ArrayPortSpeed);
        String hostMode             = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_HostMode);
        String preferredPath        = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_PreferredPath);
        String preferredPathHover   = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_TT_PreferredPath);
        String activePath           = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_ActivePath);
        String activePathHover      = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_TT_ActivePath);
        String pathId               = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_PathId);
        String pathIdHover          = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.Paths_TT_PathId);

        MenuModel volumeMenu = configData.getVolumeActions(sessionInfo);
        List<LinkModel> volumeItems = new ArrayList<LinkModel>();
        if (null != volumeMenu) {
            volumeItems = volumeMenu.getMenuItems();
        }
        
        MenuModel vObjectMenu = configData.getVObjectActions(VObjectType.HOST, sessionInfo, false);
        List<LinkModel> vObjectItems = new ArrayList<LinkModel>();
        if (null != vObjectMenu) {
            vObjectItems = vObjectMenu.getMenuItems();
        }
        
        table.addColumn(arrayDiskName,    arrayDiskNameHover,   "140", volumeItems, true, false, false);
        table.addColumn(vmwareDiskId,     null,                 "250", null, true, false, false);       
        table.addColumn(arrayName,        arrayNameHover,       "100", null, true, false, false); 
        table.addColumn(host,             hostHover,            "250", vObjectItems, true, false, false);
        table.addColumn(hostHBAPortWWN,   null,                 "200", null, true, false, false);
        table.addColumn(arrayPortWWN,     null,                 "200", null, true, false, false);
        table.addColumn(lun,              null,                 "40",  null, true, true,  false);       
        table.addColumn(arrayType,        null,                 "120", null, true, false, false); 
        table.addColumn(arrayController,  arrayControllerHover, "100", null, true, false, false);
        table.addColumn(arrayPort,        arrayPortHover,       "80",  null, true, false, false);
        table.addColumn(hostGroup,        hostGroupHover,       "100", null, true, false, false);
        table.addColumn(hostAccess,       hostAccessHover,      "80",  null, true, false, false);       
        table.addColumn(arrayPortSpeed,   null,                 "100", null, true, true,  false); 
        table.addColumn(hostMode,         hostHover,            "100", null, true, false, false);
        table.addColumn(preferredPath,    preferredPathHover,   "100", null, true, false, false);
        table.addColumn(activePath,       activePathHover,      "100", null, true, false, false);
        table.addColumn(pathId,           pathIdHover,          "120", null, true, false, false);
    }
    
    @Override
    public void setRows (TableModel table, PathResult rawData) {
        String id    = null;
        String volumeType = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.DeleteVolume_Type);
        String hostMoref = VObjectType.HOST.getMoref();
        
        for (PathWSImpl pathWSImpl : rawData.getResult()) {
            String diskId = "";
            if (null != volumeType) {
                diskId = volumeType + ":" + pathWSImpl.getVMDiskID();
            }
            String hostId = "";
            if (null != hostMoref) {
                hostId = hostMoref  + ":" + pathWSImpl.getHostIdentifier();
            }
            TableRow row  = new TableRow();

            row.addCell(diskId, pathWSImpl.getArrayDiskName(),          pathWSImpl.getFormattedArrayDiskName(), null);
            row.addCell(id,     pathWSImpl.getVMDiskID(),               pathWSImpl.getFormattedVMDiskID(), null);
            row.addCell(id,     pathWSImpl.getArrayName(),              pathWSImpl.getFormattedArrayName(), null);
            row.addCell(hostId, pathWSImpl.getHost(),                   pathWSImpl.getFormattedHost(), null);
            row.addCell(id,     pathWSImpl.getFormattedHostPortWWN(),   pathWSImpl.getFormattedHostPortWWN(), null);            
            row.addCell(id,     pathWSImpl.getArrayPortWWN(),           pathWSImpl.getFormattedArrayPortWWN(), null);
            row.addCell(id,     pathWSImpl.getLun(),                    pathWSImpl.getFormattedLun(), null);
            row.addCell(id,     pathWSImpl.getFormattedArrayType(),     pathWSImpl.getFormattedArrayType(), null);
            row.addCell(id,     pathWSImpl.getArrayController(),        pathWSImpl.getFormattedArrayController(), null);
            row.addCell(id,     pathWSImpl.getArrayPort(),              pathWSImpl.getFormattedArrayPort(), null);
            row.addCell(id,     pathWSImpl.getHostGroup(),              pathWSImpl.getFormattedHostGroup(), null);
            row.addCell(id,     pathWSImpl.getFormattedHostAccess(),    pathWSImpl.getFormattedHostAccess(), null);
            row.addCell(id,     pathWSImpl.getArrayPortSpeed(),         pathWSImpl.getFormattedArrayPortSpeed(), null);
            row.addCell(id,     pathWSImpl.getFormattedHostMode(),      pathWSImpl.getFormattedHostMode(), null);
            row.addCell(id,     pathWSImpl.getFormattedPreferredPath(), pathWSImpl.getFormattedPreferredPath(), null);
            row.addCell(id,     pathWSImpl.getFormattedActivePath(),    pathWSImpl.getFormattedActivePath(), null);
            row.addCell(id,     pathWSImpl.getPathID(),                 pathWSImpl.getFormattedPathID(), null);
            table.addRow(row);
        }
        
    }
}
