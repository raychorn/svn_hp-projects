package com.hp.asi.hpic4vc.ui.views.converged.model {

import com.vmware.core.model.DataObject;

[Bindable]
// Declares the type of object associated with this model.
// Types must be qualified with a namespace.
[Model(type="hpmodel:HPModel")]
/**
 * Data model for HP main object summary details view.
 */
public class HPSummaryDetails extends DataObject {
   // Maps a model property to the class field.
   // Note: Property and field names don't need to match.
   // Also, properties within your own type don't require a namespace.
   [Model(property="name")]

   public var name:String;
}
}