
package com.hp.asi.hpic4vc.provider.dam.provider;

import com.hp.asi.hpic4vc.provider.dam.model.HPModel;
import com.hp.asi.hpic4vc.provider.dam.model.ModelObject;
import com.hp.asi.hpic4vc.provider.dam.util.ModelObjectUriResolver;
import com.vmware.vise.data.query.DataService;
import com.vmware.vise.data.query.type;
import com.vmware.vise.data.uri.ResourceTypeResolverRegistry;
import com.vmware.vise.vim.data.VimObjectReferenceService;

/**
 * Adapts HPModel objects to make them available via the DataService.
 *
 * An instance of this adapter is exported as an osgi-service which leads to an
 * automatic registration of this adapter with the DataServiceExtensionRegistry.
 */
@type("hpmodel:HPModel") // declares the supported object types.
public class HPModelDamProvider extends BaseDamProvider {

    // Note that object types must be qualified with a namespace.
    // Only VMware Infrastructure internal types don't require a namespace.
    public static final String HPModel_TYPE   = ModelObject.NAMESPACE + HPModel.class.getSimpleName();


    /**
     * Constructor (arguments are Spring-injected, see bundle-context.xml).
     *
     * @param dataService
     *       Reference to the DataService that provides access to various resource objects
     *       in the system.
     *
     * @param typeResolverRegistry
     *       ResourceTypeResolverRegistry used to register ModelObjectUriResolver,
     *       the resolver for URIs handled by this adapter.
     *
     * @param objRefService
     *       VimObjectReferenceService used to register ModelObjectUriResolver,
     *       the resolver for URIs handled by this adapter.
     */
    public HPModelDamProvider(
         DataService dataService,
         ResourceTypeResolverRegistry typeResolverRegistry,
         VimObjectReferenceService objRefService) {       
        super(dataService, objRefService);
        
        try {
            // DataService uses this resolver to introspect scheme of the URI associated
            // with ModelObject. In doing so DataService will be able to direct data
            // requests targeting the types provided by this adapter.
            typeResolverRegistry.registerSchemeResolver(
               ModelObjectUriResolver.SCHEME,
               RESOURCE_RESOLVER);
        } catch (UnsupportedOperationException e) {
            log.warn("ModelObjectUriResolver registration failed.", e);
        }
    }


    @Override
    public void initializeRelationships () {
    }
   
    /**
     * Checks if the type is supported by this adapter.
     */
    @Override
    public boolean isSupportedType(String type) {
       return HPModel_TYPE.equals(type);
    }

    /**
     * Returns property of the specified ModelObject or UNSUPPORTED_PROPERTY_FLAG.
     */
    @Override
    public Object getProperty(ModelObject object, String propertyName) {
        if (object instanceof HPModel) {
            return getProperty((HPModel)object, propertyName);
        } 
        return UNSUPPORTED_PROPERTY_FLAG;
    }

    /**
     * Returns property of the specified Rack or UNSUPPORTED_PROPERTY_FLAG.
     */
    private Object getProperty(HPModel hpModel, String propertyName) {
        assert (hpModel != null);
        if ("name".equals(propertyName)) {
            return hpModel.getName();
        } 
        return UNSUPPORTED_PROPERTY_FLAG;
    }
}
