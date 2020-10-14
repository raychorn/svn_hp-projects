package assets.components
{
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_manage_uiView;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_uiView;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.ui.events.NavigationRequest;
	
	import flash.events.Event;
	import flash.events.MouseEvent;
	
	import mx.collections.ArrayCollection;
	
	public interface Hpic4vc_Overview_BaseComponent
	{		
		function showPortlet(contextObject:IResourceReference):void;
		
		/**
		 * This function returns an ArrayCollection of the names of the links that appear on the dashlet.
		 */
		function getLinks():ArrayCollection;
		
		/**
		 * This function performs the required action when the particular link is clicked on the dashlet.
		 * @param event	A MouseEvent representation of the click on the link.
		 * @param link	A String representation of the text of the link.
		 * 
		 */
		function onLinkClicked(event:MouseEvent, link:String):void;
	}
}