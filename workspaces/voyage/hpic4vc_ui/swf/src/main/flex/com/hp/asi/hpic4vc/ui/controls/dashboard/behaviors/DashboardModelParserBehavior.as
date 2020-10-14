package com.hp.asi.hpic4vc.ui.controls.dashboard.behaviors
{
	import com.flexicious.components.dashboard.Dashlet;
	import com.flexicious.components.dashboard.DragDropZone;
	import com.flexicious.nestedtreedatagrid.FlexDataGridColumn;
	import com.hp.asi.hpic4vc.ui.controls.dashboard.HPDashboard;
	import com.hp.asi.hpic4vc.ui.controls.dashboard.HPDashlet;
	import com.hp.asi.hpic4vc.ui.controls.dashboard.HPDragDropZone;
	import com.hp.asi.hpic4vc.ui.controls.grids.behaviors.BehaviorBase;
	import com.hp.asi.hpic4vc.ui.model.PageModel;
	import com.hp.asi.hpic4vc.ui.model.TabModel;
	import com.hp.asi.hpic4vc.ui.utils.IconRepository;
	
	import flash.utils.Dictionary;
	import flash.utils.getDefinitionByName;
	
	import mx.collections.ArrayCollection;
	import mx.collections.Sort;
	import mx.collections.SortField;
	import mx.containers.HBox;
	import mx.core.ClassFactory;
	import mx.core.EventPriority;
	import mx.core.IFactory;
	import mx.events.FlexEvent;
	
	import spark.layouts.VerticalLayout;
	
	/**
	 * 
	 * @author Flexicious-106
	 * 
	 */	
	public class DashboardModelParserBehavior 
	{
		[Bindable]
		public var pageModel:PageModel;
		
		public var dashboard:HPDashboard;
		
		public function DashboardModelParserBehavior()
		{
			super();
		}
		
		/**
		 * @inheritDoc
		 */		
		public  function apply(db:HPDashboard):void{
			this.dashboard=db;
			var dragDropZones:Array=[];
			dragDropZones.push(this.createDragDropZone("zone1"));
			dragDropZones.push(this.createDragDropZone("zone2"));
			dragDropZones.push(this.createDragDropZone("zone3"));
			
			db.dragDropZones=dragDropZones;
			
			/*for each(var ddz:DragDropZone in dragDropZones){
				var dashlets:Array=[]; 
				//TODO - create portlets based on pageModel.portlets
				dashlets.push(this.createDashlet(ddz.id + "dashlet1"));
				dashlets.push(this.createDashlet(ddz.id + "dashlet2"));
				dashlets.push(this.createDashlet(ddz.id + "dashlet3"));
				ddz.dashlets = dashlets;
			}*/
			var dashlets:Dictionary = new Dictionary;
			var s:Sort= new Sort();
			s.fields=[new SortField("order")];
			// TODO:  Add a null check for pageModel and pageModel.portlets
			if (pageModel && pageModel.portlets) {
				pageModel.portlets.sort = s;
				pageModel.portlets.refresh();
				
				for each(var portlet:TabModel in pageModel.portlets){
					var portletCol:int = parseInt(portlet.column);
					if(!dashlets[portletCol]){
						dashlets[portletCol]=[];
					}
					var dashlet:HPDashlet = createDashlet("zone_" + portletCol  + "_dashlet_" + dashlets[portletCol].length, portlet.component);
					dashlets[portletCol].push(dashlet);
					dashlet.portletModel = portlet;
					
				}
				for (var col:String in dashlets){
					dragDropZones[parseInt(col)-1].dashlets = dashlets[col];
				}
			}
		}
		
		protected function createDragDropZone(id:String):DragDropZone
		{
			//<dashboard:HPDragDropZone id="zone1" height="100%" width="30%" backgroundAlpha="0" borderVisible="false">
			var ddz:DragDropZone = new HPDragDropZone();
			//ddz.percentHeight=100;
			ddz.minWidth=350;
			ddz.setStyle("backgroundAlpha",0);
			ddz.id=id;
			ddz.setStyle("borderVisible",false);
			ddz.setStyle("headerVisible", false)
			ddz.layout=new VerticalLayout;
			return ddz;
		}
		
		protected function createDashlet(id:String,itemRenderer:String):HPDashlet
		{
			//<dashboard:HPDashlet title="HPDashlet 1.1" width="100%" height="28%" draggable="true"/>
			var dl:HPDashlet = new HPDashlet();
			dl.width=350; 
			//dl.height=dashboard.height/3; 
			dl.itemRenderer=instantiateUsingClassName(itemRenderer);//TODO need to pass itemRenderer from page model
			//dl.setStyle("backgroundAlpha",0);
			dl.id=id;
			dl.setStyle("borderVisible",false);
			dl.enableClose = false;
			dl.resizable = true;
			dl.titleBarHeight = 26;
			dl.addEventListener(FlexEvent.CREATION_COMPLETE,onCreationComplete,true,EventPriority.BINDING);
			return dl;
		}
		
		protected function onCreationComplete(event:FlexEvent):void
		{
			var dl:HPDashlet = event.currentTarget as HPDashlet;
			dl.removeEventListener(FlexEvent.CREATION_COMPLETE,onCreationComplete,true);
			var item:Object = dl.itemRendererInstance;
			item.showPortlet(dashboard.context);
			dl.links = item.getLinks();
//			item.moreClicked(event);
		}		
		
		private function instantiateUsingClassName(className:String):IFactory {
			//className = "SamplePortlet";
			var classToInstantiate : Class ;
			try{
				classToInstantiate 	= getDefinitionByName(className) as Class
			}catch(e:Error){
				classToInstantiate =  getDefinitionByName("SamplePortlet") as Class
			} 
			var myClassFactory : ClassFactory = new ClassFactory(classToInstantiate);
			return myClassFactory;
		}
	}
}