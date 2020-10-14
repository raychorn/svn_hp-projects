package com.hp.asi.hpic4vc.ui.controls.dashboard
{
	import com.flexicious.components.dashboard.Dashlet;
	import com.hp.asi.hpic4vc.ui.model.TabModel;
	
	import flash.events.MouseEvent;
	
	import mx.collections.ArrayCollection;
	
	public class HPDashlet extends Dashlet
	{
		public function HPDashlet()
		{
			super();
			draggable=true;
		}
		private var _portletModel:TabModel;

		[Bindable]
		/**
		 * The model associated with this dashlet 
		 */
		public function get portletModel():TabModel
		{
			return _portletModel;
		}

		/**
		 * @private
		 */
		public function set portletModel(value:TabModel):void
		{
			_portletModel = value;
			title=value.displayNameValue;
		}
		[Bindable()]
		/**
		 *  Text associated with the footer link.
		 */		
		public var links:ArrayCollection;
		
		
		protected override function onDragGripMouseOver(event:MouseEvent):void{
			
		}

	}
}