package com.hp.asi.hpic4vc.ui.model
{
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.CertificateModel")]
	/**
	 * A data model for a single link used in the "Certificate Management"
	 * portion of the getting started page.  This object is used to
	 * associate with the self signed certificate.
	 */
	public class CertificateModel extends BaseModel
	{
		public var hasCertificate:Boolean;
		public var selfSigned:Boolean;
		public var certificate:String;
	}
}