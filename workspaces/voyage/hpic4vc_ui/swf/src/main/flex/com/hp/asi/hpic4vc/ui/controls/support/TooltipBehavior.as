package com.hp.asi.hpic4vc.ui.controls.support
{
	
	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.events.TimerEvent;
	import flash.geom.Point;
	import flash.utils.Timer;
	
	import mx.core.FlexGlobals;
	import mx.core.IFlexDisplayObject;
	import mx.core.IUIComponent;
	import mx.managers.PopUpManager;
	
	/**
	 * Attaches the tooltip behavior to any UI component. The behavior does not
	 * automatically trigger, however, it does wrap all the functionality needed to 
	 * display a tooltip type control next to the requesting control.
	 */
	public class TooltipBehavior
	{
		public var ownerComponent:IUIComponent;
		
		public function TooltipBehavior(ownerComponent:IUIComponent)
		{
			this.ownerComponent=ownerComponent;
		}
		
		/**
		 * The current tooltip object. 
		 */		
		public var currentTooltip:IUIComponent;
		/**
		 * The current tooltip object trigger. 
		 */		
		public var currentTooltipTrigger:IUIComponent;
		/**
		 * Displays a tooltip for the control in question. The tooltip will disappear if the mouse
		 * moves over an area that is not the 'relativeTo' component or the tooltip component.. 
		 * @param relativeTo Which component to position the popup relative to
		 * @param tooltip The popup to display
		 * @param dataContext If the popup has a data property, it will be set to this value
		 * @param point If you specify this, the relativeTo is ignored, and the popup appears at the exact point you specify. Please ensure that the X and Y are relative to the Grid.
		 * @param leftOffset Whether to shift the popup left after calculating the positions, for customizing the actual position
		 * @param topOffset	Whether to shift the popup top after calculating the positions, for customizing the actual position
		 * @param offScreenMath	Whether or not to adjust the popup if it appears off screen
		 * @param where		One of three values, left, right or none. If left, positions to bottom left, if right, positions to bottom right, if none, positions right below the relativeTo component.
		 * @param container	The holder for the tooltip, defaults to UIUtils.getTopLevelApplication(). You may need to override in multi window Air apps.
		 * By default, the tooltip will go away once you hover the mouse out of the trigger cell or the tooltip and stayed that way for tooltipWatcherTimeout. You may also
		 * manually remove the tooltip by calling the hideToolTip() function. 
		 */		
		public function showTooltip(relativeTo:IUIComponent, tooltip:IUIComponent, dataContext:Object
									,point:Point=null,leftOffset:Number=0,topOffset:Number=0,offScreenMath:Boolean=true,where:String="left"
									 ,container:Object=null):void{
			
			if(currentTooltip)hideTooltip();
			
			currentTooltip=tooltip;
			currentTooltipTrigger=relativeTo;
			if(!container)
				container=FlexGlobals.topLevelApplication;
			PopUpManager.addPopUp(currentTooltip as IFlexDisplayObject,container as DisplayObject)
			if(point){
				currentTooltip.move(point.x,point.y);
				if(offScreenMath)
					UIUtils.ensureWithinView(tooltip,container as IUIComponent)
			}
			else
				UIUtils.positionBelow(tooltip,relativeTo,leftOffset,topOffset,offScreenMath,where);
			
			tooltipWatcher = new Timer(tooltipWatcherTimeout);
			tooltipWatcher.addEventListener(TimerEvent.TIMER,
				function(event:TimerEvent):void{
					if(!ownerComponent.stage){
						tooltipWatcher.stop();
						tooltipWatcher=null;
						return;
					}
					if(!currentTooltip ){hideTooltip();return;}
					var shouldShow:Boolean=false;
					var pts:Array=ownerComponent.stage.getObjectsUnderPoint((new Point(ownerComponent.stage.mouseX,ownerComponent.stage.mouseY)));
					for each(var pt:DisplayObject in pts){
						shouldShow=(
							(currentTooltip.owns(pt)
								||currentTooltipTrigger.owns(pt)) )
						if(shouldShow){
							return;
						}
					}
					hideTooltip();
				});
			
			tooltipWatcher.start();
			
			
			var tooltipObject:Object=tooltip;
			if(tooltipObject.hasOwnProperty("data"))
				tooltipObject["data"]=dataContext;
			if(tooltipObject.hasOwnProperty("grid"))
				tooltipObject["grid"]=this;
		}
		/**
		 * A timer that watches the mouse and destroys the tooltip when the mouse is outside the tooltip and the the trigger for more than
		 * tooltipWatcherTimeout msec interval 
		 */		
		protected var tooltipWatcher:Timer;
		/**
		 * Amount of time to wait after user has moved the mouse away from the tooltip, 
		 * before we destroy the tooltips. Defaults to 500 msec. 
		 */		
		public var tooltipWatcherTimeout:Number=500;
		/**
		 * Dispatched by the system manager when the mouse moves and a tooltip is active... 
		 * @param event
		 */		
		protected function toolTipMoveHandler(event:Event):void
		{
			if(!currentTooltip || 
				(!currentTooltip.owns(event.target as DisplayObject)
					&&!currentTooltipTrigger.owns(event.target as DisplayObject)) )
				hideTooltip()
		}
		/**
		 * Hides the current tooltip. 
		 */		
		public function hideTooltip():void{
			if(tooltipWatcher){
				if(tooltipWatcher.running)
					tooltipWatcher.stop()
				tooltipWatcher=null;
			}
			if(currentTooltip){
				if(currentTooltip.parent)
					PopUpManager.removePopUp(currentTooltip as IFlexDisplayObject)
				if("destroy" in currentTooltip)currentTooltip["destroy"].apply();
				currentTooltipTrigger=null
				currentTooltip=null;
			}
		}
		
	}
}