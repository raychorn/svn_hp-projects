
package com.hp.asi.hpic4vc.provider.dam.model;

import com.hp.asi.hpic4vc.provider.dam.util.ModelObjectUriResolver;

/**
 * Root object to which relate all HP elements.
 * The HPModel is the parent object to place all objects under
 *  "HP Infrastructure".
*/
public class HPModel extends ModelObject {
    private static final ModelObjectUriResolver RESOURCE_RESOLVER =
            new ModelObjectUriResolver();
    
    private static final String MAIN_ID   = RESOURCE_RESOLVER.createId("hpinfrastructure", "main");
    private static final String MAIN_NAME = "HP Infrastructure";
    private static final HPModel INSTANCE = new HPModel(MAIN_ID, MAIN_NAME);
    

    private final String name;
    
    public static HPModel getInstance() {
        return INSTANCE;
    }
    
    private HPModel(final String id, final String name) {
       super(id);
       this.name = name;
    }
    
    public String getName() {
        return this.name;
    }

}