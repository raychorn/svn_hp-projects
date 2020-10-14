package assets.components
{
	import mx.validators.ValidationResult;
	import mx.validators.Validator;
	
	
	
	public class CustomIPAddressValidator extends Validator
	{
		private var results:Array;
		public function CustomIPAddressValidator()
		{
			super();
		}
		
		override protected function doValidation(value:Object):Array {
			var inputValue:String = value.toString();
			results = [];
			
			results = super.doValidation(value);
		
			if (results.length > 0)
				return results;
			
			var validIPRegExp:RegExp = /^([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])\.([01]?\d\d?|2[0-4]\d|25[0-5])$/g;
			
			if (!validIPRegExp.test(inputValue)) {
				results.push(new ValidationResult(true, null,"Invalid IP","Enter valid IP address."));
				return results;
			}
			return results;
		}
	}
}