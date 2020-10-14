package com {
	import flash.display.DisplayObject;
	
	import mx.collections.ArrayCollection;
	import mx.logging.Log;
	
	public class Generator {
		public var parent:*;
		public var dataSource:*;
		public var children:Array = [];
		
		private var _callback:Function;
		private var _unknown:Function;
		private var __logger__:*;
		private var __iterative__:Boolean;
		
		public function Generator(datasource:*,callback:Function=null,unknown:Function=null,logger:*=null,doIterate:Boolean=true,iterative:Boolean=false) {
			var _this:* = this;
			this.children = [];
			this.parent = parent;
			this.dataSource = datasource;
			this._callback = callback;
			this._unknown = unknown;
			this.__logger__ = logger;
			this.__iterative__ = iterative;
			
			var gen:Generator;
			
			function iterate_over_array(ar:Array):void {
				if (doIterate) {
					for (i in ar) {
						anItem = ar[i];
						if (this.__logger__) {
							try { this.__logger__.write('Generator::iterate_over_array.1 --> ('+i+') of ('+ar.length+') anItem='+anItem.toString()); } catch (err:Error) {}
						}
						if (_this._callback is Function) {
							try { _this._callback(_this,anItem,ar,i) } catch (err:Error) {
								if (_this.__logger__) {
									try { _this.__logger__.write('Generator::iterate_over_array.2 --> '+err.toString()); } catch (err:Error) {}
								}
							}
							anItem = ar[i];
						}
						gen = new Generator(anItem,_this._callback);
						gen.parent = _this;
						_this.children.push(gen);
					}
				} else {
					if (_this._callback is Function) {
						try { _this._callback(_this,ar) } catch (err:Error) {
							if (_this.__logger__) {
								try { _this.__logger__.write('Generator::iterate_over_array.3 --> '+err.toString()); } catch (err:Error) {}
							}
						}
					}
				}
			}
			
			var i:String;
			if (this.dataSource is Array) {
				iterate_over_array(this.dataSource);
			} else if (this.dataSource is ArrayCollection) {
				iterate_over_array(this.dataSource.source);
			} else if (this.dataSource is DisplayObject) {
				try {
					iterate_over_array(this.dataSource.getChildren());
				} catch (err:Error) {}
			} else if (this.dataSource is String) {
				// Do Nothing - this is a leaf node.
			} else { // Walk the object...
				var keys:Array = [];
				try { keys = ObjectUtils.keys(this.dataSource); }
					catch (err:Error) {}
				if (keys.length == 0) {
					if (this._unknown is Function) {
						try { keys = ObjectUtils.keys(this._unknown(dataSource)); }
						catch (err:Error) {
							if (this.__logger__) {
								try { this.__logger__.write('Generator().0 --> '+err.toString()); } catch (err:Error) {}
							}
						}
					}
				}
				if (keys.length > 0) {
					var anItem:*;
					if ( (doIterate) && (this._callback is Function) ) {
						try { this._callback(this,this.dataSource) } catch (err:Error) {
							if (this.__logger__) {
								try { this.__logger__.write('Generator().1 --> '+err.toString()); } catch (err:Error) {}
							}
						}
					}
					var dp:* = this.dataSource;
					if (this.dataSource['children']) {
						dp = this.dataSource['children'];
						if ( (!doIterate) && (dp is Array) && (this._callback is Function) ) {
							try { this._callback(this,dp) } catch (err:Error) {
								if (this.__logger__) {
									try { this.__logger__.write('Generator().2 --> '+err.toString()); } catch (err:Error) {}
								}
							}
						}
					}
					for (i in dp) {
						anItem = dp[i];
						if ( (doIterate) && (this._callback is Function) ) {
							try { this._callback(this,anItem,dp,i) } catch (err:Error) {
								if (this.__logger__) {
									try { this.__logger__.write('Generator().3 --> '+err.toString()); } catch (err:Error) {}
								}
							}
							anItem = dp[i];
						}
						gen = new Generator(anItem,this._callback,this._unknown,doIterate);
						gen.parent = this;
						this.children.push(gen);
					}
				}
			}
		}
		
		public function next():void {
			if (this.__iterative__) {
				var i:int = -1;
			}
		}
		
		public function walk(callback:Function=null):void {
			if (callback is Function) {
				try { callback(this) } catch (err:Error) {
					if (this.__logger__) {
						try { this.__logger__.write('Generator::walk.1 --> '+err.toString()); } catch (err:Error) {}
					}
				}
			}
			if (this.children is Array) {
				for (var i:int = 0; i < this.children.length; i++) {
					this.children[i].walk(callback);
				}
			}
		}
	}
}