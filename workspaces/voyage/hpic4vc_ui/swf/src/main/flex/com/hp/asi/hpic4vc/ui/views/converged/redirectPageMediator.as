/* Copyright 2012 VMware, Inc. All rights reserved. -- VMware Confidential */

package com.hp.asi.hpic4vc.ui.views.converged {

import flash.events.EventDispatcher;

/**
 * The mediator for the converged_gettingStarted view.
 */
public class redirectPageMediator extends EventDispatcher {

   private var _view:redirectPage;

   [View]
   /**
    * The mediator's view.
    */
   public function get view():redirectPage {
      return _view;
   }

   /** @private */
   public function set view(value:redirectPage):void {
      _view = value;
   }
}

}