package com.hp.asi.hpic4vc.provider.dam.util;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.hp.asi.hpic4vc.provider.dam.model.ModelObject;
import com.hp.asi.hpic4vc.provider.dam.provider.BaseDamProvider;
import com.hp.asi.hpic4vc.provider.dam.util.RelationSet.RelationDescriptor;
import com.vmware.vise.data.Constraint;
import com.vmware.vise.data.PropertySpec;
import com.vmware.vise.data.ResourceSpec;
import com.vmware.vise.data.query.CompositeConstraint;
import com.vmware.vise.data.query.Conjoiner;
import com.vmware.vise.data.query.DataService;
import com.vmware.vise.data.query.ObjectIdentityConstraint;
import com.vmware.vise.data.query.OrderingCriteria;
import com.vmware.vise.data.query.OrderingPropertySpec;
import com.vmware.vise.data.query.PropertyValue;
import com.vmware.vise.data.query.QuerySpec;
import com.vmware.vise.data.query.RelationalConstraint;
import com.vmware.vise.data.query.RequestSpec;
import com.vmware.vise.data.query.Response;
import com.vmware.vise.data.query.ResultItem;
import com.vmware.vise.data.query.ResultSpec;
import com.vmware.vise.vim.data.VimObjectReferenceService;

public class ConstraintHandler {
    
    // Used to resolve URIs of resource objects provided by this adapter.
    protected static final ModelObjectUriResolver RESOURCE_RESOLVER =
          new ModelObjectUriResolver();
    
    private   final DataService dataService;
    private   final VimObjectReferenceService objRefService;
    private   final BaseDamProvider provider;
    
    
    public ConstraintHandler(final DataService dataService,
                             final VimObjectReferenceService refService,
                             final BaseDamProvider provider) {
        this.dataService   = dataService;
        this.objRefService = refService;
        this.provider      = provider;
    }
    
    /**
     * Validate the supplied constraint.
     */
    public boolean validateConstraint(Constraint constraint) {
       if (constraint == null) {
          return false;
       }
       // Currently the data adapter only supports:
       // 1. ObjectIdentityConstraint (i.e. get properties of an object)
       // 2. Simple Constraint (i.e. find objects of a type)
       // 3. RelationalConstraint (i.e. find relations of an object)
       // 4. CompositeConstraint joining 1 or more previous constraints.
       if (constraint instanceof ObjectIdentityConstraint) {
          String sourceType = objRefService.getResourceObjectType(
                ((ObjectIdentityConstraint)constraint).target);
          return provider.isSupportedType(sourceType);
       } else if (constraint instanceof CompositeConstraint) {
          CompositeConstraint cc = (CompositeConstraint)constraint;
          for (Constraint c : cc.nestedConstraints) {
             if (!validateConstraint(c)) {
                return false;
             }
          }
          return true;
       } else if (constraint instanceof RelationalConstraint) {
          RelationalConstraint rc = (RelationalConstraint)constraint;
          Object sourceObject =
                ((ObjectIdentityConstraint)rc.constraintOnRelatedObject).target;
          String sourceType = objRefService.getResourceObjectType(sourceObject);
          if (!provider.supportsRelation(sourceType, rc.relation, rc.targetType)) {
             return false;
          }
          return provider.isSupportedType(sourceType);
       } else if (Constraint.class.equals(constraint.getClass())) {
          return provider.isSupportedType(constraint.targetType);
       }
       // For any other constraint - mark the query as invalid.
       return false;
    }
    
    /**
     * Process a particular constraint and returns the data as per the specifics
     * of each type of constraint.
     */
    public List<ResultItem> processConstraint(
          Constraint constraint,
          PropertySpec[] propertySpecs) {
       List<ResultItem> items = null;
       if (constraint instanceof ObjectIdentityConstraint) {
          // Query is for getting properties of a specific ModelObject
          ObjectIdentityConstraint oic = (ObjectIdentityConstraint)constraint;
          items = processObjectIdentityConstraint(oic, propertySpecs);
       } else if (constraint instanceof CompositeConstraint) {
          CompositeConstraint cc = (CompositeConstraint)constraint;
          items = processCompositeConstraint(cc, propertySpecs);
       } else if (constraint instanceof RelationalConstraint) {
          RelationalConstraint rc = (RelationalConstraint)constraint;
          items = processRelationalConstraint(rc, propertySpecs);
       } else if (constraint.getClass().getSimpleName().equals(
             Constraint.class.getSimpleName())) {
          // Generic constraint, means we request all objects
          items = processSimpleConstraint(constraint, propertySpecs);
       }
       if (items == null) {
          items = new ArrayList<ResultItem>();
       }
       return items;
    }

    
    /**
     * Handles a RelationalConstraint with the following limitations :
     * <li> Value of constraintOnRelatedObject must be an ObjectIdentityConstraint.</li>
     * <li> The relation must be a property on the source i.e. the object specified
     *   in the constraintOnRelatedObject.</li>
     */
    private List<ResultItem> processRelationalConstraint(
          RelationalConstraint rc,
          PropertySpec[] propertySpecs) {
       assert (rc != null);
       assert (rc.constraintOnRelatedObject instanceof ObjectIdentityConstraint);
       List<ResultItem> returnItems = new ArrayList<ResultItem>();
       Object source = ((ObjectIdentityConstraint)rc.constraintOnRelatedObject).target;
       String sourceUid = objRefService.getUid(source);
       String sourceType = objRefService.getResourceObjectType(source);
       if (sourceUid == null) {
          return returnItems;
       }
       RelationDescriptor relDesc =
             new RelationDescriptor(sourceType, rc.relation, rc.targetType);
       Collection<Object> relatedItems = getRelatedItems(
             sourceUid,
             relDesc,
             rc.hasInverseRelation);
       if (relatedItems == null || relatedItems.isEmpty()) {
          return returnItems;
       }
       return createResultItems(relatedItems, propertySpecs);
    }

    /**
     * Handles a CompositeConstraint assuming that the conjoiner is 'OR' and delegating
     * the processing of each constraint to the appropriate handler.
     */
    private List<ResultItem> processCompositeConstraint(
          CompositeConstraint cc,
          PropertySpec[] propertySpecs) {
       List<ResultItem> items = new ArrayList<ResultItem>();
       for (Constraint constraint : cc.nestedConstraints) {
          List<ResultItem> individualItems = processConstraint(
                constraint,
                propertySpecs);
          items.addAll(individualItems);
       }
       return items;
    }

    /**
     * Returns all objects matching the targetType specified in the constraint.
     */
    private List<ResultItem> processSimpleConstraint(
          Constraint constraint,
          PropertySpec[] propertySpecs) {

       List<ResultItem> items = new ArrayList<ResultItem>();

       Collection<ModelObject> matchingTypeObjects = 
               provider.getModelObjectsOfType(constraint.targetType);
       
       for (ModelObject modelObject : matchingTypeObjects) {
           ResultItem ri = createResultItem(modelObject.getUtil().getUri(RESOURCE_RESOLVER),
                                            propertySpecs);
           if (ri != null) {
               items.add(ri);
           }
       }
       return items;
    }
    

    /**
     * Processes an ObjectIdentityConstraint and returns a ResultItem containing
     * the matching ModelObject and its requested properties.
     */
    private List<ResultItem> processObjectIdentityConstraint(
          ObjectIdentityConstraint constraint,
          PropertySpec[] propertySpecs) {

       List<ResultItem> items = new ArrayList<ResultItem>();
       URI ref = toURI(constraint.target);
       // ref is null if constraint.target is a Host

       if (ref != null) {
          ResultItem ri = createResultItem(ref, propertySpecs);
          if (ri != null) {
             items.add(ri);
          }
       }
       return items;
    }
    
    /**
     * Returns the items related to an object via a specific relation . A relation is
     * any property of a sub-type of ModelObject whose value is a sub-type or collection of
     * sub-types of ModelObject.
     */
    private Collection<Object> getRelatedItems(
          String objectUid,
          RelationDescriptor relDesc,
          boolean hasInverseRelation) {
       Collection<Object> relatedItems = new ArrayList<Object>();
       Object propertyValue = null;
       boolean getPropertySource = !hasInverseRelation;
       if (provider.supportsInverseRelation(relDesc)) {
          relDesc = provider.getInverseRelation(relDesc);
          getPropertySource = true;
       }
       if (getPropertySource) {
          propertyValue = getPropertySource(
                relDesc.name,
                objectUid,
                relDesc.targetType);
       } else {
          propertyValue = provider.getProperty(provider.getObjectByUid(objectUid), relDesc.name);
       }
       if ((propertyValue == null) || (propertyValue == provider.getUnsupportedPropertyObject())) {
          return relatedItems;
       }
       if (propertyValue.getClass().isArray()) {
          int length = Array.getLength(propertyValue);
          for (int index = 0; index < length; ++index) {
             Object relatedObject = Array.get(propertyValue, index);
             relatedItems.add(relatedObject);
          }
       } else {
          ModelObject relatedObject = toModelObject(propertyValue);
          if (relatedObject != null) {
             relatedItems.add(relatedObject);
          }
       }
       return relatedItems;
    }
    
    /**
     * Returns the object whose property with supplied propertyName has a value
     * matching propertyValueUid.
     */
    private ModelObject getPropertySource(
          String propertyName,
          String propertyValue,
          String sourceType) {
       assert (sourceType != null && propertyValue != null && propertyName != null);
       
       Collection<ModelObject> matchingTypeObjects = provider.getModelObjectsOfType(sourceType);
       
       for (ModelObject modelObject : matchingTypeObjects) {
           Object sourcePropertyValue = provider.getProperty(modelObject, propertyName);
           if (isOrContains(sourcePropertyValue, propertyValue)) {
              return modelObject;
           }
       }
       return null;
    }
    

    /**
     * Checks if the actualValue and comparableUid represent the same object or if
     * the actualValue is an array then looks for the comparableUid in the array.
     */
    private boolean isOrContains(Object actualValue, String comparableUid) {
       if (!actualValue.getClass().isArray()) {
          return representSameObject(actualValue, comparableUid);
       }
       int length = Array.getLength(actualValue);
       for (int index = 0; index < length; ++index) {
          Object relatedObject = Array.get(actualValue, index);
          if (representSameObject(relatedObject, comparableUid)) {
             return true;
          }
       }
       return false;
    }
    
    /**
     * Check if the supplied UID and objectRef represent the same resource.
     */
    private boolean representSameObject(Object objectRef, String comparableUid) {
       assert (objectRef != null);
       String objectUid = objRefService.getUid(objectRef);
       if (objectUid == null) {
          return objectUid == comparableUid;
       }
       return objectUid.equals(comparableUid);
    }
    
    /**
     * Extract names of all requested properties from the PropertySpecs.
     */
    private String[] getRequestedPropertyNames(PropertySpec[] pSpecs) {
       Set<String> properties = new HashSet<String>();
       if (pSpecs != null) {
          for (PropertySpec pSpec : pSpecs) {
             for (String property : pSpec.propertyNames) {
                properties.add(property);
             }
          }
       }
       return properties.toArray(new String[]{});
    }

    /**
     * Helper that cast objects to URI.
     */
    private URI toURI(Object target) {
       if (!(target instanceof URI)) {
          return null;
       }
       return (URI)target;
    }

    /**
     * Helper that cast objects to ModelObject.
     */
    private ModelObject toModelObject(Object target) {
       if (!(target instanceof ModelObject)) {
          return null;
       }
       return (ModelObject)target;
    }
    
    
    /**
     * Returns ResultItems for each ModelObject containing requested properties of
     * an object and a URI corresponding to the object.
     */
    private List<ResultItem> createResultItems(
          Collection<Object> objects,
          PropertySpec[] propertySpecs) {
        
       assert (objects != null && propertySpecs != null);
       
       List<ResultItem> items = new ArrayList<ResultItem>(objects.size());
       items.addAll(createResultItemsForModelObject(objects, propertySpecs));
       items.addAll(createResultItemsForNonModelObject(objects, propertySpecs));
       return items;
    }
    
    /**
     * Creates ResultItems for all objects in the supplied collection which
     * subclass ModelObject.
     */
    private List<ResultItem> createResultItemsForModelObject(
          Collection<Object> objects,
          PropertySpec[] propertySpecs) {
       List<ResultItem> items = new ArrayList<ResultItem>();
       for (Object object : objects) {
          if (!(object instanceof ModelObject)) {
             continue;
          }
          ResultItem item = createResultItem(
                ((ModelObject)object).getUtil().getUri(RESOURCE_RESOLVER),
                propertySpecs);
          items.add(item);
       }
       return items;
    }

    /**
     * Creates ResultItems for all objects in the supplied collection which do not
     * subclass ModelObject, i.e. HostSystem.
     */
    private List<ResultItem> createResultItemsForNonModelObject(
          Collection<Object> objects,
          PropertySpec[] propertySpecs) {
       List<ObjectIdentityConstraint> objConstraints =
             new ArrayList<ObjectIdentityConstraint>();
       for (Object object : objects) {
          if (object instanceof ModelObject) {
             continue;
          }
          ObjectIdentityConstraint objConstraint = new ObjectIdentityConstraint();
          objConstraint.target = object;
          objConstraints.add(objConstraint);
       }
       if (objConstraints.size() == 0) {
          return new ArrayList<ResultItem>();
       }
       // Use DataService for objects/properties this adapter doesn't handle
       return getResultItemsFromDataService(objConstraints, propertySpecs);
    }

    /**
     * Return data for objects/properties this adapter doesn't handle.
     *
     * Wraps up the supplied Constraints and PropertySpecs into a QuerySpec and requests
     * the data from DataService.
     */
    private List<ResultItem> getResultItemsFromDataService(
          List<ObjectIdentityConstraint> objConstraints,
          PropertySpec[] propertySpecs) {

       CompositeConstraint cnstrnt = new CompositeConstraint();
       cnstrnt.conjoiner = Conjoiner.OR;
       cnstrnt.nestedConstraints = objConstraints.toArray(
             new Constraint[objConstraints.size()]);

       QuerySpec qrySpc = new QuerySpec();
       qrySpc.resourceSpec = new ResourceSpec();
       qrySpc.resourceSpec.constraint = cnstrnt;
       qrySpc.resourceSpec.propertySpecs = propertySpecs;
       qrySpc.resultSpec = new ResultSpec();

       RequestSpec requestSpec = new RequestSpec();
       requestSpec.querySpec = new QuerySpec[]{qrySpc};

       Response results = dataService.getData(requestSpec);

       if (results == null || results.resultSet.length < 1) {
          return new ArrayList<ResultItem>();
       }
       return Arrays.asList(results.resultSet[0].items);
    }
    


    /**
     * Returns ResultItem for each ModelObject containing requested properties of
     * an object and a URI corresponding to the object.
     */
    protected ResultItem createResultItem(
          URI objectUri,
          PropertySpec[] propertySpecs) {
       ModelObject object = provider.getObjectByUid(RESOURCE_RESOLVER.getUid(objectUri));
       if (object == null) {
          return null;
       }
       String[] requestedProperties = getRequestedPropertyNames(propertySpecs);
       try {
          ResultItem ri = new ResultItem();
          ri.resourceObject = objectUri;
          ArrayList<PropertyValue> propValArr = new ArrayList<PropertyValue>(requestedProperties.length);

          for(int i = 0; i < requestedProperties.length; ++i) {
             String requestedProperty = requestedProperties[i];
             Object value = provider.getProperty(object, requestedProperty);

             // Queries may include properties unsupported by this adapter,
             // it is recommended to skip them instead of returning a null value.
             // But since null may be a valid value for supported properties we
             // use UNSUPPORTED_PROPERTY_FLAG to make the distinction.
             if (value != provider.getUnsupportedPropertyObject()) {
                PropertyValue pv  = new PropertyValue();
                pv.resourceObject = objectUri;
                pv.propertyName   = requestedProperty;
                pv.value          = value;
                propValArr.add(pv);
             }
          }
          ri.properties = propValArr.toArray(new PropertyValue[0]);
          return ri;
       } catch (Exception ex) {
          // log the error
          return null;
       }
    }
    
    /**
     * Returns all the objects of a type in the system.
     */
    public Object[] getAllInstancesOfTypeInEnvironment(final String targetType, 
                                                          final int maxResultCount) {
       // create QuerySpec
       QuerySpec qs       = createQuerySpec(targetType, maxResultCount);

       // get data from DataService
       RequestSpec qsSpec = new RequestSpec();
       qsSpec.querySpec   = new QuerySpec[]{qs};

       Response response  = dataService.getData(qsSpec);
       ResultItem[] items = response.resultSet[0].items;
       if (items == null) {
          return new Object[0];
       }
       Object[] objects = new Object[items.length];
       for (int index = 0; index < items.length; ++index) {
          objects[index] = items[index].resourceObject;
       }
       return objects;
    }
    
    private QuerySpec createQuerySpec(final String targetType,
                                      final int maxResultCount) {
        QuerySpec qs                  = new QuerySpec();
        qs.resourceSpec               = new ResourceSpec();
        qs.resourceSpec.constraint    = new Constraint();
        qs.resourceSpec.constraint.targetType = targetType;
        // request the name property
        PropertySpec pSpec            = new PropertySpec();
        pSpec.propertyNames           = new String[]{"name"};
        qs.resourceSpec.propertySpecs = new PropertySpec[]{pSpec};
        qs.resultSpec                 = new ResultSpec();
        qs.resultSpec.maxResultCount  = maxResultCount;
        // use default ordering
        qs.resultSpec.order           = new OrderingCriteria();
        qs.resultSpec.order.orderingProperties = new OrderingPropertySpec[0];
        return qs;
    }
}
