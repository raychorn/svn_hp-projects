package com.hp.asi.hpic4vc.ui.controls.support
{
	import flash.display.DisplayObject;
	import flash.geom.Point;
	import flash.geom.Rectangle;
	
	import mx.core.IFlexDisplayObject;
	import mx.core.IUIComponent;
	import mx.events.InterManagerRequest;
	import mx.managers.ISystemManager;
	/**
	 * A utility class with static support functions.
	 */
	public class UIUtils
	{
		public function UIUtils()
		{
		}
		/**
		 * Ensure the the popup is within the view and does not go off screen. 
		 * @param popup
		 * @param parent
		 * 
		 */		
		public static function ensureWithinView(popup:IFlexDisplayObject, parent:IUIComponent):void
		{
			if((popup.y+popup.height)>parent.height){
				popup.y-=popup.height;
			}
			if((popup.x+popup.width)>parent.width){
				popup.x-=popup.width;
			}
		}
		/**
		 * Gets the screen object for the current ui comp.
		 * @param parent
		 * @return 
		 * 
		 */		
		public static function getScreen( parent:IUIComponent):Rectangle
		{
			var screen:Rectangle;
			
			var sm:ISystemManager = parent.systemManager.topLevelSystemManager;
			var sbRoot:DisplayObject = sm.getSandboxRoot();
			
			if (sm != sbRoot)
			{
				var request:InterManagerRequest = new InterManagerRequest(InterManagerRequest.SYSTEM_MANAGER_REQUEST, 
					false, false,
					"getVisibleApplicationRect"); 
				sbRoot.dispatchEvent(request);
				screen = Rectangle(request.value);
			}
			else
				screen = sm.getVisibleApplicationRect();
			return screen;
		}
		/**
		 * Positions the provided popup window below the provided parent object. <br/>
		 * Used by the Dynamic Filter Popup <br/>
		 * @param popup		The popup that you wish to position
		 * @param parent	The control that you wish to position the popup relative to
		 * @param leftOffset Whether to shift the popup left after calculating the positions, for customizing the actual position
		 * @param topOffset	Whether to shift the popup top after calculating the positions, for customizing the actual position
		 * @param offScreenMath	Whether or not to adjust the popup if it appears off screen
		 * @param where		One of three values, left, right or none. If left, positions to bottom left, if right, positions to bottom right, if none, positions right below.
		 */		
		public static function positionBelow(popup:IFlexDisplayObject, parent:IUIComponent,leftOffset:Number=0,topOffset:Number=0,offScreenMath:Boolean=true,where:String="left"):void
		{
			var screen:Rectangle=getScreen(parent);
			
			var popUpGap:int=0;
			var point:Point = new Point(0, parent.height+popUpGap);
			point = parent.localToGlobal(point);
			
			if (point.y + popup.height > screen.bottom && 
				point.y > (screen.top + parent.height + popup.height)&&offScreenMath)
			{ 
				// PopUp will go below the bottom of the stage
				// and be clipped. Instead, have it grow up.
				point.y -= (parent.height + popup.height + 2*popUpGap);
			}
			if(where=="left")
				point.x -= popup.width;
			else if(where=="right")
				point.x += parent.width;
			
			if(leftOffset)
				point.x +=leftOffset;
			if(topOffset)
				point.y +=topOffset;
			if(offScreenMath){
				point.x = Math.min( point.x, screen.right - popup.width);
				point.x = Math.max( point.x, 0);
			}
			point = popup.parent.globalToLocal(point);
			if (popup.x != point.x || popup.y != point.y)
				popup.move(point.x, point.y);
		}
		/**
		 * Returns the sum of all items the specified data provider 
		 * @param dataProvider
		 * @param fld
		 * @return 
		 * 
		 */
		public static  function sum(dataProvider:Object,fld:String=""):Number
		{
			var total:Number = 0;
			
			for each(var row:Object in dataProvider)
			{
				var num:Number=Number(row);
				if(!isNaN(num))
					total += num;
			}
			
			return total;
		}
	}
}