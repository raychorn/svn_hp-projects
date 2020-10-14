package com.hp.asi.hpic4vc.ui.controls.grids.behaviors
{
	 
	import com.flexicious.grids.dependencies.IExtendedDataGrid;
	import com.hp.asi.hpic4vc.ui.controls.grids.HPDataGrid;

	/**
	 * This class is the base class for all behaviors to apply to
	 * the grids.
	 */	
	public /*abstract*/ class BehaviorBase
	{
		public function BehaviorBase()
		{
		}
		/**
		 * Applies the current configurator to the grid passed in.
		 * @param grid An instance of HPDataGrid. 
		 */
		public /*abstract*/ function apply(grid:HPDataGrid):void{
			throw new Error('Cannot call this method directly. Implement in the derived class and do not call super.')
		}
	}
}