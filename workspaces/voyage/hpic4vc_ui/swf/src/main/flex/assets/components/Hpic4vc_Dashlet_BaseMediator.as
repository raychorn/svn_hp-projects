package assets.components
{
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_manage_uiView;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_uiView;
	
	import flash.events.MouseEvent;

	public class Hpic4vc_Dashlet_BaseMediator extends Hpic4vc_BaseMediator
	{
		public function Hpic4vc_Dashlet_BaseMediator()
		{
			super();
		}
		
		public function moreClicked(event:MouseEvent, tabToOpen:String):void {
			setTabClickedInfo(tabToOpen);
			var monitorUIView:Hpic4vc_uiView = getView();
			var manageUIView:Hpic4vc_manage_uiView = getManageView();
			if(monitorUIView != null) {
				switch(tabToOpen){
					case Hpic4vc_TabNameEnum.NEWSFEED:
						monitorUIView.component_moreOnNewsFeedClicked(event);
						break;
				}
			} if(manageUIView != null) {
				switch(tabToOpen){
					case Hpic4vc_TabNameEnum.SOFTWAREFIRMWARE:
						manageUIView.component_moreOnSoftwareFirmwareClicked(event);
						break;
					case Hpic4vc_TabNameEnum.STORAGE:
						manageUIView.component_moreOnStorageClicked(event);
						break;
					case Hpic4vc_TabNameEnum.HOST:
						manageUIView.component_moreOnHostInformationClicked(event);
						break;
					case Hpic4vc_TabNameEnum.INFRASTRUCTURE:
						manageUIView.component_moreOnInfrastructureClicked(event);
						break;
					case Hpic4vc_TabNameEnum.NETWORKING:
						manageUIView.component_moreOnNetworkingClicked(event);
						break;
					case Hpic4vc_TabNameEnum.NETWORKDIAGRAM:
						manageUIView.networkDiagramHandler(event);
						break;
					case Hpic4vc_TabNameEnum.CLUSTER:
						manageUIView.component_moreOnClusterSummaryClicked(event);
						break;
				}
			}
		}
	}
}