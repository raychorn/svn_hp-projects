package com.hp.asi.hpic4vc.ui.model
{
    
    import mx.collections.ArrayCollection;
        
    public class VirtualSwitchModel
    {
        [Bindable]
        public var numPorts:int;
        [Bindable]
        public var numPortsAvailable:int;
        [Bindable]
        public var name:String;
        [Bindable]
        public var portGroups:ArrayCollection;

        public function VirtualSwitchModel()
        {
        }
    }
}