package assets.components
{
	import flash.events.MouseEvent;
	
	import mx.controls.ColorPicker;
	import mx.controls.LinkButton;
	import mx.core.IFactory;
	import mx.utils.ColorUtil;
	
	public class DeleteLinkRenderer extends LinkButton implements IFactory
	{
		private const BLUE_COLOR:uint = 0x33bde3;
		private var obj:Object;
		
		public function DeleteLinkRenderer()
		{
			super();
			addEventListener(MouseEvent.CLICK, clickHandlerLink);
		}
		
		public function newInstance():* 
		{ 
			return new DeleteLinkRenderer(); 
		} 
		
		override public function set data(value:Object):void { 
			  if(value!=null){
				this.label = value[3].toString();
				obj = value[2];
				setStyle("color",BLUE_COLOR);
			  }
		}
		
		protected function clickHandlerLink(event:MouseEvent):void{
		   this.parentDocument.deleteItem(obj);
		}
		 
	}
}