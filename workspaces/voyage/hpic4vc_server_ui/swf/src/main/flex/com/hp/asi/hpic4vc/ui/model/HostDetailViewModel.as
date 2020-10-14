package com.hp.asi.hpic4vc.ui.model
{
    import com.hp.asi.hpic4vc.ui.model.TableModel;
    import com.hp.asi.hpic4vc.ui.utils.Helper;
        
    public class HostDetailViewModel
    {
        [Bindable]
        public var hostInfo:LabelValueListModel;
        [Bindable]
        public var serverStatus:LabelValueListModel;
        [Bindable]
        public var serverPower:LabelValueListModel;
        
        [Bindable]
        public var memoryInfo:DataGridWrapper;
        [Bindable]
        public var cpuInfo:DataGridWrapper;
        [Bindable]
        public var firmwareInfo:DataGridWrapper;
        [Bindable]
        public var softwareInfo:DataGridWrapper;
        [Bindable]
        public var iloLog:DataGridWrapper;
        [Bindable]
        public var imlLog:DataGridWrapper;

        [Bindable]
        public var errorMessage:String;
        [Bindable]
        public var powerCostAdvantage:LabelValueModel;

        public function HostDetailViewModel()
        {
        }

        public static function makeHostDetailViewModel(value:HostDetailModel):HostDetailViewModel            
        {
            var newItem:HostDetailViewModel = new HostDetailViewModel()
            newItem.hostInfo = value.hostInfo.copy();
            newItem.serverStatus = value.serverStatus.copy();
            newItem.serverPower = value.serverPower.copy();
            
            newItem.memoryInfo = Helper.createDataGrid( value.memoryInfo );
            newItem.cpuInfo = Helper.createDataGrid( value.cpuInfo );
            newItem.firmwareInfo = Helper.createDataGrid( value.firmwareInfo );
            newItem.softwareInfo = Helper.createDataGrid( value.softwareInfo );
            newItem.iloLog = Helper.createDataGrid( value.iloLog );
            newItem.imlLog = Helper.createDataGrid( value.imlLog );
            newItem.errorMessage = new String( value.errorMessage );
            newItem.powerCostAdvantage = LabelValueModel.makeLabelValueModel(value.powerCostAdvantage.label, value.powerCostAdvantage.value);
            return newItem;
        }
    }
}