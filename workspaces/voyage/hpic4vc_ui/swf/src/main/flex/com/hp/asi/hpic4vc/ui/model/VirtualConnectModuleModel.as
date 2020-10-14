package com.hp.asi.hpic4vc.ui.model
{
    
    import mx.collections.ArrayCollection;
        
    public class VirtualConnectModuleModel
    {
        [Bindable]
        public var enclosuer:int;
        [Bindable]
        public var bay:int;
		[Bindable]
		public var name:String;
		[Bindable]
		public var status:String;
        [Bindable]
        public var ip:String;
        [Bindable]
        public var firmwareVersion:String;
        [Bindable]
        public var networks:ArrayCollection;
        [Bindable]
        public var fabrics:ArrayCollection;
        [Bindable]
        public var uplinks:ArrayCollection;

        public function VirtualConnectModuleModel()
        {
        }
    }
}