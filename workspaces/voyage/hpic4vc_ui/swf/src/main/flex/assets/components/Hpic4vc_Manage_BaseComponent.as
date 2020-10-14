package assets.components
{
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_manage_uiView;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_uiView;
	import com.vmware.core.model.IResourceReference;
	
	import flash.events.Event;
	
	public interface Hpic4vc_Manage_BaseComponent
	{
		function showManage(contextObject:IResourceReference, hpic4vc_manage_uiView:Hpic4vc_manage_uiView):void;
	}
}