package assets.components
{
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	
	public class HostPropertiesUpdatedEvent extends Event
	{
		public static const UPDATE_HOSTPROPERTIES:String = "updateHostProperties";
		
		public function HostPropertiesUpdatedEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
			
		}
	}
}