package com.hp.asi.hpic4vc.ui.controls.grids.behaviors
{
	import com.flexicious.nestedtreedatagrid.FlexDataGridColumn;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	import com.hp.asi.hpic4vc.ui.controls.grids.HPDataGrid;
	import com.hp.asi.hpic4vc.ui.controls.grids.HPDataGridColumn;
	import com.hp.asi.hpic4vc.ui.utils.IconRepository;
	
	import flash.utils.Dictionary;
	
	import mx.collections.ArrayCollection;
	
	/**
	 * 
	 * @author Flexicious-106
	 * 
	 */	
	public class ModelParserBehavior extends BehaviorBase
	{
		[Bindable]
		public var tableModel:TableModel;
		public function ModelParserBehavior()
		{
			super();
		}
		
		/**
		 * @inheritDoc
		 */		
		public override function apply(grid:HPDataGrid):void{
			var cols:Array=[];
			var dp:ArrayCollection = new ArrayCollection;
			var rowIndex:int=0;
			var cleanColNames:Dictionary=new Dictionary;
			var colIndex:int=0;
			
			
			for each(var name:String in tableModel.columnNames){
				var col:HPDataGridColumn  = new HPDataGridColumn;
				col.dataField= cleanColumnName(name);
				cleanColNames[name]=col.dataField;
				col.headerText=name; 
				col.sortField=col.dataField+"raw";
				col.headerToolTip=tableModel.columnToolTips[colIndex]; 
				col.width = tableModel.columnWidth[colIndex];
				col.visible = tableModel.isColumnVisible[colIndex];
				col.truncateToFit = true;
				col.linkModels = tableModel.columnRightClickMenu[colIndex]||col.linkModels;
				if(tableModel.hasIcons[colIndex]){
					col.setStyle("paddingLeft",25);//space for icon
					col.enableIcon=true;
					col.iconField=col.dataField+"icon";
					col.setStyle("iconLeft",2);
					col.setStyle("iconTop",0);
				}
				if(tableModel.isColumnNumeric[colIndex]){
					col.sortNumeric=true;
				}
				cols.push(col);
				colIndex++;
			}
			rowIndex=0;
			for each(var row:ArrayCollection in tableModel.rowFormattedData){
				var obj:Object={};
				var cellIdx:int=0;
				for each(var cell:String in row){
					var cleanColName:String=cleanColNames[tableModel.columnNames[cellIdx]];
					obj[cleanColName]=cell;
					obj[cleanColName+"raw"]= tableModel.rowRawData[rowIndex][cellIdx];
					obj[cleanColName+"id"]= tableModel.rowIds[rowIndex][cellIdx];
					if(tableModel.hasIcons[cellIdx]){
						obj[cleanColName+"icon"]=IconRepository[tableModel.rowIcons[rowIndex][cellIdx]];
					}
					cellIdx++;
				}
				dp.addItem(obj);
				rowIndex++;
			}
			
			
			//			var extraCol:HPDataGridColumn=new HPDataGridColumn;
			//			extraCol.minWidth=1;
			//			extraCol.width=1;
			//			extraCol.setStyle("paddingLeft",0);
			//			extraCol.setStyle("paddingRight",0);
			//			extraCol.excludeFromExport=true;
			//			extraCol.excludeFromPrint=true;
			//			extraCol.excludeFromSettings=true;
			//			cols.push(extraCol);
			grid.setColumns(cols);
			grid.setDataProvider(dp);
			grid.informationMessage=tableModel.informationMessage;
			grid.errorMessage=tableModel.errorMessage;
		}
		
		protected function cleanColumnName(param0:Object):String
		{
			return param0?param0.toString().replace(/[\[\]\. \-]/g,""):"";
		}
	}
}