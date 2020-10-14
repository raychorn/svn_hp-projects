package com.hp.asi.hpic4vc.ui.utils
{
	import mx.validators.ValidationResult;
	import mx.validators.Validator;
	
	public class IPAddressValidator extends Validator
	{
		private var results:Array;
		
		public function IPAddressValidator()
		{
			super();
		}
		
		
		override protected function doValidation(value:Object):Array {
			
			var inputValue:String = value.toString();
			
			results =[];
			results = super.doValidation(value);
			
			if (results.length>0)
				return results;
			var validIPRegExp:RegExp = /^([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])$/g;
			
			if (!validIPRegExp.test(inputValue)) {
				results.push(new ValidationResult(true, null, "InvalidIP", "Enter valid IP address."));return results;
			}
			return results;
		}
	}
}