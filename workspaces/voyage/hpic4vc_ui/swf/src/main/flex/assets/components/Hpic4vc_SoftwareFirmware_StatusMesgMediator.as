package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.SmartComponentUpdateModel;
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

	public class Hpic4vc_SoftwareFirmware_StatusMesgMediator extends Hpic4vc_BaseMediator
	{
		
		private var _view:Hpic4vc_SmartComponentUpdate_StatusMessages;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var _proxy:Hpic4vc_providerProxy;
		private var serviceGuid:String;
		private var count:int = 0;
		[Bindable]	
		private var getSmartComponentupdateTimer:Timer;
		[Bindable]
		public var  smartComponentUpdateModel:SmartComponentUpdateModel;
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_SoftwareFirmware_StatusMesgMediator");
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_SmartComponentUpdate_StatusMessages {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_SmartComponentUpdate_StatusMessages):void {
			_view = value;
			_view._mediator = this;
			_proxyServer = new Hpic4vc_server_providerProxy();
			_proxy = new Hpic4vc_providerProxy();
			_view._proxyServer = _proxyServer;
			if(_view!=null)
				requestData();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			
			serviceGuid = (UserSessionManager.instance.userSession.serversInfo[0] as ServerInfo).serviceGuid;
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data.");
				_proxyServer.getSmartComponentUpdateStatusMessages(_contextObject.uid,onGettingSmartComponentUpdateStatusMessages,_contextObject);
				 startimer();
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		private function getSmartComponentUpdateMessages(timer:TimerEvent):void{
			
			if(_view!=null){
				if (_contextObject != null) {
					_proxyServer.getSmartComponentUpdateStatusMessages(_contextObject.uid,onGettingSmartComponentUpdateStatusMessages,_contextObject);
				}
			}else {
				stopTimer();
			}
		}
		
		private function startimer():void{
			getSmartComponentupdateTimer = new Timer(10000);
			getSmartComponentupdateTimer.addEventListener(TimerEvent.TIMER, getSmartComponentUpdateMessages);
			getSmartComponentupdateTimer.start();
			
		}
		private function stopTimer():void
		{
			if(getSmartComponentupdateTimer) 
			{
				getSmartComponentupdateTimer.stop();
				getSmartComponentupdateTimer.removeEventListener(TimerEvent.TIMER_COMPLETE, getSmartComponentUpdateMessages);
				getSmartComponentupdateTimer = null;
			}
		}
		
		public function selectDeleteHandlerMore(obj:Object):void{
			_proxyServer.deleteSelectedComponentJob(_contextObject.uid,obj.toString(),onGettingDeletedMessage,_contextObject);
		}
		
		public function onGettingDeletedMessage(event:MethodReturnEvent):void {
			
			var temp:SmartComponentUpdateModel;
			if (event != null && event.result) {
				
				temp= event.result as SmartComponentUpdateModel;
				if(temp.updateSoftwareComponentMessageModel != null){
					Alert.show(temp.updateSoftwareComponentMessageModel.message);
					
				}else {
					_view.smartComponentUpdateModel = event.result as SmartComponentUpdateModel;
					_view.jobsGroup.dataProvider = _view.smartComponentUpdateModel.jobs;
					_view.queueGroup.dataProvider = _view.smartComponentUpdateModel.queue;
					(_view.jobsGroup.dataProvider as ArrayCollection).refresh();
					(_view.queueGroup.dataProvider as ArrayCollection).refresh();
					_view.jobsGroup.invalidateDisplayList();
					_view.queueGroup.invalidateDisplayList();
					_view.liveStatusUpdateBox.invalidateDisplayList();
				}
			}   
			
		}
		
		private function onGettingSmartComponentUpdateStatusMessages(event:MethodReturnEvent):void {
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
			
				_view.smartComponentUpdateModel= event.result as SmartComponentUpdateModel;
				_view.jobsGroup.dataProvider = null;
				_view.queueGroup.dataProvider = null;
				var _itemrenderer:IFactory = _view.jobsGroup.itemRenderer;
				_view.jobsGroup.itemRenderer = null;
				_view.jobsGroup.itemRenderer = _itemrenderer;
				_view.jobsGroup.dataProvider = _view.smartComponentUpdateModel.jobs;
				_view.queueGroup.dataProvider = _view.smartComponentUpdateModel.queue;
				(_view.jobsGroup.dataProvider as ArrayCollection).refresh();
				(_view.queueGroup.dataProvider as ArrayCollection).refresh();
				_view.jobsGroup.invalidateDisplayList();
				_view.queueGroup.invalidateDisplayList(); 
				

			
		}
		
	}
	}
}