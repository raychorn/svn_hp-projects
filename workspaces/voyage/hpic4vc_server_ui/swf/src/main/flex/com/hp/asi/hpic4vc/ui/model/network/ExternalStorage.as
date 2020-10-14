package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.ExternalStorage")]
	public class ExternalStorage
	{
		private var _portWWN:ArrayCollection;
		public var WWN:String;
	
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.ExternalStoragePort")] 
		public var portArray:ArrayCollection
		
		public function get portWWN():ArrayCollection
		{
			return _portWWN;
		}
		
		public function set portWWN(value:ArrayCollection):void
		{
			_portWWN = value;
			portArray = new ArrayCollection();
			for each (var id:String in _portWWN)
			{
				var externalStoragePort:ExternalStoragePort = new ExternalStoragePort();
				externalStoragePort.id = id;
				portArray.addItem(externalStoragePort);
			}
		}
	}
}