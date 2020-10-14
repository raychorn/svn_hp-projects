package com.hp.asi.hpic4vc.ui.model
{
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.ConfigurationModel")]
	/**
	 * A data model for a single link used in the "Configuring the Product"
	 * portion of the getting started page.  This object is used to
	 * associate the link with a help link.
	 */
	public class ConfigurationModel extends BaseModel
	{
		public var link:LinkModel;
		public var helpLink:LinkModel;
	}
}