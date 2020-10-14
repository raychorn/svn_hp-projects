package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.LabelValueListModel")]
	/**
	 * A data model of HostDetail hostInformation to display.
	 * This Flex class maps to the LabelValueModel java type returned by
	 * the server_provider.
	 */
	public class LabelValueListModel extends BaseModel
	{
		public var lvList:ArrayCollection;
		
        public function LabelValueListModel() {
        }
        
        public function copy():LabelValueListModel {
            var newItem:LabelValueListModel = new LabelValueListModel();
            newItem.lvList = new ArrayCollection(this.lvList.source);
            newItem.errorMessage = this.errorMessage;
			newItem.informationMessage = this.informationMessage;
            return newItem;
        }
	}
}