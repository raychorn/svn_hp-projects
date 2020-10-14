package com.hp.asi.hpic4vc.ui.model
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.ConfigurationListModel")]
	/**
	 * A data model for the links used in the "Configuring the Product"
	 * portion of the getting started page.  Each link has an
	 * associated help link.
	 */
	public class ConfigurationListModel extends BaseModel
	{
		public var configurationLinks:ArrayCollection;
	}
}