package com.hp.asi.hpic4vc.ui.controls.dashboard
{
	import com.flexicious.components.dashboard.DashboardContainer;
	import com.flexicious.components.dashboard.Dashlet;
	import com.flexicious.components.dashboard.DragDropZone;
	import com.flexicious.events.DashboardEvent;
	import com.flexicious.lic.DashboardBase;
	import com.flexicious.utils.UIUtils;
	import com.hp.asi.hpic4vc.ui.controls.dashboard.behaviors.DashboardModelParserBehavior;
	import com.hp.asi.hpic4vc.ui.model.PageModel;
	
	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.geom.Point;
	import flash.ui.Mouse;
	
	import mx.controls.Image;
	import mx.core.DragSource;
	import mx.core.IUIComponent;
	import mx.core.IVisualElement;
	import mx.core.UIComponent;
	import mx.events.DragEvent;
	import mx.events.FlexEvent;
	import mx.events.SandboxMouseEvent;
	import mx.managers.DragManager;
	
	import spark.layouts.HorizontalLayout;
	import spark.layouts.VerticalLayout;
	import spark.layouts.supportClasses.LayoutBase;
	
	
	
	public class HPDashboard extends DashboardContainer
	{
		private var isDashletMaximized:Boolean = false;
		
		public function HPDashboard()
		{
			super();
			addEventListener(DashboardEvent.DASHLET_RESIZED, onDashletResizedCallLater);
			addEventListener(DashboardEvent.DASHLET_DRAG_DROPPED, onDashletMovedCallLater);
			addEventListener(DashboardEvent.DASHLET_COLLAPSED, onDashletResizedCallLater);
			addEventListener(DashboardEvent.DASHLET_EXPANDED,onDashletResizedCallLater);
			//addEventListener(DashboardEvent.DASHLET_MAXIMIZED, onMaximizeOrMiniMize);
			addEventListener(DashboardEvent.DASHLET_MAXIMIZE_CHANGED, onMaximizeOrMiniMize);
			//addEventListener(DashboardEvent.DASHLET_DRAG_DROPPED,doPersist);
			addEventListener(DashboardEvent.DASHLET_OPEN,doPersist);
			addEventListener(DashboardEvent.DASHLET_CLOSED,doPersist);
			useCompactPreferences=true;
			autoLoadPreferences=false;
			//o	Dashlet height should be configurable, but have minimum height of 190px.  That way we could specify the height for each dashlet and it would not look too small when resized.   
			minDashletHeight=190;
			//o	Dashlet has a default width of 350px, a minimum width of 330px.  User cannot make the width smaller than 330px.  User can maximize the width to any size using the   icon.
			minDashletWidth=350;
		}
		
		protected function doPersist(event:DashboardEvent):void
		{
			persistPreferences();
		}
		protected function onMaximizeOrMiniMize(event:DashboardEvent):void
		{
				isDashletMaximized = !isDashletMaximized;
				/*if(isDashletMaximized){
					width=800;
					height=600;
				}*/
				event.dashlet.resizable = !isDashletMaximized;
				event.dashlet.moveable = !isDashletMaximized;
		}
		protected function onDashletResizedCallLater(event:DashboardEvent):void
		{
			if(!isDashletMaximized)
				callLater(onDashletResizedOrMoved,[event]);
		}
		protected function onDashletMovedCallLater(event:DashboardEvent):void
		{
			if(!isDashletMaximized)
				callLater(onDashletResizedOrMoved,[event]);
		}
		 
		protected function onDashletResizedOrMoved(event:DashboardEvent):void
		{
			if(isDashletMaximized)return;
			if(event && event.dashlet){
				var ddz:DragDropZone = event.dashlet.dragDropZone;
				
				for each(var dl:Dashlet in ddz.dashlets){
					if(event.dashlet!=dl){
						dl.width = event.dashlet.width;
					}
				}
				/*if(ddz){
					ddz.width = UIUtils.max(ddz.dashlets,"width")  ;
					ddz.height = UIUtils.max(this.dragDropZones,"height") ;
				}*/

				/*this.width = UIUtils.sum(this.dragDropZones,"width") ;
				this.height = UIUtils.max(this.dragDropZones,"height") ;*/
			}
			
				
			persistPreferences();
		}
		
		protected override function showDropIndicator(dashletOrDragDropZone:IUIComponent, event:MouseEvent=null):void{
			dropDashletOrDropZone=dashletOrDragDropZone;
			dropIndicator.visible=true;
			dropIndicator.x=1;
			if(dashletOrDragDropZone){
				var ddz:DragDropZone;
				var pt:Point;
				if(dropDashletOrDropZone is DragDropZone){
					ddz=dropDashletOrDropZone as DragDropZone;
					var _h:Number = 0;
					for(var i1:int = 0; i1 < ddz.numElements;i1++) {
						if(ddz.getElementAt(i1).visible)
							_h += ddz.getElementAt(i1).height + 3; //adding 2 for the gap
					} 
					pt = globalToLocal(ddz.localToGlobal(new Point(0, _h)));
				}else if(dropDashletOrDropZone is HPDashlet){
					ddz = (dropDashletOrDropZone as Dashlet).dragDropZone;
					var mY:Number = dropDashletOrDropZone.mouseY; 
					if(mY > (dropDashletOrDropZone.height/2)) { 
						pt = globalToLocal(ddz.localToGlobal(new Point(0,dashletOrDragDropZone.y + dashletOrDragDropZone.height)));
					}
					else { 
						pt = globalToLocal(ddz.localToGlobal(new Point(0,dashletOrDragDropZone.y)));
					}
				}
				
				dropIndicator.x=pt.x;
				dropIndicator.y=pt.y;
			}
			 
		}
		
		public override function dragBegin(event:MouseEvent, dashlet:Dashlet):void{
			if(currentMaximizedDashlet!=null)return;
			if(dashletBeingResized)return;//we are dragging
			
			if(event.buttonDown && !DragManager.isDragging){
				if(dashlet){
					var de:DashboardEvent = new DashboardEvent(DashboardEvent.DASHLET_DRAG_BEGIN,false,true,dashlet,null,this);
					dispatchEvent(de);	
					if(de.isDefaultPrevented()){
						return;
					}
					
					var pt:Point = new Point(0,0);
					pt = dashlet.localToGlobal(pt);
					pt = globalToLocal(pt);
					var imageProxy:Image = getUIComponentBitmapData(dashlet);
					if(imageProxy!=null){
						var ds:DragSource = new DragSource();
						ds.addData(imageProxy, DRAG_FORMAT_KEY);               
						DragManager.doDrag(this, ds, event, 
							imageProxy,-30-pt.x,-30-pt.y,getStyle("dragAlpha"));
					}
					
					//setDragDropZonePercentHeight(100);
					
					dashletBeingDragged=dashlet;
					//dashletBeingDragged.alpha=0.5;
					dashletBeingDragged.visible=dashletBeingDragged.includeInLayout=false;
					addEventListener(MouseEvent.MOUSE_MOVE, onDashletDraggingMouseMove);
					stage.addEventListener(MouseEvent.MOUSE_UP, onDragDropStageMouseUpHandler);
				}
			}
			setDragDropZonePercentHeight(100);
		}
		
		protected override function onDragDropStageMouseUpHandler(event:MouseEvent):void{
			super.onDragDropStageMouseUpHandler(event);
			setDragDropZonePercentHeight(Number.NaN);
		}
		
		protected override function onDragDrop(event:DragEvent):void{
			if (event.dragSource.hasFormat(DashboardBase.DRAG_FORMAT_KEY)) {
				if(dragDropCompleteFunction!=null){
					dragDropCompleteFunction(event.target as Dashlet);
				}
			}
			if(dashletBeingDragged){
				var ddz:DragDropZone;
				if(dropDashletOrDropZone is DragDropZone){
					ddz=dropDashletOrDropZone as DragDropZone;
					try {
						ddz.addDashlet(dashletBeingDragged,ddz.numElements);
					}
					catch (e:Error) {
						ddz.addDashlet(dashletBeingDragged,ddz.numElements - 1);
					}
					dashletBeingDragged.dragDropZone=(dropDashletOrDropZone as DragDropZone);
				}else if(dropDashletOrDropZone is HPDashlet){
					ddz = (dropDashletOrDropZone as Dashlet).dragDropZone;
					var mY:Number = dropDashletOrDropZone.mouseY; 
					if(mY > (dropDashletOrDropZone.height/2)){
						try{
							ddz.addDashlet(dashletBeingDragged,ddz==dropIndicator.parent?ddz.getElementIndex(dropIndicator):ddz.getElementIndex(dropDashletOrDropZone as IVisualElement) + 1);
						}catch(e:Error){
							ddz.addDashlet(dashletBeingDragged, -1);
						}
					}else
						ddz.addDashlet(dashletBeingDragged,ddz==dropIndicator.parent?ddz.getElementIndex(dropIndicator):ddz.getElementIndex(dropDashletOrDropZone as IVisualElement));
					dashletBeingDragged.dragDropZone=ddz;
				}
				//dashletBeingDragged.alpha=1;
				dropIndicator.visible=dropIndicator.includeInLayout=false;
				addElement(dropIndicator);
				dashletBeingDragged.visible=dashletBeingDragged.includeInLayout=true;
				
			}
			var de:DashboardEvent = new DashboardEvent(DashboardEvent.DASHLET_DRAG_DROPPED,false,true,dashletBeingDragged,null,this);
			dispatchEvent(de);
			dashletBeingDragged=null;
			dropDashletOrDropZone=null;
			resizeIndicator.visible=false;
			Mouse.cursor = flash.ui.MouseCursor.AUTO;
		}
		
		/**
		 *  @private
		 */
		protected override function defaultContentLayoutFunction():LayoutBase
		{
			return new HorizontalLayout();
		}
		
		/**
		 * The behavior responsible for consuming a table model and applying it to the grid, 
		 */		
		public var modelParserBehavior:DashboardModelParserBehavior = new DashboardModelParserBehavior();
		
		public var loaded:Boolean=false;
		/**
		 * Once we have the backend populate the table model, we simply provide the details to the model parser to convert it to 
		 * the grid's API
		 */		
		public function loadFromTabelModel(pageModel:PageModel):void{
			//first thing we create the 3 ddzone
			this.removeAllElements();
			
			if (loaded){
				return ;
			}
			loaded=true;
			
			this.modelParserBehavior.pageModel=pageModel;
			this.modelParserBehavior.apply(this);
			if(this.context)
				this.preferencePersistenceKey = this.context.uid.replace(/-/ig,"").replace(/:/ig,"");
			
			validateNow();
			loadPreferences();
			
			/*for each(var ddz:DragDropZone in this.dragDropZones){
					ddz.width = UIUtils.max(ddz.dashlets,"width") as Number;
					ddz.height = UIUtils.sum(ddz.dashlets,"height") as Number;
			}*/
			
		}
		/*protected override function showDropIndicator(cell:IUIComponent):void{
			super.showDropIndicator(cell)
			dropIndicator.alpha=0;
		}*/
		
		/**
		 * Start the resize 
		 * @param event
		 * @param dashlet
		 * 
		 */		
		public override function resizeBegin(event:MouseEvent,dashlet:Dashlet):void{
			if(dashletBeingDragged)return;//we are dragging
			if(currentMaximizedDashlet!=null)return;
			dropIndicator.alpha=1;
			if(event.buttonDown && dashlet){
				var de:DashboardEvent = new DashboardEvent(DashboardEvent.DASHLET_RESIZE_BEGIN,false,true,dashlet,null,this);
				dispatchEvent(de);
				if(de.isDefaultPrevented()){
					return;
				}

				var pt:Point = new Point(0,0);
				pt = dashlet.localToGlobal(pt);
				pt = globalToLocal(pt);
				resizeIndicator.visible=false;
				resizeIndicator.x=1;
				if(dashlet){
					pt=globalToLocal(dashlet.localToGlobal(new Point(0,dashlet.height)))
					resizeIndicator.x=pt.x;
					resizeIndicator.y=pt.y-dashlet.height;
				}
				resizeIndicator.height=dashlet.height-1;
				resizeIndicator.width=(dashlet.width-1);
				dashletBeingResized=dashlet;
				stage.addEventListener(MouseEvent.MOUSE_MOVE, onDashletResizingMouseMove);
				stage.addEventListener(MouseEvent.MOUSE_UP, onDashletResizingMouseUp);
				var sbRoot:DisplayObject = systemManager.getSandboxRoot();
				sbRoot.addEventListener(SandboxMouseEvent.MOUSE_UP_SOMEWHERE, cleanupResize,false,0,true);
			}
		}		
		
		/**
		 * @private 
		 */		
		protected override function onDashletResizingMouseMove(event:MouseEvent):void {
			if(event.buttonDown){
				var pt:Point=new Point(event.stageX,event.stageY);
				pt=globalToLocal(pt);
				resizeIndicator.width = Math.max(minDashletWidth,pt.x-resizeIndicator.x);
				resizeIndicator.height = Math.max(minDashletHeight,pt.y-resizeIndicator.y);
				dashletBeingResized.width = Math.max(minDashletWidth,pt.x-resizeIndicator.x);
				dashletBeingResized.height = Math.max(minDashletHeight,pt.y-resizeIndicator.y);
				var de:DashboardEvent = new DashboardEvent(DashboardEvent.DASHLET_RESIZED,false,true,dashletBeingResized,null,this);
				dispatchEvent(de);
			}else{
				cleanupResize();
			}
		}
		
		/**
		 * @private 
		 */		
		protected override function cleanupResize(e:Event=null):void
		{
//			var de:DashboardEvent = new DashboardEvent(DashboardEvent.DASHLET_RESIZED,false,true,dashletBeingResized,null,this);
//			dispatchEvent(de);
			
			resizeIndicator.visible=false;
			Mouse.cursor = flash.ui.MouseCursor.AUTO;
			stage.removeEventListener(MouseEvent.MOUSE_UP,onDashletResizingMouseUp);
			stage.removeEventListener(MouseEvent.MOUSE_MOVE,onDashletResizingMouseMove);	
			var sbRoot:DisplayObject = systemManager.getSandboxRoot();
			sbRoot.removeEventListener(SandboxMouseEvent.MOUSE_UP_SOMEWHERE, cleanupResize,false);
		}
		
		/**
		 * @private 
		 */		
		protected override function onDashletResizingMouseUp(event:MouseEvent):void {
			cleanupResize();
			if(!dashletBeingResized)return;
//			var wDelta:Number=resizeIndicator.width-dashletBeingResized.width;
//			var hDelta:Number=resizeIndicator.height-dashletBeingResized.height;
//			dashletBeingResized.width=resizeIndicator.width;
//			dashletBeingResized.height=resizeIndicator.height;
//			if(dashletBeingResized.layout is VerticalLayout){
//				dashletBeingResized.dragDropZone.width += wDelta;
//			}
//			else if(dashletBeingResized.layout is HorizontalLayout){
//				dashletBeingResized.dragDropZone.height += hDelta;
//			}
			dashletBeingResized=null;
			
		}	
		[Bindable]
		public var noPortletsFound:String;	
		[Bindable]
		public var errorFoundLabel:String; 
		/**
		 * The context associated with the dashboard.
		 **/
		public var context:Object;//Todo change this to hard reference IResourceReference;
		/**
		 * The view associated with the dashboard.
		 **/
		public var view:Object;//Todo change this to hard reference Hpic4vc_manage_uiView;
		 
		
	}
}