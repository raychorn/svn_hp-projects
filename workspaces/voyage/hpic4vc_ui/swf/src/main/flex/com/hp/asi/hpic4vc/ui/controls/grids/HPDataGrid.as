package com.hp.asi.hpic4vc.ui.controls.grids
{
	import com.flexicious.export.ExportEvent;
	import com.flexicious.grids.events.PreferencePersistenceEvent;
	import com.flexicious.nestedtreedatagrid.FlexDataGrid;
	import com.flexicious.nestedtreedatagrid.FlexDataGridBodyContainer;
	import com.flexicious.nestedtreedatagrid.FlexDataGridColumn;
	import com.flexicious.nestedtreedatagrid.FlexDataGridColumnLevel;
	import com.flexicious.nestedtreedatagrid.cells.FlexDataGridHeaderCell;
	import com.flexicious.nestedtreedatagrid.events.ExtendedFilterPageSortChangeEvent;
	import com.flexicious.nestedtreedatagrid.events.FlexDataGridEvent;
	import com.flexicious.nestedtreedatagrid.events.FlexDataGridPrintEvent;
	import com.flexicious.nestedtreedatagrid.interfaces.IFlexDataGridCell;
	import com.flexicious.nestedtreedatagrid.interfaces.IFlexDataGridDataCell;
	import com.flexicious.nestedtreedatagrid.valueobjects.ChangeInfo;
	import com.flexicious.nestedtreedatagrid.valueobjects.ComponentInfo;
	import com.flexicious.nestedtreedatagrid.valueobjects.RowInfo;
	import com.flexicious.persistence.PreferenceInfo;
	import com.flexicious.utils.UIUtils;
	import com.hp.asi.hpic4vc.ui.controls.grids.behaviors.ModelParserBehavior;
	import com.hp.asi.hpic4vc.ui.controls.grids.toolbar.HPToolbar;
	import com.hp.asi.hpic4vc.ui.controls.support.FilterTextInput;
	import com.hp.asi.hpic4vc.ui.controls.support.IHighlightableCell;
	import com.hp.asi.hpic4vc.ui.controls.support.ShowHideColumnPicker;
	import com.hp.asi.hpic4vc.ui.model.LinkModel;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	
	import flash.display.DisplayObject;
	import flash.display.Sprite;
	import flash.events.ContextMenuEvent;
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.net.FileReference;
	import flash.system.Capabilities;
	import flash.text.TextFormatAlign;
	import flash.text.TextLineMetrics;
	import flash.ui.ContextMenuItem;
	import flash.ui.Keyboard;
	import flash.utils.ByteArray;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.controls.Menu;
	import mx.controls.ProgressBarMode;
	import mx.core.ClassFactory;
	import mx.core.FlexGlobals;
	import mx.core.IFactory;
	import mx.core.IUIComponent;
	import mx.core.ScrollPolicy;
	import mx.core.UITextFormat;
	import mx.core.mx_internal;
	import mx.events.CloseEvent;
	import mx.events.FlexEvent;
	import mx.events.MenuEvent;
	import mx.managers.PopUpManager;
	import mx.rpc.events.ResultEvent;
	
	use namespace mx_internal;
	
	public class HPDataGrid extends FlexDataGrid
	{
		private static var pager_renderer:IFactory = new ClassFactory(HPToolbar);
		private static var textinput_renderer:IFactory = new ClassFactory(FilterTextInput);
		public function HPDataGrid()
		{
			super();
			enableEagerDraw=true;
			enableCopy=true;
			selectionMode="singleRow";
			enableMultiplePreferences=true;
			enablePreferencePersistence=true;
			useCompactPreferences=true;
			autoLoadPreferences=false;
			//addEventListener(FlexEvent.CREATION_COMPLETE, onCreationComplate);	
			pagerRenderer = pager_renderer;
			/*popupFactorySettingsPopup = new ClassFactory(SettingsPopup);
			popupFactorySaveSettingsPopup= new ClassFactory(SaveSettingsPopup);
			popupFactoryOpenSettingsPopup= new ClassFactory(OpenSettingsPopup);*/
			
			addEventListener(PreferencePersistenceEvent.LOAD_PREFERENCES,onLoadPreferences);
			addEventListener(PreferencePersistenceEvent.CLEAR_PREFERENCES,onClearPreferences);
			addEventListener(PreferencePersistenceEvent.PERSIST_PREFERENCES,onPersistPreferences);
			addEventListener(FlexDataGridPrintEvent.BEFORE_PRINT,onBeforPrint);
			addEventListener(ExportEvent.AFTER_EXPORT,onAfterExport);
			
			/*printOptions = new OSPrintOptions;
			pdfOptions = new OSPrintOptions;*/
			pdfOptions.printToPdf=true;
			cellBackgroundColorFunction = defaultCellBackgroundColorFunction;
			cellTextColorFunction = defaultCellTextColorFunction;
			//added styles properties from Sahil.
			pagerRowHeight=28;
			forcePagerRow=true;
			columnLevel.headerHeight=24;
			columnLevel.filterHeight=24;
			rowHeight=24;
			displayOrder="header,filter,body,footer,pager";
			enableFilters = true;
			filterVisible=false;
			horizontalScrollPolicy=ScrollPolicy.AUTO;
			showSpinnerOnFilterPageSort=true;
			pageSize=100;
			enableActiveCellHighlight=false;
			enableStickyControlKeySelection = false;
			addEventListener(FlexDataGridEvent.COLUMNS_SHIFT,onColumnsShift);
			addEventListener(FlexDataGridEvent.COLUMNS_RESIZED,onColumnsShift);
			addEventListener(ExtendedFilterPageSortChangeEvent.FILTER_PAGE_SORT_CHANGE,onFilterPageSortChange);
			
		}
		
		protected function onFilterPageSortChange(event:ExtendedFilterPageSortChangeEvent):void
		{
			persistPreferences();
		}
		
		// The below commented code is to collect the TableModel object used for debugging purposes.
		/*override protected function keyDownHandler(event:KeyboardEvent):void{
			super.keyDownHandler(event);
			if(event.keyCode==Keyboard.S && event.shiftKey){
				saveTableModel();
			}
		}
		public function saveTableModel():void{
			Alert.show("test","test",Alert.OK,this,function(e:CloseEvent):void{
				var ba:ByteArray = new ByteArray();
				ba.writeObject(tableModel);
				var f:FileReference=new FileReference();
				f.save(ba,"Sample.txt");});
		}*/
		
		protected function onColumnsShift(e:Event):void
		{
			persistPreferences();
		}		
		
		protected override function createBodyContainer():FlexDataGridBodyContainer{
			return new HPDataGridBodyContainer(this);
		}
		
		/*protected function onAddedToStage(event:Event):void
		{
		// TODO Auto-generated method stub
		
		stage.addEventListener("rightClick",onRightClick);
		trace(reportType)
		}*/
		
		/**
		 * The behavior responsible for consuming a table model and applying it to the grid, 
		 */		
		public var modelParserBehavior:ModelParserBehavior = new ModelParserBehavior();
		[Bindable()]
		/**
		 * If an error message is specified, grid will render error icon and show message on rollover 
		 */		
		public var errorMessage:String= "";
		
		/**
		 * Title message for the information popup 
		 */		
		public var informationTitle:String="Information";
		/**
		 * Title message for the error popup 
		 */		
		public var errorTitle:String="Error Occured";
		
		[Bindable()]
		/**
		 * If an information message is specified, grid will render information icon and show message on rollover 
		 */		
		public var informationMessage:String ="";
		
		/**
		 * @private 
		 */		
		private var _globalFilterString:String="";
		
		/**
		 * @private 
		 */		
		private var _globalFilterOrHighLightDirty:Boolean=false;
		
		/**
		 * Global filter string. If this property is set, the filter is run and only matching rows are shown 
		 */
		public function get globalFilterString():String
		{
			return _globalFilterString;
		}
		
		/**
		 * @private
		 */
		public function set globalFilterString(value:String):void
		{
			if(_globalFilterString != value){
				_globalFilterString = value;
				_globalFilterOrHighLightDirty=true;
				invalidateProperties();
			}
		}
		private var _globalFilterColumns:Array=[];
		[Bindable()]
		/**
		 * A list of columns that the global search uses to search. If no columns are defined, all columns are searched. 
		 */
		public function get globalFilterColumns():Array
		{
			return _globalFilterColumns;
		}
		
		/**
		 * @private
		 */
		public function set globalFilterColumns(value:Array):void
		{
			_globalFilterColumns = value;
			_globalFilterOrHighLightDirty=true;
			invalidateProperties();
		}
		private var _globalHightlightAll:Boolean=true;
		
		/**
		 * A Boolean variable that tells us whether to highlight just the first occurence or all occurences 
		 */
		public function get globalHightlightAll():Boolean
		{
			return _globalHightlightAll;
		}
		
		/**
		 * @private
		 */
		public function set globalHightlightAll(value:Boolean):void
		{
			if(_globalHightlightAll != value){
				_globalHightlightAll = value;
				_globalFilterOrHighLightDirty=true;
				invalidateProperties();
			}
			
		}
		
		
		private var _globalHightlightString:String;
		/**
		 * A String to highlight in the grid. 
		 */
		public function get globalHightlightString():String
		{
			return _globalHightlightString;
		}
		/**
		 * @private
		 */
		public function set globalHightlightString(value:String):void
		{
			if(_globalHightlightString != value){
				_globalHightlightString = value;
				_globalFilterOrHighLightDirty=true;
				matchedObjects=new ArrayCollection();
				if(value){
					for each(var matchedObject:Object in quickFind(value,globalHighlightColumns,false,true))
					matchedObjects.addItem(matchedObject);
				}
				currentMatch=1;			
				invalidateProperties();
				
			}
		}
		
		private var _globalHighlightColumns:Array=[];
		
		/**
		 * Array to search for when highlighting a term in specific columns 
		 */
		public function get globalHighlightColumns():Array
		{
			return _globalHighlightColumns;
		}
		
		/**
		 * @private
		 */
		public function set globalHighlightColumns(value:Array):void
		{
			_globalHighlightColumns = value;
			_globalFilterOrHighLightDirty=true;
			invalidateProperties();
		}
		
		private var _columnPickerRowCount:int=12;
		
		[Bindable]
		/**
		 * The number of columns to show in the column picker before a scrollbar is renderered 
		 */
		public function get columnPickerRowCount():int
		{
			return _columnPickerRowCount;
		}
		
		/**
		 * @private
		 */
		public function set columnPickerRowCount(value:int):void
		{
			_columnPickerRowCount = value;
		}
		
		
		
		
		/**
		 * Given a cell, returns its text color based upon whether or not
		 * it is selected and what the isLink property of the column is.
		 * @param cell
		 * @return int 		 
		 */		
		public function defaultCellTextColorFunction(cell:IFlexDataGridCell):*
		{
			if(cell is IFlexDataGridDataCell && cell.column){
				var hpCol:HPDataGridColumn = cell.column as HPDataGridColumn;
				if(cell.level.isItemSelected(cell.rowInfo.data)){
					//selected
					return hpCol&&hpCol.isLink?0x3333ff:0x000000;
				}else{
					//not selected
					return hpCol&&hpCol.isLink?0x33669:0x333333;
				}
			}
			return null;
		}
		/**
		 * Given a cell, returns its background color based upon whether it is
		 * a FlexDataGridHeaderCell and if the column is locked.
		 * @param cell
		 * @return int 		 
		 */		
		public function defaultCellBackgroundColorFunction(cell:IFlexDataGridCell):*
		{
			if(cell is FlexDataGridHeaderCell && cell.column && cell.column.isLocked){
				return 0xf9f9fa;
			}
			return null;
		}
		
		protected function onAfterExport(event:ExportEvent):void
		{
			
		}
		
		protected function onBeforPrint(event:FlexDataGridPrintEvent):void
		{
			event.printGrid.setStyle("horizontalGridLines",true);
			event.printGrid.setStyle("horizontalGridLineColor",0xDDDDDD);
			event.printGrid.enableMultiColumnSort = false;
			event.printGrid.rebuildHeader()
		}
		
		protected override function onCreationComplete(event:Event):void{
			
			super.onCreationComplete(event);
			printOptions.hideHiddenColumns=true;
			pdfOptions.hideHiddenColumns=true;
			excelOptions.hideHiddenColumns=true;
			wordOptions.hideHiddenColumns=true;
			
			
			pdfPrinter = new AlivePdfPrinter;
			columnLevel.filterFunction = globalFilterFunction; 
			addEventListener("rightClick",onRightClick);
			myMenu.addEventListener(MenuEvent.ITEM_CLICK,onMenuItemClick);
			myMenu.variableRowHeight = true;
		}
		
		protected function onMenuItemClick(event:MenuEvent):void
		{
			if(event.label.indexOf("Hide ")==0){
				onHideColumnClick(null);
			}else if(event.label.indexOf("Size All Columns to Fit")==0){
				onSizeAllToFitClick(null);
			}else if(event.label.indexOf("Size ")==0){
				onSizeToFitClick(null);
			}else if(event.label.indexOf("Lock First Column")==0){
				onLockColumnClick(null);
			}else if(event.label.indexOf("Show/Hide Columns")==0){
				onShowHideColumnsClick(null);
			}else if(event.label.indexOf("Show Toolbar")==0){
				onShowHideToolbarClick(null);
			}else if(event.item.hasOwnProperty("isCustomRightClick")){
				//this is a custom action
				onCellContextMenuSelect(event.item);
			}
			
		}
		mx_internal var cellUnderRigthClick:IFlexDataGridCell;
		protected function onRightClick(event:MouseEvent):void
		{
			
			
			//we are over a column header
			var items:Array=[];
			if(currentCell is FlexDataGridHeaderCell && currentCell.column && !currentCell.column.excludeFromSettings){
				
				if (showHeaderMenu) {
					var contextMenuItemHideColumn:Object=new Object();
					var contextMenuItemSizeToFitColumn:Object=new Object();
					var contextMenuItemSizeAllToFitColumn:Object=new Object();
					var contextMenuItemLockCol:Object=new Object();
					var contextMenuItemShowHideCols:Object=new Object();
					var contextMenuItemShowHideToolbar:Object=new Object();
					
					contextMenuItemHideColumn.caption="Hide " + currentCell.column.headerText;
					items.push(contextMenuItemHideColumn);
					
					contextMenuItemSizeToFitColumn.caption="Size " + currentCell.column.headerText + " to Fit";
					items.push(contextMenuItemSizeToFitColumn);
					
					contextMenuItemSizeAllToFitColumn.caption="Size All Columns to Fit";
					items.push(contextMenuItemSizeAllToFitColumn);
					
					
					items.push({type:"separator"});
					
					contextMenuItemLockCol.caption="Lock First Column";
					contextMenuItemLockCol.toggled=columns.length>0&&columns[0].columnLockMode==FlexDataGridColumn.LOCK_MODE_LEFT;
					contextMenuItemLockCol.type="check";
					contextMenuItemLockCol.separatorBefore=true;
					items.push(contextMenuItemLockCol);
					
					items.push({type:"separator"});
					contextMenuItemShowHideCols.caption="Show/Hide Columns...";
					contextMenuItemShowHideCols.separatorBefore=true;
					items.push(contextMenuItemShowHideCols);
					
					
					items.push({type:"separator"});
					contextMenuItemShowHideToolbar.caption="Show Toolbar";
					contextMenuItemShowHideToolbar.toggled=toolbar?toolbar.visible:null;
					contextMenuItemShowHideToolbar.type="check";
					contextMenuItemShowHideToolbar.separatorBefore=true;
					items.push(contextMenuItemShowHideToolbar);
					
				}
				//contextMenu.customItems=items;
			} else if(currentCell is IFlexDataGridDataCell && currentCell.column && !currentCell.column.excludeFromSettings
				&& (currentCell.column as HPDataGridColumn).linkModels.length>0 && currentCell.rowInfo.data[currentCell.column.dataField+"id"]){
				//Each LinkModel item corresponds to a column. The right click menu is displayed if a given cell has a corresponding 
				//value in rowIds and the column has LinkModel in the columnRightClickMenu
				var links:ArrayCollection = (currentCell.column as HPDataGridColumn).linkModels;
				for each(var link:LinkModel in links){
					var cmi:Object = new Object(); 
					cmi.caption=link.displayName;
					cmi.isCustomRightClick=true;
					items.push(cmi);
				}
				//contextMenu.customItems=cmis;
			}
			else{
				//contextMenu.customItems=[];
			}
			if(items.length>0){
				//UIUtils.addPopUp(menu,FlexGlobals.topLevelApplication as DisplayObject,false);
				myMenu.labelField = "caption";
				myMenu.dataProvider=items;
				
				if(myMenu.visible){
					myMenu.hide();
				}
				myMenu.show(event.stageX,event.stageY);
				cellUnderRigthClick=currentCell;
			}
		}		
		public var myMenu:Menu = Menu.createMenu(null, null, false);
		
		protected function onSizeToFitClick(event:ContextMenuEvent):void
		{
			cellUnderRigthClick.column.columnWidthMode = FlexDataGridColumn.COLUMN_WIDTH_MODE_FIT_TO_CONTENT;
			reDraw();
			validateNow();
			persistPreferences();
		}
		
		protected function onSizeAllToFitClick(event:ContextMenuEvent):void
		{
			for each(var col:FlexDataGridColumn in getColumns()){
				col.columnWidthMode = FlexDataGridColumn.COLUMN_WIDTH_MODE_FIT_TO_CONTENT;
			}
			reDraw();
			validateNow();
			persistPreferences();
		}
		
		protected function onShowHideToolbarClick(event:ContextMenuEvent):void
		{
			if(toolbar)
				toolbar.visible=toolbar.includeInLayout=!toolbar.includeInLayout;
		}
		
		protected function onShowHideColumnsClick(event:ContextMenuEvent):void
		{
			var pop:ShowHideColumnPicker = new ShowHideColumnPicker;
			pop.grid = this;
			pop.width=Math.max(150,cellUnderRigthClick?cellUnderRigthClick.width:100);
			pop.selectedItems = visibleColumns;
			UIUtils.addPopUp(pop,FlexGlobals.topLevelApplication as DisplayObject);
			UIUtils.positionBelow(pop, cellUnderRigthClick||this,0,0,true,"");
		}
		
		protected function onLockColumnClick(event:ContextMenuEvent):void
		{
			/*for each(var col:FlexDataGridColumn in columnLevel.leftLockedColumns){
			col.columnLockMode=FlexDataGridColumn.LOCK_MODE_NONE;
			}
			cellUnderRigthClick.column.columnLockMode=cellUnderRigthClick.isLeftLocked?FlexDataGridColumn.LOCK_MODE_NONE:FlexDataGridColumn.LOCK_MODE_LEFT;*/
			columns[0].columnLockMode=columns[0].isLeftLocked?FlexDataGridColumn.LOCK_MODE_NONE:FlexDataGridColumn.LOCK_MODE_LEFT;;
			reDraw();
			persistPreferences();
			
		}
		
		protected function onHideColumnClick(event:ContextMenuEvent):void
		{
			cellUnderRigthClick.column.visible=false;
			persistPreferences();
		}		
		
		/**
		 * In this method we evaluate if we are over a column header and present appropriate 
		 * column header menu options
		 */		
		protected function onContextMenuCreated(event:ContextMenuEvent):void
		{
			//contextMenu.hideBuiltInItems();
			
			
		}
		
		protected function onCellContextMenuSelect(item:Object):void
		{
			var caption:String = item.caption;
			var links:ArrayCollection = (cellUnderRigthClick.column as HPDataGridColumn).linkModels;
			var rowId:String=cellUnderRigthClick.rowInfo.data[cellUnderRigthClick.column.dataField+"id"];
			for each(var link:LinkModel in links){
				if(link.displayName==caption){
					UIUtils.openBrowserPopup(link.url + "&moref="+rowId);
					// TODO: make a call to provider to get the right url by passing the rowId.
					
				}
			}
			
		}
		/**
		 * In this method, we handle context menu item clicks.
		 */		
		protected function onContextMenuItemClick(event:ContextMenuEvent):void
		{
			var mi:ContextMenuItem = null;	
			
		}
		protected function onPersistPreferences(event:PreferencePersistenceEvent):void
		{
			if(preferencePersistenceMode=='server'){
				/*OSBusinessService.getInstance().persistPreferences(preferencePersistenceKey,event.preferenceXml,function(e:ResultEvent,tok:Object):void{
				UIUtils.showMessage("Preferences Saved to Server");
				});*/
			}
			var customDataString:String = "";
			if(this.columnLevel.leftLockedColumns.length>0){
				customDataString = (this.columnLevel.leftLockedColumns[0] as FlexDataGridColumn).uniqueIdentifier;
			}
			event.customData=customDataString;
			
		}
		
		protected function onClearPreferences(event:PreferencePersistenceEvent):void
		{
			if(preferencePersistenceMode=='server'){
				/*OSBusinessService.getInstance().clearPreferences(preferencePersistenceKey,function(e:ResultEvent,tok:Object):void{
				UIUtils.showMessage("Preferences Cleared from Server");
				});*/
				
			}
		}
		
		protected function onLoadPreferences(event:PreferencePersistenceEvent):void
		{
			if(preferencePersistenceMode=='server'){
				/*OSBusinessService.getInstance().getSavedPreferences(preferencePersistenceKey,function(e:ResultEvent,tok:Object):void{
				if(e.result)
				preferences = e.result.toString();
				});*/
			}
			if(event.customData){
				var col:FlexDataGridColumn = getColumnByUniqueIdentifier(event.customData);
				if(col){
					col.columnLockMode=FlexDataGridColumn.LOCK_MODE_LEFT;
					reDraw();
				}
			}
		}
		public override function loadPreferences():void{
			super.loadPreferences();
		}
		
		public override function trackChange(changedItem:Object, changeType:String, 
											 changeLevel:FlexDataGridColumnLevel=null, 
											 changedProperty:String=null, previousValue:*=null, newValue:*=null):void{
			if(changeType == ChangeInfo.CHANGE_TYPE_UPDATE && previousValue==null && newValue==""){
				//-          Edit mode:  clicking on a cell containing a null value and then clicking away marks
				// that field as edited.  Here, we prevent this.
				
			}else
				super.trackChange(changedItem,changeType,changeLevel,changedProperty,previousValue,newValue);
		}
		
		protected override function commitProperties():void{
			super.commitProperties();
			if(_globalFilterOrHighLightDirty){
				_globalFilterOrHighLightDirty=false;
				rebuildBody(true);
				
				if(!globalHightlightAll && globalHightlightString){
					bodyContainer.validateNow();
					for each(var row:RowInfo in bodyContainer.rows){
						for each(var comp:ComponentInfo in row.cells){
							var cell:IFlexDataGridCell = comp.component as IFlexDataGridCell;
							if(cell.text && (cell is IHighlightableCell)){
								if(cell.text.toLocaleLowerCase().indexOf(globalHightlightString.toLocaleLowerCase())>=0){
									(cell as IHighlightableCell).highlight(true);
									return;
								}
							}
						}
					}
				}
			}
		}
		/**
		 * A global filter function that matches against gloalSearchString 
		 * @param item Item to match
		 * @return 	Whether or not the item exists in the dataprovider.
		 * 
		 */		
		protected function globalFilterFunction(item:Object):Boolean
		{
			if(globalFilterString){
				var colsToMatch:Array=(globalFilterColumns.length==0)?getColumns():globalFilterColumns;
				for each(var col:FlexDataGridColumn in colsToMatch){
					var str:String = col.itemToLabel(item);
					if(str && (str.toLocaleLowerCase().indexOf(globalFilterString.toLocaleLowerCase())>=0)){
						return true;
					}
				}
			}else{
				return true;
			}
			return false;
		}
		
		/**
		 * @private 
		 */
		protected override function drawDefaultSeperators():void
		{
			leftLockedVerticalSeperator.visible=rightLockedVerticalSeperator.visible=false;
			
			if(columnLevel.leftLockedColumns.length>0){
				leftLockedVerticalSeperator.x=leftLockedContent.width;
				leftLockedVerticalSeperator.y=headerContainer.y;
				leftLockedVerticalSeperator.height=(headerContainer.height+filterContainer.height+bodyContainer.height+footerContainer.height)-isHScrollBarVisible;
				leftLockedVerticalSeperator.width = getStyle("lockedSeperatorThickness")
				drawDoubleLineSeparator(leftLockedVerticalSeperator);
				leftLockedVerticalSeperator.visible=true;
			}
			if(columnLevel.rightLockedColumns.length>0){
				rightLockedVerticalSeperator.x=rightLockedContent.x;
				rightLockedVerticalSeperator.y=headerContainer.y;
				rightLockedVerticalSeperator.height=(headerContainer.height+filterContainer.height+bodyContainer.height+footerContainer.height)-isHScrollBarVisible;
				rightLockedVerticalSeperator.width = getStyle("lockedSeperatorThickness")
				drawDoubleLineSeparator(rightLockedVerticalSeperator);
				rightLockedVerticalSeperator.visible=true;
			}
		}
		/**
		 * Draws the dual stripe from the requirements 
		 * @param leftLockedVerticalSeperator
		 * 
		 */		
		protected function drawDoubleLineSeparator(verticalSeperator:Sprite):void
		{
			verticalSeperator.graphics.lineStyle(2, 0xa8acaf);
			verticalSeperator.graphics.moveTo(0, 1);
			verticalSeperator.graphics.lineTo(0, unscaledHeight);
			verticalSeperator.graphics.lineStyle(1, 0xefefef);
			verticalSeperator.graphics.moveTo(2, 1);
			verticalSeperator.graphics.lineTo(2, unscaledHeight);
			
		}
		/**
		 * Since we have custom message bar, we override it to manage that behavior.
		 */	
		public override function showSpinner(msg:String=""):void
		{
			var pg:HPToolbar = pager as HPToolbar;
			if(pg){
				pg.progressBar.mode = ProgressBarMode.EVENT;
				pg.progressBar.visible=true;
			}
		}
		/**
		 * Displays a message without the spinner label.
		 * @param msg
		 */		
		public override function showMessage(msg:String):void{
			
		}
		/**
		 * Since we have custom message bar, we override it to manage that behavior.
		 */		
		public override function hideSpinner():void
		{
			var pg:HPToolbar = pager as HPToolbar;
			if(pg){
				pg.progressBar.mode = ProgressBarMode.MANUAL;
				pg.progressBar.visible=false;
			}
		}
		/**
		 * Once we have the backend populate the table model, we simply provide the details to the model parser to convert it to 
		 * the grid's API
		 */		
		public function loadFromTabelModel(tableModel:TableModel):void{
			this.modelParserBehavior.tableModel=tableModel;
			this.modelParserBehavior.apply(this);
			validateNow();
			loadPreferences();
			
			/*Alert.show("test","test",Alert.OK,this,function(e:CloseEvent){
			var ba:ByteArray = new ByteArray();
			ba.writeObject(tableModel);
			var f:FileReference=new FileReference();
			f.save(ba,"Sample.txt");});
			*/
			
		}
		
		
		public override function placeBottomBar():void{
			//super.placeBottomBar();
		}
		/**
		 * @private
		 */		
		protected override function createComponents(currentScroll:int=0):void
		{
			super.createComponents(currentScroll);
			setChildIndex(bodyContainer,getChildIndex(leftLockedContent)+1);
		}
		
		
		private var _tableModel:TableModel;
		[Bindable()]
		public function get tableModel():TableModel
		{
			return _tableModel;
		}
		
		public function set tableModel(value:TableModel):void
		{
			if(value && (value != _tableModel)){
				_tableModel = value;
				this.loadFromTabelModel(value);
				
			}
			
		}
		[Bindable()]
		/**
		 * The toolbar component that sits on top of the grid 
		 */		
		public var toolbar:IUIComponent;		
		
		[Bindable()]
		/**
		 * The showHeaderMenu is a boolean that decides if we want to show the right click menu for the Column Header. 
		 */		
		public var showHeaderMenu:Boolean = true;	
		
		/**
		 * A list of items from the dataprovider that matches the search criteria 
		 */		
		protected var matchedObjects:ArrayCollection=new ArrayCollection();
		/**
		 * The currently matched object 
		 */		
		private var currentMatch:int=1;
		/**
		 * Handler for the enter key
		 */		
		public function gotoNextHighlight(backwards:Boolean=false):void
		{
			if(backwards){
				if(currentMatch>1)
					currentMatch--;
			} 
			else if(currentMatch<matchedObjects.length){
				currentMatch++;
			}
			
			if(currentMatch<=matchedObjects.length && currentMatch>0){
				var currentMatchedObject:Object=matchedObjects[currentMatch-1];
				gotoItem(currentMatchedObject.item,false);
				if(!this.bodyContainer.isInVisibleHorizontalRange(currentMatchedObject.col.x,currentMatchedObject.col.width)){
					this.gotoHorizontalPosition(currentMatchedObject.col.x);
					validateNow();
				}
				var cell:IHighlightableCell = getCellForRowColumn(currentMatchedObject.item,currentMatchedObject.col as FlexDataGridColumn) as IHighlightableCell;
				if(cell){
					for each(var row:RowInfo in bodyContainer.rows){
						for each (var cellComp:ComponentInfo in row.cells){
							var tdgc:IHighlightableCell = cellComp.component as IHighlightableCell;
							if(tdgc)
								tdgc.removeHighlight();
						}
					}
					cell.highlight(true);
				}
			}
		}
		
	}
}