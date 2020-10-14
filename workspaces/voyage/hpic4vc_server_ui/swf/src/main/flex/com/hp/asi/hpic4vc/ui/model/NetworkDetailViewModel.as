package com.hp.asi.hpic4vc.ui.model
{
    import com.hp.asi.hpic4vc.ui.model.TableModel;
    import com.hp.asi.hpic4vc.ui.model.network.VCM;
    import com.hp.asi.hpic4vc.ui.utils.Helper;
    
    import mx.collections.ArrayCollection;
        
    public class NetworkDetailViewModel
    {
        [Bindable]
        public var nics:DataGridWrapper;
        [Bindable]
        public var virtualSwitches:ArrayCollection;
        [Bindable]
        public var distributedVirtualSwitches:ArrayCollection;
        [Bindable]
        public var vcms:ArrayCollection;
        [Bindable]
        public var externalSwitches:DataGridWrapper;
        [Bindable]
        public var externalStorage:DataGridWrapper;
		
		[Bindable]
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.DataStore")]
		public var ds:ArrayCollection;
		
		[Bindable] 
		public var vcm:VCM;
		

        [Bindable]
        public var errorMessage:String;

        public function NetworkDetailViewModel()
        {
        }

        public static function makeNetworkDetailViewModel(value:NetworkDetailModel):NetworkDetailViewModel            
        {
            var newItem:NetworkDetailViewModel = new NetworkDetailViewModel()
            
            newItem.nics = Helper.createDataGrid( value.nics );
            newItem.externalSwitches = Helper.createDataGrid( value.externalSwitches );
            newItem.externalStorage = Helper.createDataGrid( value.externalStorage );
            newItem.virtualSwitches = value.virtualSwitches;
            newItem.distributedVirtualSwitches = value.distributedVirtualSwitches;
            newItem.vcms = value.vcms;
            newItem.errorMessage = new String(value.errorMessage);
			newItem.ds = value.ds;
			newItem.vcm = value.vcm;
            return newItem;
        }
    }
}