package assets.components
{
	import com.vmware.core.model.ArrayDataObject;
	
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	
	public class AddOrDeleteCredentialsEvent extends Event
	{
		public static const UPDATE_CREDENTIALS:String = "updateCredentials";
		
		
		public var data:ArrayCollection;
		public function AddOrDeleteCredentialsEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
		}
	}
}