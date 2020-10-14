package assets.components
{
	import mx.controls.Alert;

	public class ModelLocator
	{
		private static var _instance:ModelLocator = new ModelLocator();
		
		public function ModelLocator()
		{
			if(_instance != null)
			{
				throw new Error("ERROR")
			}
		}
		
		public static function getInstance():ModelLocator
		{
			return _instance;
		}
		
		public var channels:Array = ["amfsecure", "amfsecure2", "amfsecure3", "amfsecure4", "amfsecure5", "amfsecure6", "amfsecure7"];
		[Bindable]
		private var i:int = 1;
		private var channel:String;
		
		public function getChannel():String
		{
			if(i == channels.length)
				i = 1;
			channel = channels[i];
			i++;
			return channel;
			//trace(channel);
		}
	}
}