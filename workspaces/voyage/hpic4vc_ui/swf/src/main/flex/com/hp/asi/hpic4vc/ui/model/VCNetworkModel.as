package com.hp.asi.hpic4vc.ui.model
{
    
    import mx.collections.ArrayCollection;
        
    public class VCNetworkModel
    {
        [Bindable]
        public var name:String;
        [Bindable]
        public var uplinkVlanId:String;

        public function VCNetworkModel()
        {
        }
    }
}