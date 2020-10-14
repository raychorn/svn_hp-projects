package assets.components
{
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_manage_uiView;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_uiView;
	import com.vmware.core.events.DataRefreshInvocationEvent;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.ui.IContextObjectHolder;
	import com.vmware.usersession.ServerInfo;
	import com.vmware.vsphere.client.util.UserSessionManager;
	
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	
	import mx.collections.ArrayCollection;
	
	/**
	 * Used to add common functions to all mediator classes
	 */
	public class Hpic4vc_BaseMediator extends EventDispatcher implements IContextObjectHolder
	{

		protected var _contextObject:IResourceReference;
		public static var tab_Clicked:String;
		public static var manageView:Hpic4vc_manage_uiView;
		public static var uiView:Hpic4vc_uiView;
		public static var actionsMenu:ArrayCollection;
		public var _manageView:Hpic4vc_manage_uiView;
		
		/**
		 * The EventHandler registers this function with vSphere Web Client so
		 * onDataRefreshInvocationEvent gets called whenever the global refresh
		 * button is called via the vSphere Web Client UI
		 */
		[EventHandler(name="{com.vmware.core.events.DataRefreshInvocationEvent.EVENT_ID}")]
		public function onDataRefreshInvocationEvent(event:DataRefreshInvocationEvent):void {
			refreshPageContent();
		}
		
		/**
		 * Instructions on what to do when the Global Refresh Button on vSphere 
		 * Web Client is pressed.
		 */
		protected function refreshPageContent():void {
			requestData();
		}
		
		/**
		 * This function contains instructions on how to request the data from
		 * the providers.
		 */
		protected function requestData():void {
			//logic to request data to be handled by child class
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
		
		protected function clearData():void {
			// Logic for clearing view to be handled by child class.
		}
		
		public function getTargetViewURL(vObjectType:String, tabName:String):String {
			// Need to navigate to Monitor tab
			if (tabName == Hpic4vc_TabNameEnum.TASKS || 
				tabName == Hpic4vc_TabNameEnum.NEWSFEED || 
				tabName == Hpic4vc_TabNameEnum.HEALTH) {
				
				if (vObjectType == Hpic4vc_VObjectEnum.HOSTSYSTEM) {
					return "com.hp.asi.hpic4vc.ui.host.monitor";
				} else if (vObjectType == Hpic4vc_VObjectEnum.VIRTUALMACHINE) {
					return "com.hp.asi.hpic4vc.ui.storage.vm.monitor";
				} else if (vObjectType == Hpic4vc_VObjectEnum.DATASTORE) {
					return "com.hp.asi.hpic4vc.ui.storage.datastore.monitor";
				} else if (vObjectType == Hpic4vc_VObjectEnum.CLUSTERCOMPUTERESOURCE) {
					return "com.hp.asi.hpic4vc.ui.cluster.monitor";
				} 
			} 
			// Need to navigate to Manage tab
			else if (tabName == Hpic4vc_TabNameEnum.STORAGE || 
					 tabName == Hpic4vc_TabNameEnum.SOFTWAREFIRMWARE || 
					 tabName == Hpic4vc_TabNameEnum.HOST || 
					 tabName == Hpic4vc_TabNameEnum.CLUSTER || 
					 tabName == Hpic4vc_TabNameEnum.INFRASTRUCTURE || 
					 tabName == Hpic4vc_TabNameEnum.NETWORKING) {
				if (vObjectType == Hpic4vc_VObjectEnum.HOSTSYSTEM) {
					return "com.hp.asi.hpic4vc.ui.host.manage";
				} else if (vObjectType == Hpic4vc_VObjectEnum.VIRTUALMACHINE) {
					return "com.hp.asi.hpic4vc.ui.storage.vm.manage";
				} else if (vObjectType == Hpic4vc_VObjectEnum.DATASTORE) {
					return "com.hp.asi.hpic4vc.ui.storage.datastore.manage";
				} else if (vObjectType == Hpic4vc_VObjectEnum.CLUSTERCOMPUTERESOURCE) {
					return "com.hp.asi.hpic4vc.ui.cluster.manage";
				} 
			} 
			return null;
		}
		
		// TODO: Remove the next 2 methods
		public function getMonitorMoreLinkURLs(_contextObject:IResourceReference):String {
			// Need to navigate to Monitor tab
			if (_contextObject.type == Hpic4vc_VObjectEnum.HOSTSYSTEM) {
				return "com.hp.asi.hpic4vc.ui.host.monitor";
			} else if (_contextObject.type == Hpic4vc_VObjectEnum.VIRTUALMACHINE) {
				return "com.hp.asi.hpic4vc.ui.storage.vm.monitor";
			} else if (_contextObject.type == Hpic4vc_VObjectEnum.DATASTORE) {
				return "com.hp.asi.hpic4vc.ui.storage.datastore.monitor";
			} else if (_contextObject.type == Hpic4vc_VObjectEnum.CLUSTERCOMPUTERESOURCE) {
				return "com.hp.asi.hpic4vc.ui.cluster.monitor";
			} else 
				return null;
		} 
			
		public function getManageMoreLinkURLs(_contextObject:IResourceReference):String {
			if (_contextObject.type == "HostSystem") {
				return "com.hp.asi.hpic4vc.ui.host.manage";
			} else if (_contextObject.type == "VirtualMachine") {
				return "com.hp.asi.hpic4vc.ui.storage.vm.manage";
			} else if (_contextObject.type == "Datastore") {
				return "com.hp.asi.hpic4vc.ui.storage.datastore.manage";
			} else if (_contextObject.type == "ClusterComputeResource") {
				return "com.hp.asi.hpic4vc.ui.cluster.manage";
			} else
				return null;
		}
		
		public function setTabClickedInfo(tabClicked:String):void {
			tab_Clicked = tabClicked;
		}
		
		public function getView():Hpic4vc_uiView {
			if (uiView == null) {
				return new Hpic4vc_uiView();
			}
			return uiView;
		}
		
		public function setManageView(manageView:Hpic4vc_manage_uiView):void {
			this._manageView = manageView;
		}
		
		public function getManageView():Hpic4vc_manage_uiView {
			return manageView;
		}
		
		public function getActionsMenu():ArrayCollection {
			return actionsMenu;
		}
		
		public function getServerGuid():String {
		    return (UserSessionManager.instance.userSession.serversInfo[0] as ServerInfo).serviceGuid;	
		}
	}
}