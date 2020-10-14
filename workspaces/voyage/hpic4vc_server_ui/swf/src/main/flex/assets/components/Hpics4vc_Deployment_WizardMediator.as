package assets.components
{
	import com.vmware.core.model.IResourceReference;
	import com.vmware.data.query.events.PropertyRequest;
	import com.vmware.ui.IContextObjectHolder;
	
	import flash.events.EventDispatcher;
	
	import mx.logging.ILogger;
	import mx.logging.Log;

	[Event(name="{com.vmware.data.query.events.PropertyRequest.REQUEST_ID}",
       type="com.vmware.data.query.events.PropertyRequest")]
	public class Hpics4vc_Deployment_WizardMediator extends EventDispatcher implements IContextObjectHolder
	{
		
		private var _contextObject:IResourceReference;
		private var _view:Hpic4vc_Deployment_Wizard;
		private static var _logger:ILogger = Log.getLogger("Hpics4vc_Deployment_WizardMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Deployment_Wizard {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Deployment_Wizard):void {
			_view = value;
		}
		
		[Bindable]
		public function get contextObject():Object {
			return _contextObject;
		}
		
		public function set contextObject(value:Object):void {
			_contextObject = IResourceReference(value);
			
			if (_contextObject == null) {
				clearData();
				return;
			}
			requestData();
		}
		
		/**
		 * Send a query to the server to load the vmData data
		 */
		private function requestData():void {
			// Requests a single property called "samples:vmData". This will be
			// handled on the server side by the VmDataProviderImpl class.
			//
			// Note: This is an asynchronous call: the response may take some time and
			// the view will be grayed out temporarily.
			var myVmInfoRequest:PropertyRequest =
				PropertyRequest.newInstance(_contextObject, "samples:vmData");
			dispatchEvent(myVmInfoRequest);
		}


		public function Hpics4vc_Deployment_WizardMediator()
		{
		}
		
		/** Resets the UI */
		private function clearData() : void {
			//_view.vmData = null;
		}
		
		
	}
}