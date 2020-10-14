package com.hp.asi.hpic4vc.ui.model
{
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.LabelValueModel")]
	/**
	 * A data model of HostDetail hostInformation to display.
	 * This Flex class maps to the LabelValueModel java type returned by
	 * the server_provider.
	 */
	public class LabelValueModel extends BaseModel
	{
		public var label:String;
		public var value:String;


        public static function makeLabelValueModel(l:String, v:String):LabelValueModel {
            var model:LabelValueModel = new LabelValueModel();
			model.label = l;
            model.value = v;
			
			return model;
        }
	}
}