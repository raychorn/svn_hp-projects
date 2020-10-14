/* Copyright 2012 Hewlett-Packard Development Company, L.P.  */
package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.LaunchToolModel")]
	/**
	 * A data model of link properties to display.
	 * This Flex class maps to the LinkModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class LaunchToolModel extends BaseModel{
		public var id:String;
		public var links:ArrayCollection;
		
		
		[Transient]
		public var storageLink:ArrayCollection;
		
		[Transient]
		public var iLOLink:ArrayCollection;
		
		[Transient]
		public var oaLink:ArrayCollection;
		
		[Transient]
		public var vcLink:ArrayCollection;
		
		[Transient]
		public var vcemLink:ArrayCollection;
		
		[Transient]
		public var ipmLink:ArrayCollection;
		
		[Transient]
		public var simLink:ArrayCollection;
		
		[Transient]
		public static const STORAGE_LAUNCH_ID:String = "Storage";
		
		[Transient]
		public static const ILO_LAUNCH_ID:String = "ilo_launch_tool";
		
		[Transient]
		public static const VCM_LAUNCH_ID:String = "vcm_launch_tool";
		
		[Transient]
		public static const OA_LAUNCH_ID:String = "oa_launch_tool";
		
		[Transient]
		public static const VCEM_LAUNCH_ID:String = "vcem_launch_tool";
		
		[Transient]
		public static const IPM_LAUNCH_ID:String = "ipm_launch_tool";
		
		[Transient]
		public static const SIM_LAUNCH_ID:String = "sim_launch_tool";
		
		
		[Transient]
		public static const CLUSTER_OA_LAUNCH_ID:String = "cluster_oa_launch_tool";
	}
}