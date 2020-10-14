package com.hp.asi.hpic4vc.provider.dam.model;

import java.net.URI;

import com.hp.asi.hpic4vc.provider.dam.util.ModelObjectUriResolver;

/**
 * Base type for all resource objects that are managed and represented by this sample.
 */
public abstract class ModelObject {

    /** Namespace to add to the raw types */
    public static final String NAMESPACE = "hpmodel:";

    private final String id;

    private final Util util = new Util(this);
    
    public ModelObject(final String id) {
        this.id = id;
    }

    /**
     * Identifier of this object.
     */
    public String getId() {
       return id;
    }

    /**
     * Reference to utilities that operate on a specific instance.
     */
    public Util getUtil() {
       return util;
    }

    /**
     * Utilities associated with MobelObject.
     */
    public static class Util {
        private final ModelObject modelObject;
        private URI uri;

        /**
         * Constructor.
         */
        private Util(final ModelObject object) {
            modelObject = object;
        }

        /**
         * Returns the type of the ModelObject.
         */
        public String getType() {
            return (NAMESPACE + modelObject.getClass().getSimpleName());
        }

        /**
         * Returns a URI that identifies the associated ModelObject.
         */
        public URI getUri(final ModelObjectUriResolver resolver) {
            if (uri == null) {
            uri = resolver.createUri(this.getType(), modelObject.getId());
            }
            return uri;
        }
    }
}
