package com.hp.asi.hpic4vc.ui.model
{
    
    import mx.collections.ArrayCollection;
        
    public class DistributedVirtualSwitchModel
    {
        [Bindable]
        public var name:String;
        [Bindable]
        public var downlinkPortGroups:ArrayCollection;
        [Bindable]
        public var uplinkPortGroups:ArrayCollection;

        public function DistributedVirtualSwitchModel()
        {
        }
    }
}