package com.hp.asi.hpic4vc.ui.model
{
    import com.hp.asi.hpic4vc.ui.model.TableModel;
    import com.hp.asi.hpic4vc.ui.utils.Helper;
    
    import mx.collections.ArrayCollection;
        
    public class InfrastructureDetailViewModel
    {
        [Bindable]
        public var enclosure:LabelValueListModel;
        [Bindable]
        public var power:LabelValueListModel;
        [Bindable]
        public var thermal:LabelValueListModel;
        
        [Bindable]
        public var fans:DataGridWrapper;
        [Bindable]
        public var powerSupplies:DataGridWrapper;
        [Bindable]
        public var interconnects:DataGridWrapper;
        [Bindable]
        public var oaModules:DataGridWrapper;

        [Bindable]
        public var syslog:ArrayCollection;

        [Bindable]
        public var errorMessage:String;

        public function InfrastructureDetailViewModel()
        {
        }

        public static function makeInfrastructureDetailViewModel(value:InfrastructureDetailModel):InfrastructureDetailViewModel            
        {
            var newItem:InfrastructureDetailViewModel = new InfrastructureDetailViewModel()
            newItem.enclosure = value.enclosure.copy();
            newItem.power = value.power.copy();
            newItem.thermal = value.thermal.copy();
            
            newItem.fans = Helper.createDataGrid( value.fans );
            newItem.powerSupplies = Helper.createDataGrid( value.powerSupplies );
            newItem.interconnects = Helper.createDataGrid( value.interconnects );
            newItem.oaModules = Helper.createDataGrid( value.oaModules );
            newItem.syslog = new ArrayCollection(value.syslog.source);
            newItem.errorMessage = new String(value.errorMessage);
            return newItem;
        }
    }
}