package assets.components
{
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_uiView;
	import com.vmware.core.model.IResourceReference;
	
	import flash.events.Event;
	
	public interface Hpic4vc_BaseComponent
	{
		function show(contextObject:IResourceReference):void;
	}
}