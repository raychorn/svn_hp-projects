package com.hp.asi.hpic4vc.ui.model
{
	public class ApplicationDataModel 
	{
		
		private static var _instance:ApplicationDataModel = new ApplicationDataModel();
		
		public function ApplicationDataModel()
		{
			if(_instance != null)
			{
				throw new Error("ERROR");
			}
		}
		
		public static function getInstance():ApplicationDataModel
		{
			return _instance;
		}
		
		[Bindable]
		public var isAuthorized:Boolean;
		
		[Bindable]
		public var userValidated:Boolean;
		
		public static function isUserAuthorized():Boolean
		{
			return getInstance().isAuthorized;
		}
		
		public static function isUserValidated():Boolean
		{
			return getInstance().userValidated;
		}
		
	
	}
}