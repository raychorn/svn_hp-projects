package com.hp.asi.hpic4vc.ui.model
{
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	
	import mx.collections.ArrayCollection;
	

	public class ClusterDetailRendererViewModel
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
		
		
		public function ClusterDetailRendererViewModel()
		{
		}
		
		public static function makeClusterDetailRendererModel(value:ClusterHostDetail):ClusterDetailRendererViewModel{
			
			var newItem:ClusterDetailRendererViewModel = new ClusterDetailRendererViewModel();
			
				
			newItem.hostInfo = value.hostInfo.copy();
			newItem.serverStatus = value.serverStatus.copy();
			if(value.serverPower!=null){
				newItem.serverPower = value.serverPower.copy();
			}
			
			newItem.memoryInfo = Helper.createDataGrid( value.memoryInfo );
			newItem.cpuInfo = Helper.createDataGrid( value.cpuInfo );
			newItem.firmwareInfo = Helper.createDataGrid( value.firmwareInfo );
			newItem.softwareInfo = Helper.createDataGrid( value.softwareInfo );
			newItem.iloLog = Helper.createDataGrid( value.iloLog );
			newItem.imlLog = Helper.createDataGrid( value.imlLog );
			return newItem;
			
			
		}
		
	}
}