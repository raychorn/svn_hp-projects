/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.utils
{
	import assets.components.Hpic4vc_BaseMediator;
	import assets.components.Hpic4vc_DataGrid;
	
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.ApplicationDataModel;
	import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
	import com.hp.asi.hpic4vc.ui.model.FooterModel;
	import com.hp.asi.hpic4vc.ui.model.HeaderModel;
	import com.hp.asi.hpic4vc.ui.model.HealthModel;
	import com.hp.asi.hpic4vc.ui.model.HierarchialDataModel;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	import com.hp.asi.hpic4vc.ui.model.UserInfoModel;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_manage_uiView;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.display.DisplayObject;
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	import mx.collections.Sort;
	import mx.collections.SortField;
	import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
	import mx.core.FlexGlobals;
	import mx.core.IChildList;
	import mx.core.UIComponent;
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.managers.PopUpManager;
	import mx.resources.ResourceManager;
	import mx.utils.ObjectUtil;
	
	import spark.components.Application;

	/** 
	 * This class contains the helper or utility methods that will used across 
	 * hpic4vc_ui component. 
	 */
	public class Helper
	{
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_uiMediator");
		private static var countTabs:int = 0;
		private static var countHeader:int = 0;
		private static var countFooter:int = 0;
		
		public static var _view:Hpic4vc_manage_uiView;
		public static var _contextObject:IResourceReference;
		public static var _proxy:Hpic4vc_providerProxy;
		
		
		
		
		
		// Utilities to load strings and images from resource file 
		public static function getString(key:String) : String {
			return ResourceManager.getInstance().getString('Hpic4vc_uiResources', key);
		}
		
		public static function getImage(key:String) : Class {
			return ResourceManager.getInstance().getClass('Hpic4vc_uiResources', key);
		}
		
		public static  function createDataGrid(tableModel:TableModel):DataGridWrapper {
			
			if (tableModel == null) {
				return null;
			} else if (tableModel.errorMessage != null) {
				_logger.error(tableModel.errorMessage);
				return null;
			}
			
			var columnHeaders:ArrayCollection = tableModel.columnNames;
			var columnWidths:ArrayCollection = tableModel.columnWidth;
			
			var cols:Array = new Array();
			for (var i:int = 0; i < columnHeaders.length; i++) {
				var column:AdvancedDataGridColumn = new AdvancedDataGridColumn();
				column.headerText = columnHeaders.getItemAt(i).toString();
				
				// Set the column width
				if (columnWidths != null && columnWidths.length && columnWidths.getItemAt(i) != null) {
					column.width = columnWidths.getItemAt(i).toString(); 
				}
				
				// Set the dataField
				column.dataField = columnHeaders.getItemAt(i).toString();
				
				// Set the column to be sortable
				column.sortable = true;
				
				// Set the sort comparision function defined in Hpic4vc_Datagrid
				column.sortCompareFunction = Hpic4vc_DataGrid.sortCompare;
				
				// Set the data
				column.labelFunction = function(item:Object, column:AdvancedDataGridColumn):String {
					var cellString:String;
					for (var j:int = 0; j < columnHeaders.length; j++) {
						if (column.headerText == columnHeaders.getItemAt(j)) {
							if ((item as ArrayCollection).getItemAt(j) == null) {
								return "";
							}
							cellString = (item as ArrayCollection).getItemAt(j).toString();
							return cellString;
						}
					}
					return cellString;
				};
				cols.push(column);
			}
			
			var dataGridWrapper:DataGridWrapper = new DataGridWrapper();
			dataGridWrapper.columns = cols;
			dataGridWrapper.list = tableModel.rowFormattedData;
			return dataGridWrapper;
		}
        
		/**
		 * A method to sort the given array collection as per the specific 
		 * column or field.
		 * 
		 * @param ar			An ArrayCollection that needs to be sorted.
		 * @param fieldName		A String value representing the field that is 
		 * 						used to sort the arrayCollection.
		 * @param isNumeric		A Boolean value specifying if it is a numeric 
		 * 						sort or alphabetic sort.
		 * 
		 */
		public static function sortArrayCollection(ar:ArrayCollection, fieldName:String, isNumeric:Boolean):void 
		{
			var dataSortField:SortField = new SortField();
			dataSortField.name = fieldName;
			dataSortField.numeric = isNumeric;
			var numericDataSort:Sort = new Sort();
			numericDataSort.fields = [dataSortField];
			ar.sort = numericDataSort;
			var result:Boolean = ar.refresh();
		}
		
		public static function onGettingHeaderDetails(event:MethodReturnEvent):void {
			if (_view != null && _contextObject != null && _proxy != null) {
				_logger.debug("Received HPIC4VC data in onGettingHeader()");
				
				if (event == null) {
					_view.noHeaderInfoFound = Helper.getString("noRecordsFound");
					return;
				} else if (event.error != null) {
					if (event.error.toString().match("DeliveryInDoubt")) {
						_logger.warn("DeliveryInDoubt exception occurred.  Count: " + countHeader);
						// Re try to request data for not more than 2 times
						if (countHeader < 2) {
							countHeader ++;
							_proxy.getHeader(_contextObject.uid, onGettingHeaderDetails, _contextObject);
							return;
						} else {
							_view.headerErrorMessage = event.error.message;
							_view.currentState = "DataAvailable";
							return;
						}
					} else {
						_view.headerErrorMessage = event.error.message;
						_view.currentState = "DataAvailable";
						return;
					}
				} else if (event.result == null) {
					_view.noHeaderInfoFound = Helper.getString("noRecordsFound");
					_view.currentState = "DataAvailable";
					return;
				} else if ((event.result as HeaderModel).errorMessage != null) {
					_view.headerErrorMessage = (event.result as HeaderModel).errorMessage;
					_view.currentState = "DataAvailable";
					return;
				} else {
					_view.noHeaderInfoFound = "";
					_view.headerErrorMessage = "";
					_view.currentState = "DataAvailable";
				}
				
				var headerInfo:HeaderModel = event.result as HeaderModel;
				
				createHeader(headerInfo, _view, _contextObject);
			}
			else {
				_logger.warn("View and/or ContextObject is null.  Returning from onGettingResult()");
				return;
			}
		}
		
		public static function createHeader(headerInfo:HeaderModel, _view:Hpic4vc_manage_uiView, _contextObject:IResourceReference):void {
			if (_view != null && _contextObject != null) {
				_view.header.visible = true;
				// Checks for null and errors
				if (headerInfo == null) {
					_view.noHeaderInfoFound = Helper.getString("noInfoFound");
					return;
				} else if (headerInfo.errorMessage != null) {
					_view.noHeaderInfoFound = headerInfo.errorMessage;
					_logger.error(headerInfo.errorMessage);
					return;
				}
				
				// Set the objReference Id
				_view.objReferenceId = _contextObject.uid;
				
				// Get Health status
				var healthStatus:HealthModel = headerInfo.health as HealthModel;
				_view.healthConsolidatedStatus = healthStatus.consolidatedStatus as String;	   
				_view.warningCount = healthStatus.warnCount as String;
				_view.errorCount = healthStatus.errorCount as String;
				_view.okCount = healthStatus.okCount as String;
				_view.informationCount = healthStatus.infoCount as String;
				
				// To get the I18Ned label for the VMware entity type.
				var contextObjectType:String = _contextObject.type;
				_view.objReferenceName = Helper.getString(contextObjectType) 
					+ " \"" 
					+ headerInfo.objReferenceName
					+ "\"";
				
				_view.productInfo = headerInfo.productInfo;
				_view.enclosureInfo = headerInfo.enclosureInfo;
				
				// Limit the display of Tasks count to 10.  If not, the tooltip would be too large!! 
				if (headerInfo.tasks.length > 10) {
					var tasksArrayCollection:ArrayCollection = new ArrayCollection();
					for (var i:int = 0; i < 10; i++) 
					{
						tasksArrayCollection.addItem(headerInfo.tasks.getItemAt(i));
					}
					_view.tasksHoverValue = tasksArrayCollection;
				} else{
					_view.tasksHoverValue = headerInfo.tasks as ArrayCollection;
				}
				
				_view.actionsCollection = headerInfo.actions as ArrayCollection;
				
				Hpic4vc_BaseMediator.actionsMenu = headerInfo.actions as ArrayCollection;
				
				_view.configurations = headerInfo.configurations as ArrayCollection;
				
				_view.helpUrl = headerInfo.helpUrl as String;
				
				_view.refreshList = headerInfo.refresh as ArrayCollection;
				
				_view.showRefreshHover = headerInfo.showRefreshHover as Boolean;
			} else {
				_logger.warn("View and/or ContextObject is null.  Returning from createHeader()");
				return;
			}
		}
		
		public static function onGettingFooterDetails(event:MethodReturnEvent):void {
			if (_view != null && _contextObject != null && _proxy != null) {
				_view.footer.visible = true;
				_logger.debug("Received HPIC4VC data in onGettingFooter()");
				if (event == null) {
					_view.noFooterInfoFound = Helper.getString("noRecordsFound");
					return;
				} else if (event.error != null) {
					_logger.warn("DeliveryInDoubt exception occurred.  Count: " + countHeader);
					if (event.error.toString().match("DeliveryInDoubt")) {
						// Re try to request data for not more than 2 times
						if (countFooter < 2) {
							countFooter ++;
							_proxy.getFooter(_contextObject.uid, onGettingFooterDetails, _contextObject);
							return;
						} else {
							_view.footerErrorMessage = event.error.message;
							return;
						}
					} else {
						_view.footerErrorMessage = event.error.message;
						return;
					}
				} else if (event.result == null) {
					_view.noFooterInfoFound = Helper.getString("noRecordsFound");
					return;
				} else if ((event.result as FooterModel).errorMessage != null) {
					_view.footerErrorMessage = (event.result as FooterModel).errorMessage;
					return;
				} else {
					_view.noFooterInfoFound = "";
					_view.footerErrorMessage = "";
					_view.currentState = "DataAvailable";
				}
				var footerInfo:FooterModel = event.result as FooterModel;
				
				_view.footerData = footerInfo;
			}
			else {
				_logger.warn("View and/or ContextObject is null.  Returning from onGettingResult()");
				return;
			}
		}
		
		public static function createHierarchialDataGrid(hierarchialDataModel:HierarchialDataModel):DataGridWrapper {
			
			/*if (hierarchialDataModel == null) {
				return null;
			} else if (hierarchialDataModel.errorMessage != null) {
				_logger.error(hierarchialDataModel.errorMessage);
				return null;
			}
			
			var columnHeaders:ArrayCollection = hierarchialDataModel.columnNames;
			var columnWidths:ArrayCollection = hierarchialDataModel.columnWidth;
			
			var cols:Array = new Array();
			for (var i:int = 0; i < columnHeaders.length; i++) {
				var column:AdvancedDataGridColumn = new AdvancedDataGridColumn();
				column.headerText = columnHeaders.getItemAt(i).toString();
				
				// Set the column width
				if (columnWidths != null && columnWidths.length && columnWidths.getItemAt(i) != null) {
					column.width = columnWidths.getItemAt(i).toString(); 
				}
				
				// Set the dataField
				column.dataField = columnHeaders.getItemAt(i).toString();
				
				// Set the data
				column.labelFunction = function(item:Object, column:AdvancedDataGridColumn):String {
					var cellString:String;
					for (var j:int = 0; j < columnHeaders.length; j++) {
						if ((item as ArrayCollection).getItemAt(j) == null) {
							return "";
						}
						cellString = (item as ArrayCollection).getItemAt(j).toString();
						return cellString;
					}
					return cellString;
				};
				cols.push(column);
			}*/
			
			var dataGridWrapper:DataGridWrapper = new DataGridWrapper();
			//dataGridWrapper.columns = cols;
			dataGridWrapper.list = hierarchialDataModel.rowFormattedData;
			return dataGridWrapper;
		}
		
		/**
		 * Returns all the popups inside an application. Only the popups whose base
		 * class is UIComponent are returned.
		 *
		 * @param applicationInstance
		 *   Application instance. If null, Application.application is used.
		 * @param onlyVisible
		 *   If true, considers only the visible popups.
		 * @return All the popups in the specified application.
		 */
		public static function getAllPopups(applicationInstance: Object = null,
											onlyVisible: Boolean = false): ArrayCollection
		{
			var result: ArrayCollection = new ArrayCollection();
			
			if (applicationInstance == null)
			{
				// NOTE: use this line for Flex 4.x and higher
				//applicationInstance = FlexGlobals.topLevelApplication;
				
				// NOTE: use this line for Flex 3.x and lower
				applicationInstance =FlexGlobals.topLevelApplication;
			}
			
			var rawChildren: IChildList = applicationInstance.systemManager.rawChildren;
			
			for (var i: int = 0; i < rawChildren.numChildren; i++)
			{
				var currRawChild:DisplayObject = rawChildren.getChildAt(i);
				
				if ((currRawChild is UIComponent) && UIComponent(currRawChild).isPopUp)
				{
					if (!onlyVisible || UIComponent(currRawChild).visible)
					{
						result.addItem(currRawChild);
					}
				}
			}
			
			return result;
		}
		
		/**
		 * Closes all the popups belonging to an application. Only the popups
		 * whose base class is UIComponent are considered.
		 *
		 * @param applicationInstance
		 *   Application instance. If null, Application.application is used.
		 * @return The list of the closed popups.
		 */
		public static function closeAllPopups(applicationInstance: Object = null): ArrayCollection
		{
			var allPopups: ArrayCollection = getAllPopups(applicationInstance);
			
			for each (var currPopup: UIComponent in allPopups)
			{
				PopUpManager.removePopUp(currPopup);
			}
			
			return allPopups;
		}
		
		public static function updateUserAuthorization(event:MethodReturnEvent):void
		{
			var userInfo:UserInfoModel;
			var appModel:ApplicationDataModel = ApplicationDataModel.getInstance();
			if (event && event.result)
			{
				appModel.userValidated = true;
				userInfo = event.result as UserInfoModel;
				if(userInfo && userInfo.errorMessage != null)
				{
					appModel.isAuthorized = true;
					
				}
				else if (userInfo && userInfo.roleId == "-1")
				{
					appModel.isAuthorized = true;
				}
				else if (userInfo.informationMessage != null)
				{
					appModel.isAuthorized = true;
				}
				else
				{
					appModel.isAuthorized = false;
				}
				
				
			}
			else
			{
				appModel.isAuthorized = false;
				appModel.userValidated = false;
			}
			
			
			
			
		}
		
		
		
	}
}