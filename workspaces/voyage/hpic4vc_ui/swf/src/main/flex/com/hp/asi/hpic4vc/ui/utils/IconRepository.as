package com.hp.asi.hpic4vc.ui.utils
{
	public class IconRepository
	{
		public function IconRepository()
		{
		}
		
		// Datagrid related icons
		[Embed('/assets/images/alert.png')]
		public static const alert:Class;
		
		[Embed('/assets/images/binocular.jpg')]
		public static const binocular:Class;
		
		[Embed('/assets/images/grey-next-icon.png')]
		public static const greyNextIcon:Class;
		
		[Embed('/assets/images/grey-previous.png')]
		public static const greyPrevious:Class;
		
		[Embed('/assets/images/info_black.png')]
		public static const infoBlack:Class;
		
		
		[Embed('/assets/images/magnifyingGlassIcon.png')]
		public static const magnifyingGlassIcon:Class;
		
		[Embed('/assets/images/export.png')]
		public static const tableExportIcon:Class;
		
		[Embed('/assets/images/arrow_down.png')]
		public static const downArrow:Class;
		[Embed('/assets/images/close2.png')]
		public static const close:Class;
		
		// Warning
		[Embed('/assets/images/statusWarning.png')]
		public static const warning:Class;
		[Embed('/assets/images/statusWarning.png')]
		public static const degraded:Class;
		
		// Error
		// For Tasks and Newsfeed Status
		[Embed('/assets/images/error.png')]
		public static const news_tasks_error:Class;
		[Embed('/assets/images/error.png')]
		public static const error:Class;
		
		// For Health Status
		[Embed('/assets/images/statusRed.png')]
		public static const failed:Class;
		
		// Info
		[Embed('/assets/images/info.png')]
		public static const info:Class;
		[Embed('/assets/images/info.png')]
		public static const information:Class;
		
		// Success		
		// Tasks and Newsfeed Status
		[Embed('/assets/images/success.png')]
		public static const completed:Class;
		[Embed('/assets/images/success.png')]
		public static const in_progress:Class;
		[Embed('/assets/images/success.png')]
		public static const monitoring:Class;
		[Embed('/assets/images/success.png')]
		public static const scheduled:Class;
		[Embed('/assets/images/success.png')]
		public static const cancelling:Class;		
		[Embed('/assets/images/success.png')]
		public static const normal:Class;
		[Embed('/assets/images/success.png')]
		public static const queued:Class;
		[Embed('/assets/images/success.png')]
		public static const running:Class;
		[Embed('/assets/images/success.png')]
		public static const canceled:Class;
		
		// Health Status
		[Embed('/assets/images/statusGreen.png')]
		public static const good:Class;
		[Embed('/assets/images/statusGreen.png')]
		public static const ok:Class;
		
		// Unknown
		[Embed('/assets/images/statusUnknown.png')]
		public static const unknown:Class;
	}
}