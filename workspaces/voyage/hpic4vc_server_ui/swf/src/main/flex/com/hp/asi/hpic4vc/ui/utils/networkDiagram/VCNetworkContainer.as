package  com.hp.asi.hpic4vc.ui.utils.networkDiagram
{
	import flash.events.MouseEvent;
	
	import mx.controls.Label;
	
	import spark.components.HGroup;
	import spark.components.Image;

	public class VCNetworkContainer extends BaseContainer
	{
		
		import mx.binding.utils.ChangeWatcher;
		import mx.collections.ArrayCollection;
		import mx.controls.Text;
		
		[Embed("/assets/networkDiagram/bottleneck.png")]
		[Bindable]
		private var bottleNeck:Class
		
		public var showBottleNeck:Boolean;
		
		[Bindable]
		public var networkName:String;
		
		[Bindable]
		public var description:String;
		
		[Bindable]
		public var vms:ArrayCollection;
		
		private var vmsWatcher:ChangeWatcher
		
		public var vcname:String;
		
		public var hiColor:uint;
		
		public var color:uint;
		
		public function VCNetworkContainer()
		{
			super();
			
		}
		
		override protected function createChildren():void
		{
			super.createChildren();
			
			this.title = networkName;
			this.titleBackgroundColor = 0x333333;
			
			var hGroup2:HGroup = new HGroup();
			var desc:Label = new Label();
			desc.text = description;
			desc.styleName = "vcNetworkLabel"; 
			
			var image:Image = new Image();
			image.source = bottleNeck;
			image.visible = showBottleNeck;
			image.includeInLayout = showBottleNeck;
			image.toolTip = "Possible Bottleneck"
			
			this.group.addElement(hGroup2);
			hGroup2.addElement(desc);
			hGroup2.addElement(image);
			
			this.backgroundColor = color;
		}
		
		
	}
}