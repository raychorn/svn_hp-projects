package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.NewsFeedModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.EventDispatcher;
	
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	public class Hpic4vc_Overview_NewsfeedMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_Overview_Newsfeed;
		private var _proxy:Hpic4vc_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_Overview_NewsfeedMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Overview_Newsfeed {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Overview_Newsfeed):void {
			_view = value;
			_proxy = new Hpic4vc_providerProxy();
		}
		
		override public function set contextObject(value:Object):void {
            if (_view == null){
                _logger.warn('Overview_Newsfeed view is null');
                return;
            }
			_contextObject = _view._contextObject;
			if (_contextObject != null && IResourceReference(value) != null) {  
				if (_contextObject.uid != IResourceReference(value).uid) {
					clearData();
					return;
				}
			}
			requestData();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data for news summary.");
				_proxy.getNewsFeed(_contextObject.uid, onGettingNewsFeedSummary, _contextObject);
			} else {
				_logger.warn("ContextObject is null, hence not requesting for news summary data.");
				return;
			}
		}
		
		private function onGettingNewsFeedSummary(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.warn("Received HPIC4VC data in onGettingResult() for for news summary data.");
				_view.noInfoFoundLabel = "";
				_view.errorFoundLabel = "";
				
				if (event == null) {
					_view.errorFoundLabel = Helper.getString("errorOccurred");
					return;
				} else if (event.error != null) {
					_logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
					if (event.error.toString().match("DeliveryInDoubt")) {
						// Re try to request data for not more than 2 times
						if (count < 2) {
							count ++;
							requestData();
							return;
						} else {
							_view.errorFoundLabel = event.error.message;
							return;
						}
					} else {
						_view.errorFoundLabel = event.error.message;
						return;
					}
				} else if (event.result == null) {
					_view.errorFoundLabel = Helper.getString("errorOccurred");
					return;
				} else if ((event.result as NewsFeedModel).errorMessage != null) {
					_view.errorFoundLabel = (event.result as NewsFeedModel).errorMessage;
					return;
				} else if ((event.result as NewsFeedModel).informationMessage != null) {
					_view.noInfoFoundLabel = (event.result as NewsFeedModel).informationMessage;
					return;
				} 
				
				var newsFeedModel:NewsFeedModel = event.result as NewsFeedModel;
				
				_view.newsData = newsFeedModel.newsItems;
			}
			else {
				_logger.warn("View is null.  Returning from onGettingResult() for news summary data.");
				return;
			}
		}
    }
}