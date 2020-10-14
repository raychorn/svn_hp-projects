
package com.hp.asi.hpic4vc.provider.dam.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.asi.hpic4vc.provider.dam.model.ModelObject;
import com.hp.asi.hpic4vc.provider.dam.util.ConstraintHandler;
import com.hp.asi.hpic4vc.provider.dam.util.ModelObjectUriResolver;
import com.hp.asi.hpic4vc.provider.dam.util.ObjectStore;
import com.hp.asi.hpic4vc.provider.dam.util.RelationSet;
import com.hp.asi.hpic4vc.provider.dam.util.RelationSet.RelationDescriptor;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.vmware.vise.data.ResourceSpec;
import com.vmware.vise.data.query.DataProviderAdapter;
import com.vmware.vise.data.query.DataService;
import com.vmware.vise.data.query.QuerySpec;
import com.vmware.vise.data.query.RequestSpec;
import com.vmware.vise.data.query.Response;
import com.vmware.vise.data.query.ResultItem;
import com.vmware.vise.data.query.ResultSet;
import com.vmware.vise.data.query.ResultSpec;
import com.vmware.vise.usersession.UserSession;
import com.vmware.vise.vim.data.VimObjectReferenceService;

/**
 * Adapts ModelObjects to make them available via the DataService.
 * ModelObject instances are resources uniquely identified using a URI.
 * This adapter also introduces relations between ModelObjects and vSphere objects.
 *
 * An instance of this adapter is exported as an osgi-service which leads to an
 * automatic registration of this adapter with the DataServiceExtensionRegistry.
 */
public abstract class BaseDamProvider implements DataProviderAdapter {

    // Used to resolve URIs of resource objects provided by this adapter.
    protected static final ModelObjectUriResolver RESOURCE_RESOLVER =
         new ModelObjectUriResolver();

    // Internal flag for skipping properties not supported by this adapter
    protected static final Object UNSUPPORTED_PROPERTY_FLAG = new Object();

    private   final ConstraintHandler constraintHandler;
    protected final Log               log;
    protected final RelationSet       relationHolder;
    protected final ObjectStore       objectStore;

    /**
     * Constructor (arguments are Spring-injected, see bundle-context.xml).
     *
     * @param dataService
     *       Reference to the DataService that provides access to various resource objects
     *       in the system.
     *
     * @param objRefService
     *       VimObjectReferenceService used to register ModelObjectUriResolver,
     *       the resolver for URIs handled by this adapter.
     */
    public BaseDamProvider(
         final DataService dataService,
         final VimObjectReferenceService objRefService) {
        if (dataService == null || objRefService == null) {
            throw new IllegalArgumentException(
               "DamProvider constructor arguments must be non-null.");
        }
        this.log               = LogFactory.getLog(this.getClass());
        this.relationHolder    = new RelationSet();
        this.objectStore       = new ObjectStore();
        this.constraintHandler = new ConstraintHandler(dataService, objRefService, this);
        initializeRelationships();
    }

    public abstract void initializeRelationships ();
   
   
    /**
     * Checks if the type is supported by this adapter.
     */
    public abstract boolean isSupportedType(String type);
   

    /**
     * Returns property of the specified ModelObject or UNSUPPORTED_PROPERTY_FLAG.
     */
    public abstract Object getProperty(ModelObject object, String propertyName);

    @Override
    /**
     * Hook into vSphere client's DataService: all query requests for the types
     * supported by this adapter land here.  Depending on the request the
     * response will contain newly discovered objects, or properties on existing
     * objects, or relational data.
     */
    public Response getData(RequestSpec request) {
        if (request == null) {
            throw new IllegalArgumentException("request must be non-null.");
        }
        QuerySpec[] querySpecs = request.querySpec;

        List<ResultSet> results = new ArrayList<ResultSet>(querySpecs.length);
        for (QuerySpec qs : querySpecs) {
            ResultSet rs = processQuery(qs);
            results.add(rs);
        }
        Response response  = new Response();
        response.resultSet = results.toArray(new ResultSet[]{});
        return response;
    }
   
    public Object getUnsupportedPropertyObject() {
        return UNSUPPORTED_PROPERTY_FLAG;
    }
    
    /**
     * Returns all the objects of a type in the system.
     */
    protected Object[] getAllInstancesOfTypeInEnvironment(final String targetType, 
                                                         final int maxResultCount) {
        return this.constraintHandler.getAllInstancesOfTypeInEnvironment(targetType, maxResultCount);
    }
   
    /**
     * Process a single QuerySpec. Response to an invalid QuerySpec is a ResultSet
     * containing an exception.
     */
    protected ResultSet processQuery(QuerySpec qs) {
        ResultSet rs = new ResultSet();

        if (!validateQuerySpec(qs)) {
            Exception ex = new IllegalArgumentException(
               "BaseDamProvider.invalidQuerySpec");
            rs.error = ex;
            return rs;
        }

        // Process the constraint.
        List<ResultItem> items = constraintHandler.processConstraint(
            qs.resourceSpec.constraint,
            qs.resourceSpec.propertySpecs);

        // Prepare the ResultSet to return.
        // Note that totalMatchedObjectCount is only relevant when the query is
        // for discovering new objects.
        rs.totalMatchedObjectCount = (items != null) ? items.size() : 0;
        rs.items     = adjustResultItems(items, qs.resultSpec);
        rs.queryName = qs.name;
        return rs;
    }

    /**
     * Extract the range of items corresponding to the resultSpec's offset and
     * maxResultCount (case of paging).
     */
    private ResultItem[] adjustResultItems(
            List<ResultItem> items,
            ResultSpec resultSpec) {
       
        int maxResultCount = resultSpec.maxResultCount != null ? resultSpec.maxResultCount : -1;
        int returnedItemsLength = (maxResultCount == -1 || maxResultCount > items.size()) ?
            items.size() : maxResultCount;
        int offset     = (resultSpec.offset != null) ? resultSpec.offset : 0;
        int startIndex = (offset <= 1) ? 0 : (offset - 1);
        int endIndex   = startIndex + returnedItemsLength;
        List<ResultItem> adjustedList = null;

        try {
            adjustedList = items.subList(startIndex, endIndex);
        } catch(IndexOutOfBoundsException ex) {
            log.error("Error getting the range of ResultItems from " + startIndex
               + " to " + endIndex);
            adjustedList = items;
        }
        return adjustedList.toArray(new ResultItem[adjustedList.size()]);
    }


    /**
     * Validates the input query spec.
     *
     * @return
     *    Returns false if the query spec cannot be processed by this data adapter.
     */
    private boolean validateQuerySpec(QuerySpec qs) {
        if (qs == null) {
            return false;
        }

        ResourceSpec resourceSpec = qs.resourceSpec;
        if (resourceSpec == null) {
            return false;
        }
        return constraintHandler.validateConstraint(resourceSpec.constraint);
    }
    

    protected String getServerGuid () {
        UserSession userSession = SessionManagerImpl.getInstance().getUserSession();
        if (null != userSession && 
                null != userSession.serversInfo &&
                userSession.serversInfo.length > 0) {
            return userSession.serversInfo[0].serviceGuid;
        }
        
        return null;
    }


    public boolean supportsRelation(final String sourceType,
                                final String relation,
                                final String targetType) {
        return this.relationHolder.containsRelation(sourceType, relation, targetType);
    }

    public boolean supportsRelation(final RelationDescriptor relation) {
        return this.relationHolder.containsRelation(relation);
    }
   
    public boolean supportsInverseRelation(final String sourceType,
                                   final String relation,
                                   final String targetType) {
        return this.relationHolder.containsInverseRelation(sourceType, relation, targetType);
    }

    public boolean supportsInverseRelation(final RelationDescriptor relation) {
        return this.relationHolder.containsInverseRelation(relation);
    }
   
    public RelationDescriptor getInverseRelation(final RelationDescriptor relation) {
        return this.relationHolder.getInverseRelation(relation);
    }
   
    public Collection<ModelObject> getModelObjectsOfType(final String type) {
        return this.objectStore.getModelObjectsOfType(type);
    }
   
    public ModelObject getObjectByUid(final String uid) {
        return this.objectStore.getObjectByUid(uid);
    }

}
