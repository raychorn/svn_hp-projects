package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.FirmwareJobsForClusterModel;
	import com.hp.asi.hpic4vc.ui.model.FirmwareJobsModel;
	import com.hp.asi.hpic4vc.ui.model.FirmwareListOfJobsForClusterModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	import com.vmware.usersession.ServerInfo;
	import com.vmware.vsphere.client.util.UserSessionManager;
	
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.core.IFactory;
	import mx.logging.ILogger;
	import mx.logging.Log;

	public class Hpic4vc_FirmwareStatusMessagesMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_FirmwareStatusMessages;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var _proxy:Hpic4vc_providerProxy;
		private var serviceGuid:String;
		private var count:int = 0;
		[Bindable]	
		private var getFirmwareJobsForClusterTimer:Timer;
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_FirmwareStatusMessagesMediator");
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_FirmwareStatusMessages {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_FirmwareStatusMessages):void {
			_view = value;
			_view._mediator = this;
			_proxyServer = new Hpic4vc_server_providerProxy();
			_proxy = new Hpic4vc_providerProxy();
			_view._proxyServer = _proxyServer;
			if(_view != null)
			    requestData();	
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		/*public function getData():void
		{
			requestData();
		}*/
		
		override protected function requestData():void {
			
			serviceGuid = (UserSessionManager.instance.userSession.serversInfo[0] as ServerInfo).serviceGuid;
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data.");
				_proxyServer.getFirmwareJobsForCluster(_contextObject.uid,onGettingFirmwareJobsForCluster,_contextObject);
				startimer();
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		private function getFirmwareUpdateForClusterMessages(timer:TimerEvent):void{
			if(_view!=null){
				if (_contextObject != null) {
					_proxyServer.getFirmwareJobsForCluster(_contextObject.uid,onGettingFirmwareJobsForCluster,_contextObject);
				}
			}else {
				stopTimer();
			}
		}
		
		private function startimer():void{
			getFirmwareJobsForClusterTimer = new Timer(10000);
			getFirmwareJobsForClusterTimer.addEventListener(TimerEvent.TIMER, getFirmwareUpdateForClusterMessages);
			getFirmwareJobsForClusterTimer.start();
			
		}
		private function stopTimer():void
		{
			if(getFirmwareJobsForClusterTimer) 
			{
				getFirmwareJobsForClusterTimer.stop();
				getFirmwareJobsForClusterTimer.removeEventListener(TimerEvent.TIMER_COMPLETE, getFirmwareUpdateForClusterMessages);
				getFirmwareJobsForClusterTimer = null;
			}
		}
		public function selectDeleteHandlerCluster(obj:Object,hosts:Array):void{
			_proxyServer.deleteClusterFirmwareJob(_contextObject.uid,obj.toString(),hosts,onGettingDeletedMessage,_contextObject);
		}
		
		public function onGettingDeletedMessage(event:MethodReturnEvent):void {
			
			var temp:FirmwareListOfJobsForClusterModel;
			if (event != null && event.result) {
				
				temp= event.result as FirmwareListOfJobsForClusterModel;
				if(temp.updateFirmwareComponentMessageModel != null){
					Alert.show(temp.updateFirmwareComponentMessageModel.message);
					
				}else {
					
					_view.firmwareListOfJobsForClusterModel= event.result as FirmwareListOfJobsForClusterModel;
				
			    }   
			}
		}
		
		
		private function onGettingFirmwareJobsForCluster(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.debug("Received HPIC4VC server credentials data in onGettingCredentialsPage()");  
				if (event == null) {
					_view.noRecordsFoundLabel = Helper.getString("noRecordsFound");
					return;
				} else if (event.error != null) {
					if (event.error.toString().match("DeliveryInDoubt")) {
						_logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
						// Re try to request data for not more than 2 times
						if (count < 2) {
							count ++;
							requestData();
							return;
						} else {
							_view.errorFoundLabel = event.error.message;
							return;
						}
					} else {
						_view.errorFoundLabel = event.error.message;
						return;
					}
				} else if (event.result == null) {
					_view.errorFoundLabel = Helper.getString("noRecordsFound");
					return;
				} 
				_view.firmwareListOfJobsForClusterModel= event.result as FirmwareListOfJobsForClusterModel;
			}
			
		}
	}
}
