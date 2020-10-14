package assets.events
{
	import flash.events.Event;
	
	public class ColumnSelectedEvent extends Event
		
	{
		
		public var colIdx:int;
		public var isSelected:Boolean;
		public static const COLUMN_SELECTED:String = "columnSelected";
		public function ColumnSelectedEvent(type:String, colIdx:int , isSelected:Boolean ,bubbles:Boolean=true, cancelable:Boolean=false)
		{
			//TODO: implement function
			super(type,bubbles,cancelable);
			
			this.colIdx = colIdx;
			this.isSelected = isSelected;
		}
		
		override public function clone():Event {
			
			return new ColumnSelectedEvent(type,colIdx,isSelected);
		}
	}
}